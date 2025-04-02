package com.roukaixin.cronvideos.strategy;

import com.roukaixin.cronvideos.enums.CloudShareProviderEnum;
import com.roukaixin.cronvideos.mapper.CloudShareMapper;
import com.roukaixin.cronvideos.mapper.CloudStorageAuthMapper;
import com.roukaixin.cronvideos.mapper.DownloadTaskMapper;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
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

    private final DownloaderMapper downloaderMapper;

    private final DownloadTaskMapper downloadTaskMapper;

    private final CloudShareMapper cloudShareMapper;

    private final QuarkApi quarkApi;

    private final RedisTemplate<String, Object> redisTemplate;

    public CloudDriveContext(CloudStorageAuthMapper cloudStorageAuthMapper,
                             DownloaderMapper downloaderMapper,
                             DownloadTaskMapper downloadTaskMapper,
                             CloudShareMapper cloudShareMapper,
                             QuarkApi quarkApi,
                             RedisTemplate<String, Object> redisTemplate) {
        this.cloudStorageAuthMapper = cloudStorageAuthMapper;
        this.downloaderMapper = downloaderMapper;
        this.downloadTaskMapper = downloadTaskMapper;
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
                        downloaderMapper,
                        downloadTaskMapper,
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
