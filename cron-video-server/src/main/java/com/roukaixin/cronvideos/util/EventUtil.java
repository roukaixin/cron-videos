package com.roukaixin.cronvideos.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventUtil {

    private static ApplicationEventPublisher applicationEventPublisher;

    private final ApplicationEventPublisher ctx;

    public EventUtil(ApplicationEventPublisher ctx) {
        this.ctx = ctx;
    }

    @PostConstruct
    public void init() {
        EventUtil.applicationEventPublisher = ctx;
    }

    public static <T> void publishEvent(T data) {
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(data);
            if (log.isDebugEnabled()) {
                log.debug("监听到的数据 -> {}", data);
            }
        } else {
            log.error("ApplicationContext is not set.");
        }
    }
}
