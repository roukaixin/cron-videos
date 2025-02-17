package com.roukaixin.cronvideos.scheduling;


import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.CloudSharesMapper;
import com.roukaixin.cronvideos.mapper.MediaMapper;
import com.roukaixin.cronvideos.pojo.CloudShares;
import com.roukaixin.cronvideos.pojo.Media;
import com.roukaixin.cronvideos.strategy.CloudDriveContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class MediaDownloadScheduled {

    private final CloudSharesMapper cloudSharesMapper;

    private final MediaMapper mediaMapper;

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;

    private final CloudDriveContext cloudDriveContext;


    public MediaDownloadScheduled(CloudSharesMapper cloudSharesMapper,
                                  MediaMapper mediaMapper,
                                  Aria2DownloadTasksMapper aria2DownloadTasksMapper,
                                  SmoothWeightedRoundRobin smoothWeightedRoundRobin,
                                  CloudDriveContext cloudDriveContext) {
        this.cloudSharesMapper = cloudSharesMapper;
        this.mediaMapper = mediaMapper;
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
        this.smoothWeightedRoundRobin = smoothWeightedRoundRobin;
        this.cloudDriveContext = cloudDriveContext;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduled() {
        List<CloudShares> sharesList = cloudSharesMapper.selectList(null);
        sharesList.forEach(shares -> {
            Media media = mediaMapper.selectById(shares.getMediaId());
            cloudDriveContext.getCloudDrive(shares.getProvider()).download(shares, media);
        });
    }
}
