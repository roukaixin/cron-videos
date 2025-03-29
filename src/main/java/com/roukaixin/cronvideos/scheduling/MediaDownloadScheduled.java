package com.roukaixin.cronvideos.scheduling;


import com.alibaba.fastjson2.JSON;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

}
