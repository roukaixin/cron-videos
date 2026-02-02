package com.roukaixin.cronvideos.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.roukaixin.cronvideos.handler.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DownloaderTypeEnum implements BaseEnum<Integer> {

    aria2(0, "aria2"),

    qbittorrent(1, "qbittorrent");

    @JsonValue
    private final int id;

    private final String name;

    @Override
    public Integer getCode() {
        return id;
    }
}
