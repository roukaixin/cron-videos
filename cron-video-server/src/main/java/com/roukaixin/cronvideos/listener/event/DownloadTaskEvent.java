package com.roukaixin.cronvideos.listener.event;

import lombok.*;

/**
 * 下载任务事件类
 *
 * @author roukaixin
 * @date 2026/1/28 23:14
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DownloadTaskEvent {

    /**
     * 下载器 ID
     */
    private Long downloaderId;

    /**
     * 状态
     */
    private int status;

    /**
     * 任务 ID
     */
    private String gid;

    /**
     * 原状态
     */
    private int originalStatus;

}
