package com.roukaixin.cronvideos.listener.event;

import com.roukaixin.cronvideos.domain.Downloader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 下载器事件类
 *
 * @author roukaixin
 * @date 2026/1/13 19:12
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DownloaderEvent {

    /**
     * 操作类型
     */
    private Operation operation;

    /**
     * 下载器
     */
    private Downloader downloader;


    public static enum Operation {

        QUERY,

        SAVE,

        UPDATE,

        DELETE,

    }
}
