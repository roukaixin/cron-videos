package com.roukaixin.cronvideos.strategy.quark;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.Aria2ServerMapper;
import com.roukaixin.cronvideos.mapper.CloudShareMapper;
import com.roukaixin.cronvideos.mapper.CloudStorageAuthMapper;
import com.roukaixin.cronvideos.pojo.*;
import com.roukaixin.cronvideos.strategy.CloudDrive;
import com.roukaixin.cronvideos.utils.Aria2Utils;
import com.roukaixin.cronvideos.utils.FileUtils;
import com.roukaixin.cronvideos.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component(value = "cloud_drive_1")
public class QuarkStrategy implements CloudDrive {

    private final CloudStorageAuthMapper cloudStorageAuthMapper;

    private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;

    private final Aria2ServerMapper aria2ServerMapper;

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    private final CloudShareMapper cloudShareMapper;

    private final QuarkApi quarkApi;

    private final RedisTemplate<String, Object> redisTemplate;

    public QuarkStrategy(CloudStorageAuthMapper cloudStorageAuthMapper,
                         SmoothWeightedRoundRobin smoothWeightedRoundRobin,
                         Aria2ServerMapper aria2ServerMapper,
                         Aria2DownloadTasksMapper aria2DownloadTasksMapper,
                         QuarkApi quarkApi,
                         RedisTemplate<String, Object> redisTemplate,
                         CloudShareMapper cloudShareMapper) {
        this.cloudStorageAuthMapper = cloudStorageAuthMapper;
        this.smoothWeightedRoundRobin = smoothWeightedRoundRobin;
        this.aria2ServerMapper = aria2ServerMapper;
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
        this.quarkApi = quarkApi;
        this.redisTemplate = redisTemplate;
        this.cloudShareMapper = cloudShareMapper;
    }

    @Override
    public Integer download(CloudShare cloudShare, Media media) {
        Integer count = 0;
        // 根据分享 pwd_id 获取 stoken
        String shareToken = getShareToken(cloudShare.getShareId(), cloudShare.getId());
        if (!ObjectUtils.isEmpty(shareToken)) {
            // 分享文件列表
            List<FileInfo> sharepageFileList = getSharepageFileList(cloudShare, shareToken, "0", 1);
            // 分享链接获取的文件列表(过滤后)
            List<FileInfo> fileInfoList = videoList(sharepageFileList, cloudShare.getFileRegex());
            if (log.isInfoEnabled()) {
                log.info("分享文件列表 -> {}", JSONObject.toJSONString(fileInfoList));
            }
            Pattern pattern = Pattern.compile(media.getEpisodeRegex());
            List<Aria2DownloadTask> aria2DownloadTask = getAria2DownloadTask(media.getId());
            // 过滤掉已经下载的文件
            fileInfoList = downloadedFileExclude(fileInfoList, aria2DownloadTask, pattern);
            log.info("过滤后分享文件列表 : {}", JSONObject.toJSONString(fileInfoList));
            for (FileInfo info : fileInfoList) {
                // 在自己网盘中创建文件夹(如果没有),现在默认保存路径都在跟路经下的 `来自：分享`
                String saveFolderFid = (String) redisTemplate.opsForValue().get("target-save-folder-id");
                if (ObjectUtils.isEmpty(saveFolderFid)) {
                    saveFolderFid = getSaveFolderFid(media.getTitle(), media.getSeasonNumber());
                    redisTemplate.opsForValue().set("target-save-folder-id", saveFolderFid);
                }
                if (!saveFolderFid.isEmpty()) {
                    // 保存文件到自己网盘,并返回文件ID(fid),调用保存接口返回一个任务id(task_id),在根据任务id获取文件id
                    String saveTaskId = saveFile(info, cloudShare.getShareId(), shareToken, saveFolderFid);
                    if (!saveTaskId.isEmpty()) {
                        // 通过 task_id 获取需要下载的文件id
                        String downloadFid = getDownloadFid(saveTaskId, 0, 10);
                        if (!downloadFid.isEmpty()) {
                            // 发送失败不考虑先,gid 为空就是失败
                            String gid = sendDownload(downloadFid, info, media, aria2DownloadTask, pattern);
                            log.info("提交到 aria2 下载了, 返回 GID 为 `{}`", gid);
                            count++;
                            // 删除原文件
                            String taskId = deleteSourceFile(downloadFid);
                            if (!taskId.isEmpty()) {
                                // 判断是否删除成功
                                boolean deleteFlag = deleteSourceFileTask(taskId, 0, 10);
                                log.info("删除网盘原文件 -> {}", deleteFlag);
                            }
                        } else {
                            //
                            log.error("保存文件到网盘,但是无法获取到 task_id,所以没办法删除文件");
                        }
                    }

                }
                ThreadUtils.sleep(TimeUnit.MILLISECONDS, 500);
            }
            // 删除redis数据
            redisTemplate.delete("target-save-folder-id");
            redisTemplate.delete("cookies");
        }
        return count;
    }

    private List<Aria2DownloadTask> getAria2DownloadTask(Long mediaId) {
        return aria2DownloadTasksMapper.selectList(
                Wrappers.<Aria2DownloadTask>lambdaQuery().eq(Aria2DownloadTask::getMediaId, mediaId)
        );
    }

    private List<FileInfo> downloadedFileExclude(List<FileInfo> fileInfoList,
                                                 List<Aria2DownloadTask> aria2DownloadTasks,
                                                 Pattern pattern) {
        Map<Integer, Long> episodeNumberMap = aria2DownloadTasks.stream()
                .filter(e -> !e.getStatus().equals(3) && !ObjectUtils.isEmpty(e.getSize()))
                .collect(Collectors.toMap(Aria2DownloadTask::getEpisodeNumber, Aria2DownloadTask::getSize));
        return fileInfoList
                .stream()
                .filter(e -> {
                    String episodeRegex = FileUtils.episodeRegex(pattern, e.getFileName());
                    if (ObjectUtils.isEmpty(episodeRegex)) {
                        return false;
                    }
                    Integer episodeNumber = FileUtils.getEpisodeNumber(episodeRegex);
                    Long size = episodeNumberMap.get(episodeNumber);
                    return ObjectUtils.isEmpty(size) || e.getSize() > size;
                })
                .toList();
    }

    // 获取 stoken(分享token)
    private String getShareToken(String shareId, Long id) {
        String response = quarkApi.shareSharepageToken(shareId);
        String shareToken = "";
        if (!ObjectUtils.isEmpty(response)) {
            JSONObject responseJson = JSONObject.parseObject(response);
            if (responseJson.getInteger("status").equals(200)) {
                shareToken = responseJson.getObject("data", JSONObject.class)
                        .getString("stoken");
            } else {
                // 连接已经失效
                CloudShare cloudShare = new CloudShare();
                cloudShare.setId(id);
                cloudShare.setIsLapse(1);
                cloudShare.setLapseCause(response);
                cloudShareMapper.updateById(cloudShare);
            }
        }
        return shareToken;
    }


    private List<FileInfo> videoList(List<FileInfo> fileInfoList, String fileRegex) {
        Pattern p = Pattern.compile(fileRegex);
        // 分享链接获取的文件列表(过滤掉不是视频的文件)
        fileInfoList =
                fileInfoList
                        .stream()
                        .peek(e -> e.setFileName(e.getFileName().strip()))
                        .filter(fileInfo -> p.matcher(fileInfo.getFileName()).matches())
                        .sorted(Comparator.comparing(FileInfo::getFileName, Collator.getInstance()))
                        .toList();
        return fileInfoList;
    }

    private List<FileInfo> getSharepageFileList(CloudShare cloudShare, String stoken, String pdirFid, Integer page) {
        String response = quarkApi.shareSharepageDetail(cloudShare.getShareId(), stoken, pdirFid, page);
        List<FileInfo> fileInfoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(response)) {
            JSONObject responseJson = JSONObject.parseObject(response);
            if (responseJson.getInteger("status").equals(200)) {
                JSONObject data = responseJson.getJSONObject("data");
                List<FileInfo> dataList = data.getList("list", FileInfo.class);
                for (FileInfo info : dataList) {
                    if (info.getCategory().equals(0)) {
                        if (!ObjectUtils.isEmpty(cloudShare.getExcludedDirs()) &&
                                cloudShare.getExcludedDirs().contains(info.getFileName())) {
                            continue;
                        }
                        // 如果是目录,递归调用获取文件
                        fileInfoList.addAll(getSharepageFileList(cloudShare, stoken, info.getFid(), page));
                    } else {
                        fileInfoList.add(info);
                    }

                }
                Integer total = responseJson.getJSONObject("metadata").getInteger("_total");
                if (total > page * 100) {
                    fileInfoList.addAll(getSharepageFileList(cloudShare, stoken, pdirFid, page + 1));
                }
            }
        }
        return fileInfoList;
    }

    // 获取保存目录id(在自己网盘下,需要 cookie 访问)
    private String getSaveFolderFid(String title, Integer seasonNumber) {
        // 以下请求都需要 cookie
        String response = quarkApi.fileSort("0", getCookies());
        JSONObject responseJson = JSONObject.parseObject(response);
        String fid = "";
        if (responseJson.getInteger("status").equals(200)) {
            List<FileInfo> list = responseJson.getJSONObject("data").getList("list", FileInfo.class);
            boolean exit = false;
            for (FileInfo fileInfo : list) {
                if (fileInfo.getFileName().equals("来自：分享")) {
                    exit = true;
                    fid = fileInfo.getFid();
                    break;
                }
            }
            if (exit) {
                response = quarkApi.fileSort(fid, getCookies());
                responseJson = JSONObject.parseObject(response);
                if (responseJson.getInteger("status").equals(200)) {
                    list = responseJson.getJSONObject("data").getList("list", FileInfo.class);
                    exit = false;
                    for (FileInfo fileInfo : list) {
                        if (fileInfo.getFileName().equals(title)) {
                            exit = true;
                            fid = fileInfo.getFid();
                            break;
                        }
                    }
                    if (exit) {
                        // title 存在,不知道 season 存不存在.判断 season 是否存在
                        response = quarkApi.fileSort(fid, getCookies());
                        responseJson = JSONObject.parseObject(response);
                        if (responseJson.getInteger("status").equals(200)) {
                            list = responseJson.getJSONObject("data").getList("list", FileInfo.class);
                            exit = false;
                            for (FileInfo fileInfo : list) {
                                if (fileInfo.getFileName().equals(String.format("Season %02d", seasonNumber))) {
                                    exit = true;
                                    fid = fileInfo.getFid();
                                    break;
                                }
                            }
                            if (!exit) {
                                fid = mkdirFile(fid, String.format("Season %02d", seasonNumber));
                            }
                        }
                    } else {
                        // 创建 title
                        String file = mkdirFile(fid, title);
                        if (!file.isEmpty()) {
                            // 创建 season
                            fid = mkdirFile(file, String.format("Season %02d", seasonNumber));
                        }
                    }
                }
            }
        }
        return fid;
    }

    // 创建文件夹
    private String mkdirFile(String pdirFid, String fileName) {
        String fid = "";
        String response = quarkApi.file(pdirFid, fileName, getCookies());
        if (response != null && !response.isEmpty()) {
            JSONObject responseJson = JSONObject.parseObject(response);
            if (responseJson.getInteger("status").equals(200)) {
                fid = responseJson.getJSONObject("data").getString("fid");
            }
        }
        return fid;
    }

    // 保存文件
    private String saveFile(FileInfo fileInfo,
                            String pwdId,
                            String stoken,
                            String saveFid) {
        // 一个一个保存,防止网盘容量不足
        String response = quarkApi.shareSharepageSave(pwdId, stoken, saveFid, Collections.singletonList(fileInfo.getFid()), getCookies());
        return getTaskId(response);
    }

    private MultiValueMap<String, String> getCookies() {
        String cookies = (String) redisTemplate.opsForValue().get("cookies");
        MultiValueMap<String, String> cookiesMap = new LinkedMultiValueMap<>();
        if (ObjectUtils.isEmpty(cookies)) {
            CloudStorageAuth cloudStorageAuth = cloudStorageAuthMapper.selectOne(
                    Wrappers.<CloudStorageAuth>lambdaQuery().eq(CloudStorageAuth::getProvider, 1));
            if (!ObjectUtils.isEmpty(cloudStorageAuth)) {
                cookies = cloudStorageAuth.getCookie();
                redisTemplate.opsForValue().set("cookies", cookies);
            } else {
                return cookiesMap;
            }
        }

        for (String s : cookies.split(";")) {
            String[] split = s.split("=", 2);
            cookiesMap.add(split[0], split[1]);
        }
        return cookiesMap;
    }

    private String getDownloadFid(String taskId, int retryCount, final int maxRetries) {
        ThreadUtils.sleep(TimeUnit.MILLISECONDS, 1500);
        // 避免递归过多，达到最大重试次数时退出
        if (retryCount >= maxRetries) {
            return "";
        }
        String response = quarkApi.task(taskId, retryCount, getCookies());
        String downloadFid = "";
        if (response != null && !response.isEmpty()) {
            JSONObject responseJson = JSONObject.parseObject(response);
            if (responseJson.getInteger("status").equals(200)) {
                JSONObject data = responseJson
                        .getJSONObject("data");
                if (data.getInteger("status").equals(2)) {
                    downloadFid = data
                            .getJSONObject("save_as")
                            .getJSONArray("save_as_top_fids")
                            .getString(0);
                } else {
                    return getDownloadFid(taskId, retryCount + 1, maxRetries);
                }
            }
        }
        return downloadFid;
    }

    // 发生请求 aria2 下载
    private String sendDownload(String fid,
                                FileInfo fileinfo,
                                Media media,
                                List<Aria2DownloadTask> aria2DownloadTasks,
                                Pattern p) {
        String gid = "";
        // 获取下载链接
        JSONObject downloadInfo = getDownloadUrl(fid);
        String downloadUrl = downloadInfo.getString("download_url");
        String cookie = downloadInfo.getString("cookie");
        if (log.isDebugEnabled()) {
            log.debug("文件名 -> {}", fileinfo.getFileName());
            log.debug("直链下载地址 -> {}", downloadUrl);
            log.debug("cookie -> {}", cookie);
        }
        if (!downloadUrl.isEmpty() && !cookie.isEmpty()) {
            // 获取下载直链返回 `__puus=` cookie(下载需要)
            Long aria2ServerId = smoothWeightedRoundRobin.getAria2ServerId();
            if (log.isDebugEnabled()) {
                log.debug("aria2 数据库 id -> {}", aria2ServerId);
            }
            if (!ObjectUtils.isEmpty(aria2ServerId)) {
                Aria2Server aria2Server = aria2ServerMapper.selectById(aria2ServerId);
                if (!ObjectUtils.isEmpty(aria2Server)) {
                    // 根据正则表达式匹配出来的结果
                    String regex = FileUtils.episodeRegex(p, fileinfo.getFileName());
                    if (!ObjectUtils.isEmpty(regex)) {
                        String dir = Aria2Utils.getDir(
                                aria2Server.getIp(),
                                aria2Server.getPort(),
                                aria2Server.getSecret()
                        );
                        String aria2SavePath = getAria2SavePath(dir, media.getTitle(), media.getSeasonNumber());
                        // 保存在数据库中
                        Integer episodeNumber = FileUtils.getEpisodeNumber(regex);
                        String mimeType = downloadInfo.getString("format_type");
                        String out = FileUtils.getName(
                                media.getTitle(),
                                media.getSeasonNumber(),
                                media.getTotalEpisodes(),
                                fileinfo.getFileName(),
                                regex,
                                mimeType
                        );
                        Aria2DownloadTask aria2DownloadTask =
                                aria2DownloadTasks
                                        .stream()
                                        .filter(e -> e.getEpisodeNumber().equals(episodeNumber))
                                        .findFirst()
                                        .orElseGet(Aria2DownloadTask::new);
                        aria2DownloadTask.setMediaId(media.getId());
                        aria2DownloadTask.setAria2ServiceId(aria2ServerId);
                        aria2DownloadTask.setEpisodeNumber(episodeNumber);
                        aria2DownloadTask.setSavePath(aria2SavePath);
                        aria2DownloadTask.setGid(gid);
                        aria2DownloadTask.setSize(fileinfo.getSize());
                        aria2DownloadTask.setStatus(0);
                        aria2DownloadTask.setOutName(out);
                        aria2DownloadTasksMapper.insertOrUpdate(aria2DownloadTask);
                        // 下载之前先判断任务是否在等待下载
                        if (!taskIsWaiting(aria2Server, aria2DownloadTask.getGid())) {
                            // 现保存任务到数据库,防止 aria2 发生数据过来无法判断
                            String response = aria2Download(
                                    aria2Server, downloadUrl, aria2SavePath, out, cookie
                            );
                            gid = JSONObject.parseObject(response).getString("result");
                            // 暂时不考虑发生到aria2失败,更新数据
                            aria2DownloadTask.setGid(gid);
                            aria2DownloadTasksMapper.updateById(aria2DownloadTask);
                        } else {
                            gid = aria2DownloadTask.getGid();
                        }
                    }
                }
            } else {
                log.error("获取 aria2 服务器失败");
            }
        }
        return gid;
    }

    private JSONObject getDownloadUrl(String fid) {
        String downloadUrl = "";
        String formatType = "";
        // cookie 和 responseBody
        Map<String, String> response = quarkApi.download(Collections.singletonList(fid), getCookies());
        String responseBody = response.get("response");
        if (!responseBody.isEmpty()) {
            JSONObject responseJson = JSONObject.parseObject(responseBody);
            if (responseJson.getInteger("status").equals(200) && responseJson.getInteger("code").equals(0)) {
                JSONObject downloadBodyData = responseJson.getJSONArray("data").getJSONObject(0);
                downloadUrl = downloadBodyData.getString("download_url");
                formatType = downloadBodyData.getString("format_type");
            }
        }
        return JSONObject.of(
                "cookie", response.get("cookies"),
                "download_url", downloadUrl,
                "format_type", formatType
        );
    }

    private String getAria2SavePath(String dir, String title, Integer season) {
        return dir.isEmpty() ?
                title + File.separator +
                        String.format("Season %02d", season) :
                dir + File.separator + title + File.separator +
                        String.format("Season %02d", season);
    }


    private String aria2Download(Aria2Server aria2Server, String downloadUrl, String aria2DownloadDir, String out, String cookies) {
        // 调用 aria2 下载
        JSONArray params = new JSONArray();
        params.add("token:" + aria2Server.getSecret());
        params.add(JSONArray.of(downloadUrl));
        params.add(JSONObject.of(
                "dir", aria2DownloadDir,
                "split", "16",
                "max-connection-per-server", 16,
                "out", out,
                "allow-overwrite", true,
                "header", JSONArray.of("Cookie:" + cookies)
        ));
        String s = Aria2Utils.addUri(aria2Server.getIp(), aria2Server.getPort(), params.toJSONString());
        log.info("发送下载结果 {}", s);
        return s;
    }

    private String deleteSourceFile(String fid) {
        String response = quarkApi.fileDelete(Collections.singletonList(fid), getCookies());
        return getTaskId(response);
    }

    private String getTaskId(String response) {
        String taskId = "";
        if (response != null && !response.isEmpty()) {
            JSONObject responseJson = JSONObject.parseObject(response);
            if (responseJson.getInteger("status").equals(200) && responseJson.getInteger("code").equals(0)) {
                taskId = responseJson.getJSONObject("data").getString("task_id");
            }
        }
        return taskId;
    }

    private boolean deleteSourceFileTask(String taskId, int retryCount, final int maxRetries) {
        ThreadUtils.sleep(TimeUnit.SECONDS, 1);
        boolean flag = false;
        // maxRetries 最大重试次数，防止无限递归
        // 避免递归过多，达到最大重试次数时退出
        if (retryCount >= maxRetries) {
            return false;
        }
        String response = quarkApi.task(taskId, retryCount, getCookies());
        if (response != null && !response.isEmpty()) {
            JSONObject responseJson = JSONObject.parseObject(response);
            if (responseJson.getInteger("status").equals(200)) {
                JSONObject data = responseJson
                        .getJSONObject("data");
                if (data.getInteger("status").equals(2)) {
                    flag = true;
                } else {
                    return deleteSourceFileTask(taskId, retryCount + 1, maxRetries);
                }
            }
        }
        return flag;

    }

    private boolean taskIsWaiting(Aria2Server aria2Server, String gid) {
        boolean wait = false;
        if (!ObjectUtils.isEmpty(gid)) {
            JSONArray params = new JSONArray();
            params.add("token:" + aria2Server.getSecret());
            params.add(gid);
            params.add(new ArrayList<>().add("status"));
            String tellStatus = Aria2Utils.tellStatus(aria2Server.getIp(), aria2Server.getPort(), params.toJSONString());
            if (!ObjectUtils.isEmpty(tellStatus)) {
                JSONObject result = JSONObject.parseObject(tellStatus).getJSONObject("result");
                if (!ObjectUtils.isEmpty(result)) {
                    String status = result.getString("status");
                    if (!ObjectUtils.isEmpty(status) && status.equals("waiting")) {
                        wait = true;
                    }
                }
            }
        }
        return wait;
    }
}
