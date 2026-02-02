package com.roukaixin.cronvideos.scheduling;


import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.algorithm.SnowflakeIdWorker;
import com.roukaixin.cronvideos.api.TmdbApi;
import com.roukaixin.cronvideos.api.domain.Episode;
import com.roukaixin.cronvideos.domain.Cloud;
import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.Media;
import com.roukaixin.cronvideos.domain.MediaEpisode;
import com.roukaixin.cronvideos.mapper.CloudMapper;
import com.roukaixin.cronvideos.mapper.DownloadTaskMapper;
import com.roukaixin.cronvideos.mapper.MediaEpisodeMapper;
import com.roukaixin.cronvideos.mapper.MediaMapper;
import com.roukaixin.cronvideos.strategy.CloudDrive;
import com.roukaixin.cronvideos.strategy.CloudDriveContext;
import com.roukaixin.cronvideos.strategy.domain.FileInfo;
import com.roukaixin.cronvideos.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MediaScheduled {

    private final CloudMapper cloudMapper;

    private final MediaMapper mediaMapper;

    private final CloudDriveContext cloudDriveContext;

    private final DownloadTaskMapper downloadTaskMapper;

    private final MediaEpisodeMapper mediaEpisodeMapper;

    private final TmdbApi tmdbApi;

    private final SnowflakeIdWorker idWorker;


    public MediaScheduled(CloudMapper cloudMapper,
                          MediaMapper mediaMapper,
                          CloudDriveContext cloudDriveContext,
                          DownloadTaskMapper downloadTaskMapper,
                          MediaEpisodeMapper mediaEpisodeMapper,
                          TmdbApi tmdbApi,
                          SnowflakeIdWorker idWorker) {
        this.cloudMapper = cloudMapper;
        this.mediaMapper = mediaMapper;
        this.cloudDriveContext = cloudDriveContext;
        this.downloadTaskMapper = downloadTaskMapper;
        this.mediaEpisodeMapper = mediaEpisodeMapper;
        this.tmdbApi = tmdbApi;
        this.idWorker = idWorker;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10, initialDelay = 1000)
    public void downloadMedia() {
        if (SmoothWeightedRoundRobin.getInstance().size() > 0) {
            mediaMapper.selectAllStream((result) -> {
                Media media = result.getResultObject();
                List<Integer> needDownloadEpisode = mediaEpisodeMapper.selectEpisodeNumberByMediaId(
                        media.getId(),
                        LocalDateTime.now()
                ).stream().map(MediaEpisode::getEpisodeNumber).toList();
                if (!needDownloadEpisode.isEmpty()) {
                    log.info("开始更新视频 -> {}", media.getName());
                    List<Cloud> cloudShareList = cloudMapper.selectListByMediaId(media.getId());
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
                        listMap.forEach((_, value) -> value.stream()
                                .max(Comparator.comparingLong(FileInfo::getSize)).ifPresent(videos::add));
                        // 过滤已经下载过的视频
                        List<FileInfo> filterVideos = filterDownlandVideo(media.getId(), videos);
                        log.info("已经过滤掉已下载的视频 -> {}", JsonUtils.toJSONString(filterVideos));
                        int downlandCount = 0;
                        for (FileInfo filterVideo : filterVideos) {
                            CloudDrive cloudDrive = cloudDriveContext.getCloudDrive(filterVideo.getProvider());
                            downlandCount += cloudDrive.download(media, filterVideo);
                        }
                        if (downlandCount > 0) {
                            // 更新下载总数
                            new Thread(() -> downloadTaskMapper.selectStreamByMediaId(media.getId(), taskResult -> {
                                DownloadTask downloadTask = taskResult.getResultObject();
                                if (downloadTask.getStatus() != 3) {
                                    // 更新 mediaEpisode
                                    MediaEpisode mediaEpisode = mediaEpisodeMapper.selectOne(media.getId(), downloadTask.getEpisodeNumber());
                                    if (!ObjectUtils.isEmpty(mediaEpisode)) {
                                        mediaEpisode.setIsUpdate(1);
                                        mediaEpisodeMapper.updateIsUpdateById(1, mediaEpisode.getId());
                                    }
                                }
                            })).start();
                        }
                    }
                    log.info("更新结束视频 -> {}", media.getName());
                }
            });
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
        List<DownloadTask> downloadTasks = downloadTaskMapper.selectListByMediaId(mediaId);
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
        mediaMapper.selectAllStream(result -> {
            Media media = result.getResultObject();
            if (StringUtils.hasLength(media.getTmdbId())) {
                List<MediaEpisode> mediaEpisodeList = mediaEpisodeMapper.selectOneByMediaId(media.getId());
                Map<String, MediaEpisode> mediaEpisodeMap = mediaEpisodeList.stream().collect(Collectors
                        .toMap(k -> k.getSeasonNumber() + ":" + k.getEpisodeNumber(), v -> v));
                switch (media.getType()) {
                    case tv -> {
                        List<Episode> episodeList = tmdbApi.tvEpisodes(media.getTmdbId(), media.getSeasonNumber());
                        if (!ObjectUtils.isEmpty(episodeList)) {
                            List<MediaEpisode> update = new ArrayList<>();
                            List<MediaEpisode> add = new ArrayList<>();
                            for (Episode episode : episodeList) {
                                MediaEpisode mediaEpisode = mediaEpisodeMap.get(episode.getSeasonNumber() + ":" + episode.getEpisodeNumber());
                                if (Objects.isNull(mediaEpisode)) {
                                    mediaEpisode = new MediaEpisode();
                                    mediaEpisode.setId(idWorker.nextId());
                                    mediaEpisode.setMediaId(media.getId());
                                    mediaEpisode.setSeasonNumber(episode.getSeasonNumber());
                                    mediaEpisode.setEpisodeNumber(episode.getEpisodeNumber());
                                    mediaEpisode.setAirDate(episode.getAirDate());
                                    add.add(mediaEpisode);
                                } else {
                                    mediaEpisode.setMediaId(media.getId());
                                    mediaEpisode.setSeasonNumber(episode.getSeasonNumber());
                                    mediaEpisode.setEpisodeNumber(episode.getEpisodeNumber());
                                    mediaEpisode.setAirDate(episode.getAirDate());
                                    update.add(mediaEpisode);
                                }
                            }
                            if (!update.isEmpty()) {
                                mediaEpisodeMapper.updateBatch(update);
                            }
                            if (!add.isEmpty()) {
                                mediaEpisodeMapper.insertBatch(add);
                            }
                        }
                    }
                    case movie -> {

                    }
                }


            }
        });
    }


}
