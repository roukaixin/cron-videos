package com.roukaixin.cronvideos.strategy.quark;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.enums.CloudShareProviderEnum;
import com.roukaixin.cronvideos.enums.MediaResolutionEnum;
import com.roukaixin.cronvideos.enums.MediaTypeEnum;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.Aria2ServerMapper;
import com.roukaixin.cronvideos.mapper.CloudShareMapper;
import com.roukaixin.cronvideos.mapper.CloudStorageAuthMapper;
import com.roukaixin.cronvideos.pojo.*;
import com.roukaixin.cronvideos.strategy.CloudDrive;
import com.roukaixin.cronvideos.strategy.domain.FileInfo;
import com.roukaixin.cronvideos.strategy.domain.MediaMetadata;
import com.roukaixin.cronvideos.utils.Aria2Utils;
import com.roukaixin.cronvideos.utils.FileUtils;
import com.roukaixin.cronvideos.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.Collator;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
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
    public List<FileInfo> getFileList(Media media, CloudShare cloudShare) {
        List<FileInfo> fileInfos = new ArrayList<>();
        // 根据分享 pwd_id 获取 stoken
        String shareToken = getShareToken(cloudShare.getShareId(), cloudShare.getId());
        if (!ObjectUtils.isEmpty(shareToken)) {
            List<QuarkFileInfo> sharepageFileList = getSharepageFileList(cloudShare, shareToken, "0", 1);
            // 在自己网盘中创建文件夹(如果没有),现在默认保存路径都在跟路经下的 `来自：分享`
            String saveFolderFid = (String) redisTemplate.opsForValue().get("target-save-folder-id");
            if (ObjectUtils.isEmpty(saveFolderFid)) {
                saveFolderFid = getSaveFolderFid(media.getName(), media.getSeason());
                redisTemplate.opsForValue().set("target-save-folder-id", saveFolderFid);
            }
            Pattern episodeNumber = Pattern.compile(cloudShare.getEpisodeRegex());
            for (QuarkFileInfo filterVideo : getFilterVideos(sharepageFileList, cloudShare.getFileRegex())) {
                FileInfo build = FileInfo
                        .builder()
                        .provider(CloudShareProviderEnum.quark)
                        .fileId(filterVideo.getFid())
                        .fileName(filterVideo.getFileName())
                        .shareId(cloudShare.getShareId())
                        .shareToken(shareToken)
                        .size(filterVideo.getSize())
                        .parentFileId(saveFolderFid)
                        .mediaMetadata(
                                MediaMetadata.builder()
                                        .width(filterVideo.getVideoWidth())
                                        .height(filterVideo.getVideoHeight()).build()
                        )
                        .episodeNumber(
                                getEpisodeNumber(FileUtils.getEpisodeNumber(media.getType(), episodeNumber, filterVideo.getFileName()), media.getStartEpisode())
                        )
                        .build();
                fileInfos.add(build);
            }
            redisTemplate.delete("target-save-folder-id");
        }
        // 过滤正则匹配失败的数据
        return fileInfos.stream()
                .sorted(Comparator.comparingInt(FileInfo::getEpisodeNumber))
                .filter(e -> e.getEpisodeNumber() < 0)
                .toList();
    }

    // 获取 stoken(分享token)
    private String getShareToken(String shareId, Long id) {
        Map<String, Object> response = quarkApi.shareSharepageToken(shareId);
        String shareToken = "";
        String data = (String) response.get("data");
        if (response.get("is_ok").equals(false)) {
            // 连接已经失效
            CloudShare cloudShare = new CloudShare();
            cloudShare.setId(id);
            cloudShare.setIsLapse(1);
            cloudShare.setLapseCause(data);
            cloudShareMapper.updateById(cloudShare);
            log.info("分享链接已经失效 -> {} -> {}", shareId, data);
        } else {
            JSONObject responseJson = JSONObject.parseObject(data);
            if (responseJson.getInteger("status").equals(200)) {
                shareToken = responseJson.getObject("data", JSONObject.class)
                        .getString("stoken");
            }
        }
        return shareToken;
    }

    private List<QuarkFileInfo> getSharepageFileList(CloudShare cloudShare,
                                                     String stoken,
                                                     String pdirFid,
                                                     Integer page) {
        String response = quarkApi.shareSharepageDetail(cloudShare.getShareId(), stoken, pdirFid, page);
        List<QuarkFileInfo> fileInfoList = new ArrayList<>();
        if (!ObjectUtils.isEmpty(response)) {
            JSONObject responseJson = JSONObject.parseObject(response);
            if (responseJson.getInteger("status").equals(200)) {
                JSONObject data = responseJson.getJSONObject("data");
                List<QuarkFileInfo> dataList = data.getList("list", QuarkFileInfo.class);
                for (QuarkFileInfo info : dataList) {
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

    /**
     * 获取保存目录id(在自己网盘下,需要 cookie 访问)
     *
     * @param name   媒体名称
     * @param season 季
     * @return 文件夹目录 id
     */
    private String getSaveFolderFid(String name, Integer season) {
        // 以下请求都需要 cookie
        String response = quarkApi.fileSort("0", getCookies());
        JSONObject responseJson = JSONObject.parseObject(response);
        String fid = "";
        if (responseJson.getInteger("status").equals(200)) {
            List<QuarkFileInfo> list = responseJson.getJSONObject("data").getList("list", QuarkFileInfo.class);
            boolean exit = false;
            for (QuarkFileInfo fileInfo : list) {
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
                    list = responseJson.getJSONObject("data").getList("list", QuarkFileInfo.class);
                    exit = false;
                    for (QuarkFileInfo fileInfo : list) {
                        if (fileInfo.getFileName().equals(name)) {
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
                            list = responseJson.getJSONObject("data").getList("list", QuarkFileInfo.class);
                            exit = false;
                            for (QuarkFileInfo fileInfo : list) {
                                if (fileInfo.getFileName().equals(String.format("Season %02d", season))) {
                                    exit = true;
                                    fid = fileInfo.getFid();
                                    break;
                                }
                            }
                            if (!exit) {
                                fid = mkdirFile(fid, String.format("Season %02d", season));
                            }
                        }
                    } else {
                        // 创建 title
                        String file = mkdirFile(fid, name);
                        if (!file.isEmpty()) {
                            // 创建 season
                            fid = mkdirFile(file, String.format("Season %02d", season));
                        }
                    }
                }
            }
        }
        return fid;
    }

    /**
     * 创建文件夹（自己网盘下）
     *
     * @param pdirFid  父目录 id
     * @param fileName 文件夹名
     * @return 创建成功后返回的 fid(文件id)
     */
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

    /**
     * 过滤视频
     *
     * @param sharepageFileList 分享文件列表
     * @param fileRegex file 正则
     * @return 过滤后的视频
     */
    private List<QuarkFileInfo> getFilterVideos(List<QuarkFileInfo> sharepageFileList, String fileRegex) {
        // 过滤出只是 video 类别的文件
        List<QuarkFileInfo> videoList = sharepageFileList
                .stream()
                .peek(e -> e.setFileName(e.getFileName().strip()))
                .filter(e -> "video".equals(e.getObjCategory()))
                .sorted(Comparator.comparing(QuarkFileInfo::getFileName, Collator.getInstance()))
                .toList();
        if (log.isInfoEnabled()) {
            log.info("{} -> 文件大小：{} -> 文件过滤之前 -> {}", CloudShareProviderEnum.quark, videoList.size(), JSON.toJSONString(videoList));
        }
        // 过滤出大于 1080p 的媒体视频
        videoList = videoList
                .stream()
                .filter(e -> MediaResolutionEnum.shortNameNumber(e.getVideoWidth(), e.getVideoHeight()) > 1080)
                .toList();
        // 根据 fileRegex 正则表达式过滤
        if (!ObjectUtils.isEmpty(fileRegex)) {
            Pattern p = Pattern.compile(fileRegex);
            // 分享链接获取的文件列表(过滤掉不是视频的文件)
            videoList = videoList
                    .stream()
                    .filter(fileInfo -> p.matcher(fileInfo.getFileName()).matches())
                    .toList();
        }
        if (log.isInfoEnabled()) {
            log.info("{} -> 文件大小：{} -> 文件过滤之后 -> {}", CloudShareProviderEnum.quark, videoList.size(), JSON.toJSONString(videoList));
        }
        return videoList;
    }

    /**
     * 获取媒体视频的集数
     * @param source 匹配出来的集数
     * @param begin 开始集数
     * @return 实际媒体集数
     */
    private Integer getEpisodeNumber(Integer source, Integer begin) {
        if (ObjectUtils.isEmpty(begin)) {
            return source;
        }
        return source - begin + 1;
    }

    @Override
    public int download(Media media, FileInfo filterVideo) {
        int count = 0;
        // 转存
        String taskId = transferFile(filterVideo);
        if (StringUtils.hasText(taskId)) {
            // 获取转存后的文件id（fid）。通过 task_id 获取需要下载的文件id
            String downloadFid = getDownloadFileId(taskId, 0, 10);
            if (StringUtils.hasText(downloadFid)) {
                // 获取文件的直链下载地址
                Map<String, String> downloadInfo = getDownloadUrl(downloadFid);
                String downloadUrl = downloadInfo.getOrDefault("download_url", null);
                String cookie = downloadInfo.getOrDefault("cookie", null);
                String formatType = downloadInfo.getOrDefault("format_type", null);
                if (log.isDebugEnabled()) {
                    log.debug("文件名 -> {}", filterVideo.getFileName());
                    log.debug("直链下载地址 -> {}", downloadUrl);
                    log.debug("cookie -> {}", cookie);
                }
                if (StringUtils.hasText(downloadUrl) && StringUtils.hasText(cookie) && StringUtils.hasText(formatType)) {
                    // 发送下载
                    String gid = sendDownload(downloadUrl, cookie, formatType, media, filterVideo);
                    log.info("提交到 aria2 下载了, 返回 GID 为 `{}`", gid);
                    if (StringUtils.hasText(gid)) {
                        // 下载成功
                        count++;
                        // 删除原文件
                        taskId = deleteSourceFile(downloadFid);
                        if (!taskId.isEmpty()) {
                            // 判断是否删除成功
                            boolean deleteFlag = deleteSourceFileTask(taskId, 0, 10);
                            log.info("删除网盘原文件 -> {}", deleteFlag);
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * 转存
     *
     * @param videoInfo 文件信息
     * @return 任务id
     */
    private String transferFile(FileInfo videoInfo) {
        // 一个一个保存,防止网盘容量不足
        String response = quarkApi.shareSharepageSave(
                videoInfo.getShareId(),
                videoInfo.getShareToken(),
                videoInfo.getParentFileId(),
                Collections.singletonList(videoInfo.getFileId()),
                getCookies()
        );
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

    /**
     * 获取转存之后的文件 id
     *
     * @param taskId     任务id
     * @param retryCount 当前重试数次
     * @param maxRetries 重试最大数次
     * @return 文件 id
     */
    private String getDownloadFileId(String taskId, int retryCount, final int maxRetries) {
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
                    return getDownloadFileId(taskId, retryCount + 1, maxRetries);
                }
            }
        }
        return downloadFid;
    }

    private Map<String, String> getDownloadUrl(String fid) {
        Map<String, String> map = new HashMap<>();
        // cookie 和 responseBody
        Map<String, String> response = quarkApi.download(Collections.singletonList(fid), getCookies());
        String responseBody = response.get("response");
        map.put("cookie", response.get("cookies"));
        if (!responseBody.isEmpty()) {
            JSONObject responseJson = JSONObject.parseObject(responseBody);
            if (responseJson.getInteger("status").equals(200) && responseJson.getInteger("code").equals(0)) {
                JSONObject downloadBodyData = responseJson.getJSONArray("data").getJSONObject(0);
                map.put("download_url", downloadBodyData.getString("download_url"));
                map.put("format_type", downloadBodyData.getString("format_type"));
            }
        }
        return map;
    }

    private String sendDownload(String url, String cookie, String mimeType, Media media, FileInfo fileInfo) {
        String gid = "";
        // 获取下载任务
        Aria2DownloadTask aria2DownloadTask = aria2DownloadTasksMapper.selectOne(
                Wrappers.<Aria2DownloadTask>lambdaQuery()
                        .eq(Aria2DownloadTask::getMediaId, media.getId())
                        .eq(Aria2DownloadTask::getEpisodeNumber, fileInfo.getEpisodeNumber())
        );
        // __puus= (下载需要)
        Long aria2ServerId = smoothWeightedRoundRobin.getAria2ServerId();
        if (log.isDebugEnabled()) {
            log.debug("aria2 数据库 id -> {}", aria2ServerId);
        }
        // show -> /title/session、  movie -> /title
        String savePath = getAria2SavePath(media.getType(), media.getName(), media.getSeason());
        // 下载保存的文件名
        String outName = FileUtils.getOutName(
                media.getType(),
                media.getName(),
                media.getSeason(),
                media.getTotalEpisode(),
                fileInfo.getFileName(),
                fileInfo.getEpisodeNumber(),
                mimeType
        );

        if (!ObjectUtils.isEmpty(aria2ServerId)) {
            if (ObjectUtils.isEmpty(aria2DownloadTask)) {
                aria2DownloadTask = new Aria2DownloadTask();
            }
            aria2DownloadTask.setMediaId(media.getId());
            aria2DownloadTask.setAria2ServiceId(aria2ServerId);
            aria2DownloadTask.setEpisodeNumber(fileInfo.getEpisodeNumber());
            aria2DownloadTask.setSavePath(savePath);
            aria2DownloadTask.setSize(fileInfo.getSize());
            aria2DownloadTask.setVideoWidth(fileInfo.getMediaMetadata().getWidth());
            aria2DownloadTask.setVideoHeight(fileInfo.getMediaMetadata().getHeight());
            aria2DownloadTask.setStatus(0);
            aria2DownloadTask.setOutName(outName);
            aria2DownloadTask.setResourceStatus(0);
            Aria2Server aria2Server = aria2ServerMapper.selectById(aria2ServerId);
            // 更新(失败的)或者插入
            aria2DownloadTasksMapper.insertOrUpdate(aria2DownloadTask);
            // 下载之前先判断任务是否在等待下载
            if (taskIsWaiting(aria2Server, aria2DownloadTask.getGid())) {
                gid = aria2DownloadTask.getGid();
            } else {
                // 获取 aria2 中的保存目录
                String dir = Aria2Utils.getDir(
                        aria2Server.getIp(),
                        aria2Server.getPort(),
                        aria2Server.getSecret()
                );
                // 现保存任务到数据库,防止 aria2 发生数据过来无法判断
                String response = aria2Download(
                        aria2Server, url, dir + savePath, outName, cookie
                );
                gid = JSONObject.parseObject(response).getString("result");
                // 暂时不考虑发生到aria2失败,更新数据
                aria2DownloadTask.setGid(gid);
                aria2DownloadTasksMapper.updateById(aria2DownloadTask);
            }
        }
        return gid;
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

    private String getAria2SavePath(MediaTypeEnum type, String title, Integer season) {
        String savePath = "";
        switch (type) {
            case tv -> savePath = "/" + title + "/" + String.format("Season %02d", season);
            case movie -> savePath = "/" + title;
        }
        return savePath;
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
}
