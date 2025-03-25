package com.roukaixin.cronvideos.strategy;

import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.enums.CloudShareProviderEnum;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.mapper.Aria2ServerMapper;
import com.roukaixin.cronvideos.mapper.CloudShareMapper;
import com.roukaixin.cronvideos.mapper.CloudStorageAuthMapper;
import com.roukaixin.cronvideos.strategy.quark.QuarkApi;
import com.roukaixin.cronvideos.strategy.quark.QuarkStrategy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CloudDriveContext {


    private static final Map<String, CloudDrive> CLOUD_DRIVE_MAP = new HashMap<>();

    private final CloudStorageAuthMapper cloudStorageAuthMapper;

    private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;

    private final Aria2ServerMapper aria2ServerMapper;

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    private final CloudShareMapper cloudShareMapper;

    private final QuarkApi quarkApi;

    private final RedisTemplate<String, Object> redisTemplate;

    public CloudDriveContext(CloudStorageAuthMapper cloudStorageAuthMapper,
                             SmoothWeightedRoundRobin smoothWeightedRoundRobin,
                             Aria2ServerMapper aria2ServerMapper,
                             Aria2DownloadTasksMapper aria2DownloadTasksMapper,
                             CloudShareMapper cloudShareMapper,
                             QuarkApi quarkApi,
                             RedisTemplate<String, Object> redisTemplate) {
        this.cloudStorageAuthMapper = cloudStorageAuthMapper;
        this.smoothWeightedRoundRobin = smoothWeightedRoundRobin;
        this.aria2ServerMapper = aria2ServerMapper;
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
        this.cloudShareMapper = cloudShareMapper;
        this.quarkApi = quarkApi;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        CLOUD_DRIVE_MAP.put(
                "cloud_drive_" + CloudShareProviderEnum.quark.getProvider(),
                new QuarkStrategy(
                        cloudStorageAuthMapper,
                        smoothWeightedRoundRobin,
                        aria2ServerMapper,
                        aria2DownloadTasksMapper,
                        quarkApi,
                        redisTemplate,
                        cloudShareMapper
                )
        );
    }


    public CloudDrive getCloudDrive(CloudShareProviderEnum key) {
        return CLOUD_DRIVE_MAP.get("cloud_drive_" + key.getProvider());
    }
}
