package com.roukaixin.cronvideos.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum DownloaderTypeEnum {

    ARIA2(0),

    QBITTORRENT(1);

    @JsonValue
    @EnumValue
    private final int id;

    private final static List<DownloaderTypeEnum> V;

    static {
        V = List.of(values());
    }

}
