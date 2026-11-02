package com.roukaixin.cronvideos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 启用状态
 *
 * @author roukaixin
 * @date 2026/4/15 19:58
 */
@Getter
@AllArgsConstructor
public enum DownloaderStatusEnum {


    ENABLE(1),

    DISABLE(0);

    @JsonValue
    private final Integer code;

    @JsonCreator
    public DownloaderStatusEnum formCode(Integer code) {
        return V.stream().filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }

    private final static List<DownloaderStatusEnum> V;

    static {
        V = List.of(values());
    }
}
