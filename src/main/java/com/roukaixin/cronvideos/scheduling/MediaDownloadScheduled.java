package com.roukaixin.cronvideos.scheduling;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.CloudShareMapper;
import com.roukaixin.cronvideos.mapper.MediaMapper;
import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import com.roukaixin.cronvideos.pojo.CloudShare;
import com.roukaixin.cronvideos.pojo.Media;
import com.roukaixin.cronvideos.strategy.CloudDrive;
import com.roukaixin.cronvideos.strategy.CloudDriveContext;
import com.roukaixin.cronvideos.strategy.domain.FileInfo;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
                    null,
                    resultContext -> {
                        // 流式返回一条数据
                        Media media = resultContext.getResultObject();
                        // 判断是否需要下载更新。movie 类型始终都要下载(如果还没下载)、tv 类型需要在更新日或者更新日的前一天都要去下载
                        if (media.getTotalEpisode() > media.getCurrentEpisode()
                                && (media.getUpdateDay().contains(today)
                                || (media.getUpdateDay().contains(yesterday) && now.getHour() < 8))
                        ) {
                            log.info("开始更新视频 -> {}", media.getName());
                            List<CloudShare> cloudShareList = cloudShareMapper.selectList(
                                    Wrappers.<CloudShare>lambdaQuery()
                                            .eq(CloudShare::getMediaId, media.getId())
                                            .eq(CloudShare::getIsLapse, 0)
                            );
                            if (!cloudShareList.isEmpty()) {
                                List<FileInfo> videoList = new ArrayList<>();
                                for (CloudShare cloudShare : cloudShareList) {
                                    CloudDrive cloudDrive = cloudDriveContext.getCloudDrive(cloudShare.getProvider());
                                    List<FileInfo> fileList = cloudDrive.getFileList(media, cloudShare);
                                    videoList.addAll(fileList);
                                }
                                // key： 集数，value： 所有数据包括重复集数的数据
                                Map<Integer, List<FileInfo>> listMap = videoList.stream()
                                        .collect(Collectors.groupingBy(FileInfo::getEpisodeNumber));
                                List<FileInfo> videos = new ArrayList<>();
                                // 获取当前集数中文件大小最大的
                                listMap.forEach((key, value) -> value.stream()
                                        .max(Comparator.comparingLong(FileInfo::getSize)).ifPresent(videos::add));
                                // 过滤已经下载过的视频
                                List<FileInfo> filterVideos = filterDownlandVideo(media.getId(), videos);
                                log.info("已经过滤掉已下载的视频 -> {}", JSON.toJSONString(filterVideos));
                                int downlandCount = 0;
                                for (FileInfo filterVideo : filterVideos) {
                                    CloudDrive cloudDrive = cloudDriveContext.getCloudDrive(filterVideo.getProvider());
                                    downlandCount += cloudDrive.download(media, filterVideo);
                                }
                                if (downlandCount > 0) {
                                    // 更新下载总数
                                    Long count = aria2DownloadTasksMapper.selectCount(
                                            Wrappers.<Aria2DownloadTask>lambdaQuery().eq(Aria2DownloadTask::getMediaId, media.getId())
                                    );
                                    media.setCurrentEpisode(Math.toIntExact(count));
                                    mediaMapper.updateById(media);
                                }
                            }
                            log.info("更新结束视频 -> {}", media.getName());
                        }
                    }
            );
        }
    }

    /**
     * 过滤掉已经下载文件
     *
     * @param mediaId 媒体id
     * @param videos  为过滤之前的视频文件
     * @return 过滤后的视频文件
     */
    private List<FileInfo> filterDownlandVideo(Long mediaId, List<FileInfo> videos) {
        List<Aria2DownloadTask> aria2DownloadTasks = aria2DownloadTasksMapper.selectList(
                Wrappers.<Aria2DownloadTask>lambdaQuery().eq(Aria2DownloadTask::getMediaId, mediaId));
        // 已经下载
        Map<Integer, Long> episodeNumberMap = aria2DownloadTasks.stream()
                .filter(e -> !e.getStatus().equals(3) && !ObjectUtils.isEmpty(e.getSize()))
                .collect(Collectors.toMap(Aria2DownloadTask::getEpisodeNumber, Aria2DownloadTask::getSize));
        return videos.stream().filter(e -> {
            if (e.getEpisodeNumber() == -1) {
                return false;
            }
            if (!episodeNumberMap.containsKey(e.getEpisodeNumber())) {
                return true;
            }
            // 下载失败的需要重新下载
            Long size = episodeNumberMap.get(e.getEpisodeNumber());
            return e.getSize() > size;
        }).toList();
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
        // ffmpeg -y -loglevel repeat+level+error -i intput.mp4 -c copy -map 0:v -map 0:a -map -0:v:m:attached_pic -metadata title='' output.mkv
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
