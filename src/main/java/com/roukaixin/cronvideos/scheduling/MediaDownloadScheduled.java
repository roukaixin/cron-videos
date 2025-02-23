package com.roukaixin.cronvideos.scheduling;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.CloudSharesMapper;
import com.roukaixin.cronvideos.mapper.MediaMapper;
import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import com.roukaixin.cronvideos.pojo.CloudShares;
import com.roukaixin.cronvideos.pojo.Media;
import com.roukaixin.cronvideos.strategy.CloudDriveContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MediaDownloadScheduled {

    private final CloudSharesMapper cloudSharesMapper;

    private final MediaMapper mediaMapper;

    private final CloudDriveContext cloudDriveContext;

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;


    public MediaDownloadScheduled(CloudSharesMapper cloudSharesMapper,
                                  MediaMapper mediaMapper,
                                  CloudDriveContext cloudDriveContext,
                                  Aria2DownloadTasksMapper aria2DownloadTasksMapper) {
        this.cloudSharesMapper = cloudSharesMapper;
        this.mediaMapper = mediaMapper;
        this.cloudDriveContext = cloudDriveContext;
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10, initialDelay = 1000)
    public void scheduled() {
        // 查询全部影视信息(未下载完的)
        LocalDateTime now = LocalDateTime.now();
        // 今天
        Integer today = now.getDayOfWeek().getValue();
        // 昨天
        Integer yesterday = now.minusDays(1).getDayOfWeek().getValue();
        mediaMapper.selectList(
                Wrappers.<Media>lambdaQuery()
                        .apply("total_episodes > current_episode"),
                resultContext -> {
                    Media resultMedia = resultContext.getResultObject();
                    if (resultMedia.getUpdateDays().contains(today) ||
                            resultMedia.getUpdateDays().contains(yesterday)) {
                        log.info("更新视频 {}", resultMedia.getTitle());
                        CloudShares cloudShares = cloudSharesMapper.selectOne(
                                Wrappers
                                        .<CloudShares>lambdaQuery()
                                        .eq(CloudShares::getMediaId, resultMedia.getId())
                        );
                        if (!ObjectUtils.isEmpty(cloudShares)) {
                            cloudDriveContext.getCloudDrive(cloudShares.getProvider())
                                    .download(cloudShares, resultMedia);
                            // 更新下载总数
                            Long count = aria2DownloadTasksMapper.selectCount(
                                    Wrappers.<Aria2DownloadTask>lambdaQuery().eq(Aria2DownloadTask::getMediaId, resultMedia.getId())
                            );
                            resultMedia.setCurrentEpisode(Math.toIntExact(count));
                            mediaMapper.updateById(resultMedia);
                        } else {
                            if (log.isInfoEnabled()) {
                                log.info("该影视没有网盘分享链接 -> {}", resultMedia.getTitle());
                            }
                        }
                    }
                }
        );
    }
}
