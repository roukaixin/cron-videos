package com.roukaixin.cronvideos.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * 下载器下载任务
 * 表名 download_task
 *
 * @author roukaixin
 * @date 2026/1/12 07:21
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DownloadTask {

    /**
     * 任务唯一 ID
     */
    private Long id;

    /**
     * 关联 media 表 id
     */
    private Long mediaId;

    /**
     * 关联 downloader 表 id
     */
    private Long downloaderId;

    /**
     * Aria2 任务 ID（通常是 16/64 位字符串）
     */
    private String gid;

    /**
     * 集数
     */
    private Integer episodeNumber;

    /**
     * 存储路径
     */
    private String savePath;

    /**
     * 文件名
     */
    private String outName;

    /**
     * 媒体分辨率宽度
     */
    private Integer videoWidth;

    /**
     * 媒体分辨率高度
     */
    private Integer videoHeight;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 任务状态（0: 等待中, 1: 下载中, 2: 已完成, 3: 失败）
     */
    private Integer status;

    /**
     * 资源状态。0无、1只保留视频和音频、2已经自动到影视目录
     */
    private Integer resourceStatus;

    /**
     * 任务创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 任务更新时间
     */
    private LocalDateTime updatedAt;

}