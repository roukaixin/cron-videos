package com.roukaixin.cronvideos.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {


    @ExceptionHandler(Exception.class)
    public void exception(Exception e) {
        log.error("全局异样捕获 -> ", e);
    }
}
