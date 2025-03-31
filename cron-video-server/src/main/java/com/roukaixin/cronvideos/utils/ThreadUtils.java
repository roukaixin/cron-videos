package com.roukaixin.cronvideos.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadUtils {


    public static void sleep(TimeUnit timeUnit, long timeout) {
        try {
            timeUnit.sleep(timeout);
        } catch (InterruptedException e) {
            log.info("sleep 方法失败 {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
