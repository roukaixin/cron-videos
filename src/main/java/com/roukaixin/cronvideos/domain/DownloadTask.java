package com.roukaixin.cronvideos.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @TableName download_task
 */
@TableName(value = "download_task")
@Data
public class DownloadTask {

    /**
     * 任务唯一ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联的媒体 ID
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 任务更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}