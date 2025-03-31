package com.roukaixin.cronvideos.config;

import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Aria2Config {

    @Bean
    public Aria2WebSocketPool aria2WebSocketPool() {
        return new Aria2WebSocketPool();
    }
}
