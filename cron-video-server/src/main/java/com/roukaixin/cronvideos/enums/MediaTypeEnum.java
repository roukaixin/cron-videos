package com.roukaixin.cronvideos.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MediaTypeEnum {

    tv("tv"),
    movie("movie");

    @EnumValue
    @JsonValue
    private final String name;
}
