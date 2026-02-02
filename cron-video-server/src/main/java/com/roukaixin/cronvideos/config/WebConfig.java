package com.roukaixin.cronvideos.config;

import com.roukaixin.cronvideos.algorithm.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Configuration
@Slf4j
public class WebConfig {

    @Bean
    public RestClient createRestClient() {
        return RestClient
                .builder()
                .requestFactory(new BufferingClientHttpRequestFactory(new JdkClientHttpRequestFactory()))
                .build();
    }

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker() {
        return new SnowflakeIdWorker(1, 1);
    }

    @Bean
    public JsonMapper jsonMapper() {
        SimpleModule longToString = new SimpleModule();
        longToString.addSerializer(Long.class, ToStringSerializer.instance);
        return JsonMapper
                .builder()
                .addModule(longToString)
                .build();
    }

}
