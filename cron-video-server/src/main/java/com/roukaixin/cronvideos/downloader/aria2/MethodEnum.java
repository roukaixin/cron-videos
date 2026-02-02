package com.roukaixin.cronvideos.downloader.aria2;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * aria2 方法
 *
 * @author roukaixin
 * @date 2026/2/2 21:55
 */
@Getter
@AllArgsConstructor
public enum MethodEnum {

    aria2_getVersion("aria2.getVersion"),

    aria2_onDownloadStart("aria2.onDownloadStart"),

    aria2_onDownloadComplete("aria2.onDownloadComplete"),

    aria2_onDownloadError("aria2.onDownloadError"),

    ;

    private final String method;
}
