package com.roukaixin.cronvideos.strategy;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.Aria2ServerMapper;
import com.roukaixin.cronvideos.mapper.CloudStorageAuthMapper;
import com.roukaixin.cronvideos.pojo.*;
import com.roukaixin.cronvideos.strategy.quark.FileInfo;
import com.roukaixin.cronvideos.utils.Aria2Utils;
import com.roukaixin.cronvideos.utils.FileUtils;
import com.roukaixin.cronvideos.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component(value = "cloud_drive_1")
public class QuarkStrategy implements CloudDrive {

    private final RestClient restClient;

    private final CloudStorageAuthMapper cloudStorageAuthMapper;

    private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;

    private final Aria2ServerMapper aria2ServerMapper;

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    public QuarkStrategy(RestClient restClient,
                         CloudStorageAuthMapper cloudStorageAuthMapper,
                         SmoothWeightedRoundRobin smoothWeightedRoundRobin,
                         Aria2ServerMapper aria2ServerMapper,
                         Aria2DownloadTasksMapper aria2DownloadTasksMapper) {
        this.restClient = restClient;
        this.cloudStorageAuthMapper = cloudStorageAuthMapper;
        this.smoothWeightedRoundRobin = smoothWeightedRoundRobin;
        this.aria2ServerMapper = aria2ServerMapper;
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
    }

    @Override
    public void download(CloudShares cloudShares, Media media) {
        String sToken = getSToken(cloudShares.getShareId());
        if (!ObjectUtils.isEmpty(sToken)) {
            log.info("stoken : {}", sToken);
            List<FileInfo> fileInfoList = getFileList(cloudShares.getShareId(), sToken, null);
            Pattern p = Pattern.compile(cloudShares.getFileRegex());
            fileInfoList = fileInfoList
                    .stream()
                    .filter(fileInfo -> p.matcher(fileInfo.getFileName()).matches())
                    .sorted(Comparator.comparing(FileInfo::getFileName, Collator.getInstance()))
                    .toList();
            log.info("分享文件列表 : {}", JSONObject.toJSONString(fileInfoList));
            for (FileInfo info : fileInfoList) {
                // 保存之后文件的id
                String fid = saveFile(info, cloudShares.getShareId(), sToken, media.getTitle(), media.getSeasonNumber());
                log.info("下载文件id : {}", fid);
                if (!fid.isEmpty()) {
                    Pattern pattern = Pattern.compile(media.getEpisodeRegex());
                    // 发生失败不考虑先,gid 为空就是失败
                    String gid = sendDownload(fid, info, media, pattern);
                    log.info("下载任务 gid {}", gid);
                    // 删除原文件
                    String taskId = deleteSourceFile(fid);
                    if (!taskId.isEmpty()) {
                        // 判断是否删除成功
                        boolean deleteFlag = deleteSourceFileTask(taskId, 0, 2);
                    }
                }
                ThreadUtils.sleep(TimeUnit.MILLISECONDS, 500);
            }
        }
    }

    private boolean deleteSourceFileTask(String taskId, int retryCount, final int maxRetries) {
        boolean flag = false;
        // maxRetries 最大重试次数，防止无限递归
        // 避免递归过多，达到最大重试次数时退出
        if (retryCount >= maxRetries) {
            return false;
        }
        JSONObject taskBody = taskApi(taskId);
        if (taskBody != null) {
            if (taskBody.getInteger("status").equals(200)) {
                JSONObject data = taskBody
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

    private String deleteSourceFile(String fid) {
        String taskId = "";
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/file/delete").build().toUri();
        JSONObject body = restClient
                .post()
                .uri(uri)
                .body(JSONObject.of(
                        "action_type", 2,
                        "filelist", JSONArray.of(fid),
                        "exclude_fids", new JSONArray())
                )
                .cookies(c -> c.addAll(getCookies()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::print)
                .body(JSONObject.class);
        if (body != null) {
            if (body.getInteger("status").equals(200) && body.getInteger("code").equals(0)) {
                taskId = body.getJSONObject("data").getString("task_id");
            }
        }
        return taskId;
    }

    private String getSToken(String pwdId) {
        String sToken = "";
        JSONObject resp = restClient
                .post()
                .uri("https://drive-h.quark.cn/1/clouddrive/share/sharepage/token")
                .body(JSONObject.of(
                        "pwd_id", pwdId,
                        "passcode", ""
                ))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::print)
                .body(JSONObject.class);
        if (resp != null) {
            if (resp.getInteger("status").equals(200)) {
                sToken = resp.getObject("data", JSONObject.class)
                        .getString("stoken");
            }
        }
        return sToken;
    }

    private List<FileInfo> getFileList(String pwdId, String sToken, String pdirFid) {

        URI uri = getUriComponentsBuilder("https://drive-h.quark.cn/1/clouddrive/share/sharepage/detail")
                .queryParam("pwd_id", "{pwd_id}")
                .queryParam("stoken", "{stoken}")
                .queryParam("pdir_fid", "{pdir_fid}")
                .queryParam("_page", 1)
                .queryParam("_size", 1000)
                .encode()
                .build().expand(pwdId, sToken, pdirFid).toUri();
        JSONObject body = restClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::print)
                .body(JSONObject.class);
        List<FileInfo> fileInfoList = new ArrayList<>();
        if (body != null) {
            if (body.getInteger("status").equals(200)) {
                JSONObject data = body.getJSONObject("data");
                List<FileInfo> dataList = data.getList("list", FileInfo.class);
                dataList.forEach(e -> {
                    if (e.getCategory().equals(0)) {
                        fileInfoList.addAll(getFileList(pwdId, sToken, e.getFid()));
                    } else {
                        fileInfoList.add(e);
                    }
                });
            }
        }
        return fileInfoList;
    }

    // 保存路径id(在自己网盘下,需要 cookie 访问)
    private String savePathFid(String title, Integer seasonNumber) {
        CloudStorageAuth cloudStorageAuth = cloudStorageAuthMapper.selectOne(
                Wrappers.<CloudStorageAuth>lambdaQuery().eq(CloudStorageAuth::getProvider, 1));
        String fid = "";
        if (!ObjectUtils.isEmpty(cloudStorageAuth)) {
            fid = getSaveFid(title, seasonNumber);
        } else {
            log.info("缺少 Cookie");
        }
        return fid;
    }

    // 获取保存目录id(在自己网盘下,需要 cookie 访问)
    private String getSaveFid(String title, Integer seasonNumber) {
        // 以下请求都需要 cookie
        JSONObject fileSort = getFileSort("0");
        String fid = "";
        if (fileSort.getInteger("status").equals(200)) {
            List<FileInfo> list = fileSort.getJSONObject("data").getList("list", FileInfo.class);
            boolean exit = false;
            for (FileInfo fileInfo : list) {
                if (fileInfo.getFileName().equals("来自：分享")) {
                    exit = true;
                    fid = fileInfo.getFid();
                    break;
                }
            }
            if (exit) {
                fileSort = getFileSort(fid);
                if (fileSort.getInteger("status").equals(200)) {
                    list = fileSort.getJSONObject("data").getList("list", FileInfo.class);
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
                        fileSort = getFileSort(fid);
                        if (fileSort.getInteger("status").equals(200)) {
                            list = fileSort.getJSONObject("data").getList("list", FileInfo.class);
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

    private JSONObject getFileSort(String pdirFid) {
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/file/sort")
                .queryParam("pdir_fid", "{pdir_fid}")
                .encode().build().expand(pdirFid).toUri();
        JSONObject body = restClient.get()
                .uri(uri)
                .cookies(c -> c.addAll(getCookies()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::print).body(JSONObject.class);
        log.info("文件目录 {}", body == null ? "" : body.toJSONString());
        return body == null ? JSONObject.of() : body;
    }

    // 创建文件夹
    private String mkdirFile(String pdirFid, String fileName) {
        String fid = "";
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/file")
                .build().toUri();
        JSONObject body = restClient.post()
                .uri(uri)
                .cookies(cookie -> cookie.addAll(getCookies()))
                .body(JSONObject.of("pdir_fid", pdirFid, "file_name", fileName))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::print)
                .body(JSONObject.class);
        log.info("创建文件夹 {}", body == null ? "" : body.toJSONString());
        if (body != null) {
            if (body.getInteger("status").equals(200)) {
                fid = body.getJSONObject("data").getString("fid");
            }
        }
        return fid;
    }

    // 保存文件
    private String saveFile(FileInfo fileInfo,
                            String pwdId,
                            String stoken,
                            String title,
                            Integer seasonNumber) {
        // 在自己网盘中创建文件夹(如果没有),现在默认保存路径都在跟路经下的 `来自：分享`
        String fid = savePathFid(title, seasonNumber);
        log.info("保存路经文件夹 ID {}", fid);
        String downloadFid = "";
        if (!fid.isEmpty()) {
            URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/share/sharepage/save")
                    .build().toUri();
            // 一个一个保存,防止网盘容量不足
            JSONObject body = restClient
                    .post()
                    .uri(uri)
                    .cookies(c -> c.addAll(getCookies()))
                    .body(JSONObject.of(
                                    "pwd_id", pwdId,
                                    "stoken", stoken,
                                    "to_pdir_fid", fid,
                                    "fid_list", JSONArray.of(fileInfo.getFid())
                            )
                    )
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, this::print)
                    .body(JSONObject.class);
            log.info("保存文件到指定目录 {}", body == null ? "" : body.toJSONString());
            if (body != null) {
                // status:400 code:32003 表示网盘没有可用空间
                if (body.getInteger("status").equals(200) && body.getInteger("code").equals(0)) {
                    String taskId = body.getJSONObject("data").getString("task_id");
                    downloadFid = getDownloadFid(taskId, 0);
                }
            }
        }
        return downloadFid;
    }

    private MultiValueMap<String, String> getCookies() {
        CloudStorageAuth cloudStorageAuth = cloudStorageAuthMapper.selectOne(
                Wrappers.<CloudStorageAuth>lambdaQuery().eq(CloudStorageAuth::getProvider, 1));
        MultiValueMap<String, String> cookies = new LinkedMultiValueMap<>();
        if (!ObjectUtils.isEmpty(cloudStorageAuth)) {
            String cookie = cloudStorageAuth.getCookie();
            for (String s : cookie.split(";")) {
                String[] split = s.split("=", 2);
                cookies.add(split[0], split[1]);
            }
        }
        return cookies;
    }

    private UriComponentsBuilder getUriComponentsBuilder(String uri) {
        return UriComponentsBuilder
                .fromUriString(uri)
                .queryParam("pr", "ucpro")
                .queryParam("fr", "pc");
    }

    private String getDownloadFid(String taskId, int retryCount) {
        // 最大重试次数，防止无限递归
        final int MAX_RETRIES = 2;
        // 避免递归过多，达到最大重试次数时退出
        if (retryCount >= MAX_RETRIES) {
            return "";
        }
        JSONObject taskBody = taskApi(taskId);
        String downloadFid = "";
        if (taskBody != null) {
            if (taskBody.getInteger("status").equals(200)) {
                JSONObject data = taskBody
                        .getJSONObject("data");
                if (data.getInteger("status").equals(2)) {
                    downloadFid = data
                            .getJSONObject("save_as")
                            .getJSONArray("save_as_top_fids")
                            .getString(0);
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        log.info("睡眠异常 {}", e.getMessage());
                    }
                    return getDownloadFid(taskId, retryCount + 1);
                }
            }
        }
        return downloadFid;
    }

    private JSONObject taskApi(String taskId) {
        URI taskUri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/task")
                .queryParam("task_id", "{task_id}")
                .encode()
                .build()
                .expand(taskId).toUri();
        JSONObject taskBody = restClient
                .get()
                .uri(taskUri)
                .cookies(c -> c.addAll(getCookies()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::print)
                .body(JSONObject.class);
        log.info("获取任务结果 {}", taskBody == null ? "" : taskBody.toJSONString());
        return taskBody;
    }

    // 发生请求 aria2 下载
    private String sendDownload(String fid, FileInfo fileinfo, Media media, Pattern p) {
        String gid = "";
        // 获取下载链接
        JSONObject downloadInfo = getDownloadUrl(fid);
        String downloadUrl = downloadInfo.getString("download_url");
        if (!downloadUrl.isEmpty()) {
            log.info("文件名 : {} 直链下载地址 : {}", fileinfo.getFileName(), downloadUrl);
            // 获取下载直链返回 `__puus=` cookie(下载需要)
            String cookie = downloadInfo.getString("cookie");
            if (cookie != null) {
                Long aria2ServerId = smoothWeightedRoundRobin.getAria2ServerId();
                String regex = FileUtils.episodeRegex(p, fileinfo.getFileName());
                Aria2Server aria2Server = aria2ServerMapper.selectById(aria2ServerId);
                String dir = Aria2Utils.getDir(
                        aria2Server.getIp(),
                        aria2Server.getPort(),
                        aria2Server.getSecret()
                );
                String aria2SavePath = getAria2SavePath(dir, media.getTitle(), media.getSeasonNumber());
                // 保存在数据库中
                Aria2DownloadTask aria2DownloadTask = new Aria2DownloadTask();
                aria2DownloadTask.setMediaId(media.getId());
                aria2DownloadTask.setAria2ServiceId(aria2ServerId);
                aria2DownloadTask.setEpisodeNumber(FileUtils.getEpisodeNumber(regex));
                aria2DownloadTask.setSavePath(aria2SavePath);
                aria2DownloadTask.setGid(gid);
                aria2DownloadTasksMapper.insert(aria2DownloadTask);
                String mimeType = downloadInfo.getString("format_type");
                String out = FileUtils.getName(
                        media.getTitle(),
                        media.getSeasonNumber(),
                        fileinfo.getFileName(),
                        regex,
                        mimeType
                );
                gid = JSONObject.parseObject(aria2Download(
                        aria2Server, downloadUrl, aria2SavePath, out, cookie
                )).getString("result");
                // 暂时不考虑发生到aria2失败,更新数据
                aria2DownloadTask.setGid(gid);
                aria2DownloadTasksMapper.updateById(aria2DownloadTask);
            }

        }
        return gid;
    }

    private JSONObject getDownloadUrl(String fid) {
        String downloadUrl = "";
        String formatType = "";
        URI uri = getUriComponentsBuilder("https://drive.quark.cn/1/clouddrive/file/download")
                .build().toUri();
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) quark-cloud-drive/2.5.20 Chrome/100.0.4896.160 Electron/18.3.5.4-b478491100 Safari/537.36 Channel/pckk_other_ch";
        ResponseEntity<JSONObject> entity = restClient
                .post()
                .uri(uri)
                .header("User-Agent", userAgent)
                .cookies(c -> c.addAll(getCookies()))
                .body(JSONObject.of("fids", JSONArray.of(fid)))
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::print)
                .toEntity(JSONObject.class);
        JSONObject downloadBody = entity.getBody();
        log.info("获取直链结果 {}", downloadBody == null ? "" : downloadBody.toJSONString());
        if (downloadBody != null) {
            if (downloadBody.getInteger("status").equals(200) && downloadBody.getInteger("code").equals(0)) {
                JSONObject downloadBodyData = downloadBody.getJSONArray("data").getJSONObject(0);
                downloadUrl = downloadBodyData.getString("download_url");
                formatType = downloadBodyData.getString("format_type");
            }
        }
        return JSONObject.of(
                "cookie", getSetCookie(entity),
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

    private <T> String getSetCookie(ResponseEntity<T> response) {
        List<String> cookies = response.getHeaders().get("set-cookie");
        String cookie = "";
        if (cookies != null) {
            for (String string : cookies) {
                String[] split = string.split(";");
                cookie = Arrays.stream(split)
                        .filter(a -> a.contains("__puus="))
                        .collect(Collectors.joining("; "));
            }
        }
        return cookie;
    }

    private String aria2Download(Aria2Server aria2Server, String downloadUrl, String aria2DownloadDir, String out, String cookies) {
        // 调用 aria2 下载
        JSONArray objects = new JSONArray();
        objects.add("token:" + aria2Server.getSecret());
        objects.add(JSONArray.of(downloadUrl));
        objects.add(JSONObject.of(
                "dir", aria2DownloadDir,
                "split", "16",
                "max-connection-per-server", 16,
                "out", out,
                "header", JSONArray.of("Cookie:" + cookies)
        ));
        String s = Aria2Utils.addUri(aria2Server.getIp(), aria2Server.getPort(), objects.toJSONString());
        log.info("发送下载结果 {}", s);
        return s;
    }

    private void print(HttpRequest request, ClientHttpResponse response) throws IOException {
        log.info("================请求发生错误================");
        log.info("请求 URL : {}", request.getURI());
        log.info("请求方法 : {}", request.getMethod());
        log.info("响应状态 : {} {}", response.getStatusCode().value(), response.getStatusText());
        log.info("==========================================");
    }
}
