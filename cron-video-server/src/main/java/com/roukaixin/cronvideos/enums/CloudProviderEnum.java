package com.roukaixin.cronvideos.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CloudProviderEnum {

    QUARK(1, "夸克网盘"),
    ALIYUN(2, "阿里云盘"),
    BAIDU(3, "百度网盘");

    @EnumValue
    @JsonValue
    private final Integer provider;

    private final String name;
}
