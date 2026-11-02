package com.roukaixin.cronvideos.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 网盘类型
 *
 * @author roukaixin
 * @date 2026/4/6 19:35
 */
@Getter
@AllArgsConstructor
public enum PlatformTypeEnum {

    QUARK(1, "夸克网盘"),

    ALIYUN(2, "阿里云盘"),

    BAIDU(3, "百度网盘");

    @JsonValue
    private final Integer provider;

    private final String name;

    private final static List<PlatformTypeEnum> V;

    static {
        V = List.of(values());
    }
}
