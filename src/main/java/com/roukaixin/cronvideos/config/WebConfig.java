package com.roukaixin.cronvideos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class WebConfig {

    @Bean
    public RestClient createRestClient() {
        return RestClient.builder().build();
    }

}
