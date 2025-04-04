package com.roukaixin.cronvideos.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventUtils {

    private static ApplicationContext applicationContext;

    private final ApplicationContext ctx;

    public EventUtils(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @PostConstruct
    public void init() {
        EventUtils.applicationContext = ctx;
    }

    public static <T> void publishEvent(T data) {
        if (applicationContext != null) {
            ApplicationEventPublisher publisher = applicationContext;
            publisher.publishEvent(data);
            if (log.isDebugEnabled()) {
                log.debug("监听到的数据 -> {}", data);
            }
        } else {
            log.error("ApplicationContext is not set.");
        }
    }
}
