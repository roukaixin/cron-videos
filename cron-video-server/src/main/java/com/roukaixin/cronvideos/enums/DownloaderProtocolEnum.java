package com.roukaixin.cronvideos.enums;

import com.roukaixin.cronvideos.handler.BaseEnum;
import lombok.AllArgsConstructor;

import java.util.Locale;

@AllArgsConstructor
public enum DownloaderProtocolEnum implements BaseEnum<String> {

    ws,

    wss,

    http,

    https;

    @Override
    public String getCode() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
