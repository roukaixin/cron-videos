package com.roukaixin.cronvideos.handler;

import com.roukaixin.cronvideos.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<String>> exception(Exception e) {
        log.error("全局异样捕获 -> ", e);
        return new ResponseEntity<>(
                R.<String>builder()
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(e.getMessage())
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
