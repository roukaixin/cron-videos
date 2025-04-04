package com.roukaixin.cronvideos.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DownloaderProtocolEnum implements IEnum<String> {

    ws,
    wss,
    http,
    https;

    @Override
    @JsonValue
    public String getValue() {
        return name().toLowerCase();
    }
}
