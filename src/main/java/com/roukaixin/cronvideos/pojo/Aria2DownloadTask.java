package com.roukaixin.cronvideos.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @TableName aria2_download_task
 */
@TableName(value = "aria2_download_task")
@Data
public class Aria2DownloadTask {

    /**
     * 任务唯一ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Aria2 任务 ID（通常是 16/64 位字符串）
     */
    private String gid;

    /**
     * 关联的媒体 ID
     */
    private Long mediaId;

    /**
     * 关联的 aria2 服务 ID
     */
    private Long aria2ServiceId;

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
     * 文件大小
     */
    private Long size;

    /**
     * 任务状态（0: 等待中, 1: 下载中, 2: 已完成, 3: 失败）
     */
    private Integer status;

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

    /**
     * 逻辑删除字段（0未删除,1已删除）
     */
    @TableLogic
    private Integer isDeleted;
}