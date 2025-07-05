package com.roukaixin.cronvideos.scheduling;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.api.TmdbApi;
import com.roukaixin.cronvideos.api.domain.Episode;
import com.roukaixin.cronvideos.domain.*;
import com.roukaixin.cronvideos.mapper.CloudMapper;
import com.roukaixin.cronvideos.mapper.DownloadTaskMapper;
import com.roukaixin.cronvideos.mapper.MediaEpisodeMapper;
import com.roukaixin.cronvideos.mapper.MediaMapper;
import com.roukaixin.cronvideos.strategy.CloudDrive;
import com.roukaixin.cronvideos.strategy.CloudDriveContext;
import com.roukaixin.cronvideos.strategy.domain.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Component
@Slf4j
public class MediaScheduled {

    private final CloudMapper cloudMapper;

    private final MediaMapper mediaMapper;

    private final CloudDriveContext cloudDriveContext;

    private final DownloadTaskMapper downloadTaskMapper;

    private final MediaEpisodeMapper mediaEpisodeMapper;

    private final TmdbApi tmdbApi;


    public MediaScheduled(CloudMapper cloudMapper,
                          MediaMapper mediaMapper,
                          CloudDriveContext cloudDriveContext,
                          DownloadTaskMapper downloadTaskMapper,
                          MediaEpisodeMapper mediaEpisodeMapper,
                          TmdbApi tmdbApi) {
        this.cloudMapper = cloudMapper;
        this.mediaMapper = mediaMapper;
        this.cloudDriveContext = cloudDriveContext;
        this.downloadTaskMapper = downloadTaskMapper;
        this.mediaEpisodeMapper = mediaEpisodeMapper;
        this.tmdbApi = tmdbApi;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10, initialDelay = 1000)
    public void downloadMedia() {
        if (SmoothWeightedRoundRobin.getInstance().size() > 0) {
            mediaMapper.selectList(
                    Wrappers.emptyWrapper(),
                    resultContext -> {
                        // 流式返回一条数据
                        Media media = resultContext.getResultObject();
                        List<Integer> needDownloadEpisode = mediaEpisodeMapper.selectList(
                                Wrappers.<MediaEpisode>lambdaQuery()
                                        .select(MediaEpisode::getEpisodeNumber)
                                        .le(MediaEpisode::getAirDate, LocalDateTime.now().format(ISO_LOCAL_DATE))
                                        .eq(MediaEpisode::getIsUpdate, 0)
                                        .eq(MediaEpisode::getMediaId, media.getId())
                        ).stream().map(MediaEpisode::getEpisodeNumber).toList();
                        if (!needDownloadEpisode.isEmpty()) {
                            log.info("开始更新视频 -> {}", media.getName());
                            List<Cloud> cloudShareList = cloudMapper.selectList(
                                    Wrappers.<Cloud>lambdaQuery()
                                            .eq(Cloud::getMediaId, media.getId())
                                            .eq(Cloud::getIsLapse, 0)
                            );
                            if (!cloudShareList.isEmpty()) {
                                List<FileInfo> videoList = new ArrayList<>();
                                for (Cloud cloud : cloudShareList) {
                                    CloudDrive cloudDrive = cloudDriveContext.getCloudDrive(cloud.getProvider());
                                    List<FileInfo> fileList = cloudDrive.getFileList(media, cloud, needDownloadEpisode);
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
                                    new Thread(() -> downloadTaskMapper.selectList(
                                            Wrappers.<DownloadTask>lambdaQuery()
                                                    .eq(DownloadTask::getMediaId, media.getId()),
                                            resultDownloadTask -> {
                                                DownloadTask downloadTask = resultDownloadTask.getResultObject();
                                                if (downloadTask.getStatus() != 3) {
                                                    // 更新 mediaEpisode
                                                    MediaEpisode mediaEpisode = mediaEpisodeMapper.selectOne(
                                                            Wrappers.<MediaEpisode>lambdaQuery()
                                                                    .eq(MediaEpisode::getMediaId, downloadTask.getMediaId())
                                                                    .eq(MediaEpisode::getEpisodeNumber, downloadTask.getEpisodeNumber()));
                                                    if (!ObjectUtils.isEmpty(mediaEpisode)) {
                                                        mediaEpisode.setIsUpdate(1);
                                                        mediaEpisodeMapper.updateById(mediaEpisode);
                                                    }
                                                }
                                            }
                                    )).start();
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
        List<DownloadTask> downloadTasks = downloadTaskMapper.selectList(
                Wrappers.<DownloadTask>lambdaQuery().eq(DownloadTask::getMediaId, mediaId));
        // 已经下载
        Map<Integer, Long> episodeNumberMap = downloadTasks.stream()
                .filter(e -> !e.getStatus().equals(3) && !ObjectUtils.isEmpty(e.getSize()))
                .collect(Collectors.toMap(DownloadTask::getEpisodeNumber, DownloadTask::getSize));
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


    @Scheduled(cron = "0 0 6 * * *")
    public void updateMediaMate() {
        mediaMapper.selectList(Wrappers.emptyWrapper(), resultContext -> {
            Media media = resultContext.getResultObject();
            if (StringUtils.hasLength(media.getTmdbId())) {
                List<MediaEpisode> mediaEpisodeList = mediaEpisodeMapper.selectList(
                        Wrappers.<MediaEpisode>lambdaQuery().eq(MediaEpisode::getMediaId, media.getId()));
                Map<String, MediaEpisode> mediaEpisodeMap = mediaEpisodeList.stream().collect(Collectors
                        .toMap(k -> k.getSeasonNumber() + ":" + k.getEpisodeNumber(), v -> v));
                switch (media.getType()) {
                    case tv -> {
                        List<Episode> episodeList = tmdbApi.tvEpisodes(media.getTmdbId(), media.getSeasonNumber());
                        if (!ObjectUtils.isEmpty(episodeList)) {
                            mediaEpisodeList = new ArrayList<>();
                            for (Episode episode : episodeList) {
                                MediaEpisode mediaEpisode = mediaEpisodeMap.getOrDefault(
                                        episode.getSeasonNumber() + ":" + episode.getEpisodeNumber(),
                                        new MediaEpisode());
                                mediaEpisode.setMediaId(media.getId());
                                mediaEpisode.setSeasonNumber(episode.getSeasonNumber());
                                mediaEpisode.setEpisodeNumber(episode.getEpisodeNumber());
                                mediaEpisode.setAirDate(episode.getAirDate());
                                mediaEpisodeList.add(mediaEpisode);
                            }
                            mediaEpisodeMapper.insertOrUpdate(mediaEpisodeList);
                        }
                    }
                    case movie -> {

                    }
                }


            }
        });
    }


}
