package com.roukaixin.cronvideos.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DownloaderTypeEnum {

    aria2(0, "aria2"),
    qbittorrent(1, "qbittorrent");

    @EnumValue
    @JsonValue
    private final int id;

    private final String name;

}
