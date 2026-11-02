package com.roukaixin.cronvideos.enums;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public enum DownloaderProtocolEnum {

    ws,

    wss,

    http,

    https;

    private final static List<DownloaderProtocolEnum> V;

    static {
        V = List.of(values());
    }
}
