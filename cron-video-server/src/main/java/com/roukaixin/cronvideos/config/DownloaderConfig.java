package com.roukaixin.cronvideos.config;

import com.roukaixin.cronvideos.pooled.PooledDownloader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置
 *
 * @author roukaixin
 * @date 2026/1/11 10:10
 */
@Configuration
public class DownloaderConfig {

    @Bean
    public PooledDownloader pooledDownloader() {
        return new PooledDownloader();
    }
}
