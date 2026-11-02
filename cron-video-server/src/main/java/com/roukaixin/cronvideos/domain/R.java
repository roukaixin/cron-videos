package com.roukaixin.cronvideos.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 统一返回类
 *
 * @author roukaixin
 * @date 2026/4/1 09:25
 */
@Setter
@Getter
@Builder
public class R<T> {

    private Integer code;

    private String message;

    private T data;

    public static <T> R<T> success(String message) {
        return R.success(null, message);
    }

    public static <T> R<T> success(T data, String message) {
        return R.<T>builder().code(200).data(data).message(message).build();
    }
}
