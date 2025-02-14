package com.roukaixin.cronvideos.pojo;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class R<T> {

    private Integer code;

    private String message;

    private T data;

}
