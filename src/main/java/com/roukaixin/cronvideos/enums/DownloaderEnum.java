package com.roukaixin.cronvideos.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DownloaderEnum {

    ARIA2(1, "aria2"),
    qbittorrent(2, "qbittorrent");

    @EnumValue
    @JsonValue
    private final int id;

    private final String name;

}
