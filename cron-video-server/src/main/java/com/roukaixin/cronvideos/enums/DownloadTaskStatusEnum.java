package com.roukaixin.cronvideos.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 任务状态
 *
 * @author roukaixin
 * @date 2026/4/9 21:38
 */
@Getter
@AllArgsConstructor
public enum DownloadTaskStatusEnum {

    INIT(-1, "初始化"),

    WAITING(0, "等待中"),

    DOWNLOADING(1, "下载中"),

    COMPLETED(2, "已完成"),

    FAILED(3, "失败");

    @JsonValue
    @EnumValue
    private final int code;

    private final String desc;

    private final static List<DownloadTaskStatusEnum> V;

    static {
        V = List.of(values());
    }
}
