package com.roukaixin.cronvideos.scheduling;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.enums.MediaTypeEnum;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.CloudShareMapper;
import com.roukaixin.cronvideos.mapper.MediaMapper;
import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import com.roukaixin.cronvideos.pojo.CloudShare;
import com.roukaixin.cronvideos.pojo.Media;
import com.roukaixin.cronvideos.strategy.CloudDriveContext;
import com.roukaixin.cronvideos.utils.SshUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.common.SftpException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class MediaDownloadScheduled {

    private final CloudShareMapper cloudShareMapper;

    private final MediaMapper mediaMapper;

    private final CloudDriveContext cloudDriveContext;

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;


    public MediaDownloadScheduled(CloudShareMapper cloudShareMapper,
                                  MediaMapper mediaMapper,
                                  CloudDriveContext cloudDriveContext,
                                  Aria2DownloadTasksMapper aria2DownloadTasksMapper,
                                  SmoothWeightedRoundRobin smoothWeightedRoundRobin) {
        this.cloudShareMapper = cloudShareMapper;
        this.mediaMapper = mediaMapper;
        this.cloudDriveContext = cloudDriveContext;
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
        this.smoothWeightedRoundRobin = smoothWeightedRoundRobin;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10, initialDelay = 1000)
    public void downloadMedia() {
        if (smoothWeightedRoundRobin.size() > 0) {
            // 查询全部影视信息(未下载完的)
            LocalDateTime now = LocalDateTime.now();
            // 今天
            Integer today = now.getDayOfWeek().getValue();
            // 昨天
            Integer yesterday = now.minusDays(1).getDayOfWeek().getValue();
            mediaMapper.selectList(
                    Wrappers.<Media>lambdaQuery().apply("total_episode > current_episode"),
                    resultContext -> {
                        // 流式返回一条数据
                        Media resultMedia = resultContext.getResultObject();
                        // 判断是否需要下载更新。movie 类型始终都要下载(如果还没下载)、show 类型需要在更新日或者更新日的前一天都要去下载
                        if (resultMedia.getType().equals(MediaTypeEnum.movie) ||
                                (
                                        resultMedia.getType().equals(MediaTypeEnum.tv) &&
                                                (resultMedia.getUpdateDay().contains(today) ||
                                                        resultMedia.getUpdateDay().contains(yesterday))
                                )
                        ) {
                            log.info("开始更新视频 -> {}", resultMedia.getName());
                            // 获取网盘分享连接。
                            CloudShare cloudShare = cloudShareMapper.selectOne(
                                    Wrappers
                                            .<CloudShare>lambdaQuery()
                                            .eq(CloudShare::getMediaId, resultMedia.getId())
                            );
                            if (!ObjectUtils.isEmpty(cloudShare)) {
                                if (cloudShare.getIsLapse().equals(0)) {
                                    // downloadCount : 当前下载数量
                                    Integer downloadCount = cloudDriveContext.getCloudDrive(cloudShare.getProvider())
                                            .download(cloudShare, resultMedia);
                                    if (downloadCount > 0) {
                                        // 更新下载总数
                                        Long count = aria2DownloadTasksMapper.selectCount(
                                                Wrappers.<Aria2DownloadTask>lambdaQuery().eq(Aria2DownloadTask::getMediaId, resultMedia.getId())
                                        );
                                        resultMedia.setCurrentEpisode(Math.toIntExact(count));
                                        mediaMapper.updateById(resultMedia);
                                    }
                                }
                                if (cloudShare.getIsLapse() == 1) {
                                    if (log.isInfoEnabled()) {
                                        log.info("分享链接已经失效 -> {}", resultMedia.getName());
                                    }
                                    // 发生邮箱告知分享链接已经失效
                                }
                            } else {
                                if (log.isInfoEnabled()) {
                                    log.info("该影视没有网盘分享链接 -> {}", resultMedia.getName());
                                }
                            }
                            log.info("更新结束视频 -> {}", resultMedia.getName());
                        }
                    }
            );
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void formatMedia() {
        aria2DownloadTasksMapper.selectList(
                Wrappers
                        .<Aria2DownloadTask>lambdaQuery()
                        .eq(Aria2DownloadTask::getResourceStatus, 0)
                        .eq(Aria2DownloadTask::getStatus, 2),
                resultContext -> {
                    Aria2DownloadTask aria2DownloadTask = resultContext.getResultObject();
                    if (!ObjectUtils.isEmpty(aria2DownloadTask.getOutName())) {
                        log.info("开始转化视频 -> {}", aria2DownloadTask.getOutName());
                        String savePath = aria2DownloadTask.getSavePath();
                        String outName = aria2DownloadTask.getOutName();
                        String targetOutName = FilenameUtils.getBaseName(outName);
                        ClientSession tnt = SshUtils.init("tnt", "127.0.0.1", 22, "230553");
                        String ffmpeg = getFfmpeg(savePath, outName, targetOutName, null);
                        int flag = 0;
                        while (flag < 2) {
                            if (flag == 1) {
                                StringBuilder exclude_maps = new StringBuilder();
                                String ffprobe = SshUtils.execFfprobe(tnt, getFfprobe(savePath, outName));
                                JSONObject ffprobeJson = JSONObject.parseObject(ffprobe);
                                if (!ObjectUtils.isEmpty(ffprobeJson)) {
                                    JSONArray streams = ffprobeJson.getJSONArray("streams");
                                    for (int i = 0; i < streams.size(); i++) {
                                        JSONObject jsonObject = streams.getJSONObject(i);
                                        if (jsonObject.getString("codec_name").equals("unknown")) {
                                            // 需要过滤的音频，不知道解码的
                                            Integer index = jsonObject.getInteger("index");
                                            exclude_maps.append("-map -0:").append(index).append(" ");
                                        }
                                    }
                                }
                                ffmpeg = getFfmpeg(savePath, outName, targetOutName, exclude_maps);
                            }
                            if (SshUtils.execFfmpeg(tnt, ffmpeg)) {
                                // 执行成功
                                aria2DownloadTask.setResourceStatus(1);
                                aria2DownloadTasksMapper.updateById(aria2DownloadTask);
                                // 删除源文件
                                if (SshUtils.execFfmpeg(tnt, getRm(savePath, outName))) {
                                    log.info("删除视频成功 -> {}", outName);
                                }
                                // 修改回原来的名字
                                if (SshUtils.execFfmpeg(tnt, getMv(savePath, targetOutName))) {
                                    // 修改成功
                                    log.info("视频使用 ffmpeg 转化成功 -> {}", outName);
                                }
                            }
                            flag++;
                        }

                        log.info("转化结束视频 -> {}", aria2DownloadTask.getOutName());
                    }
                });
        moveMedia();
    }

    private static String getInputFile(String savePath, String outName) {
        return "'/home/tnt/job/IdeaProjects/docker-software/local/download" + savePath + "/" + outName + "'";
    }

    private static String getTargetFile(String savePath, String targetOutName) {
        return "'/home/tnt/job/IdeaProjects/docker-software/local/download" + savePath + "/" + targetOutName + "_target.mkv'";
    }

    private static String getFilePath(String savePath, String targetOutName) {
        return "'/home/tnt/job/IdeaProjects/docker-software/local/download" + savePath + "/" + targetOutName + ".mkv'";
    }

    private static String getRm(String savePath, String outName) {
        String[] command = {
                "rm",
                getInputFile(savePath, outName)
        };
        return String.join(" ", command);
    }

    private static String getMv(String savePath, String targetOutName) {
        String[] command = {
                "mv",
                getTargetFile(savePath, targetOutName),
                getFilePath(savePath, targetOutName)
        };
        return String.join(" ", command);
    }

    private static String getFfmpeg(String savePath, String outName, String targetOutName, StringBuilder exclude_maps) {
        String[] command = {
                "ffmpeg",
                "-y",
                "-loglevel", "repeat+level+error",
                "-i", getInputFile(savePath, outName),
                "-c", "copy",
                "-map", "0:v",
                "-map", "0:a",
                "-map", "-0:v:m:attached_pic",
                ObjectUtils.isEmpty(exclude_maps) ? "" : String.valueOf(exclude_maps),
                "-metadata", "title=''",
                getTargetFile(savePath, targetOutName)
        };
        return String.join(" ", command);
    }

    private static String getFfprobe(String savePath, String outName) {
        String[] command = {
                "ffprobe",
                "-v", "error",
                "-select_streams", "a",
                "-show_entries", "stream=index,codec_name",
                "-of", "json",
                "-show_optional_fields", "always",
                getInputFile(savePath, outName)
        };
        return String.join(" ", command);
    }

    public void moveMedia() {
        AtomicReference<ClientSession> sourceSession = new AtomicReference<>();
        AtomicReference<ClientSession> targetSession = new AtomicReference<>();
        aria2DownloadTasksMapper.selectList(Wrappers.<Aria2DownloadTask>lambdaQuery().eq(Aria2DownloadTask::getResourceStatus, 1), resultContext -> {
            Aria2DownloadTask aria2DownloadTask = resultContext.getResultObject();
            String outName = aria2DownloadTask.getOutName();
            if (!ObjectUtils.isEmpty(outName)) {
                Media media = mediaMapper.selectById(aria2DownloadTask.getMediaId());
                if (!ObjectUtils.isEmpty(media.getTypeAlias())) {
                    try {
                        // 下载器服务器 ip 地址  session
                        sourceSession.set(SshUtils.init("tnt", "127.0.0.1", 22, "230553"));
                        // 媒体服务器 ip 地址 session
                        targetSession.set(SshUtils.init("tnt", "127.0.0.1", 22, "230553"));
                        SshUtils.move(
                                sourceSession.get(),
                                targetSession.get(),
                                "/home/tnt/job/IdeaProjects/docker-software/local/download",
                                "/home/tnt/Videos" + "/" + media.getTypeAlias(),
                                aria2DownloadTask.getSavePath(),
                                outName
                        );
                        aria2DownloadTask.setResourceStatus(2);
                        aria2DownloadTasksMapper.updateById(aria2DownloadTask);
                    } catch (IOException e) {
                        if (e instanceof SftpException sftpException && sftpException.getMessage().equals("No such file")) {
                            // 文件不存在
                            aria2DownloadTask.setResourceStatus(2);
                            aria2DownloadTasksMapper.updateById(aria2DownloadTask);
                        } else {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
        });
        try {
            ClientSession clientSession = sourceSession.get();
            if (clientSession != null) {
                clientSession.close();
            }
            clientSession = targetSession.get();
            if (clientSession != null) {
                clientSession.close();
            }
        } catch (IOException e) {
            log.error("ssh session 关闭异常", e);
        }
    }
}
