package com.roukaixin.cronvideos.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.roukaixin.cronvideos.domain.entity.base.BaseEntity;
import com.roukaixin.cronvideos.enums.DownloadTaskStatusEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * 下载任务
 * 表名 download_task
 *
 * @author roukaixin
 * @date 2026/1/12 07:21
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table("download_task")
public class DownloadTask extends BaseEntity {

    /**
     * 主键 ID
     */
    @Id
    @Column(value = "id")
    private Long id;

    /**
     * 下载器 ID （关联 downloader 表 id）
     */
    @Column(value = "downloader_id")
    private Long downloaderId;

    /**
     * 下载文件信息ID （关联 download_file_info_id 表 id）
     */
    @Column("download_file_info_id")
    private Long downloadFileInfoId;

    /**
     * Aria2 任务 ID（通常是 16 位十六进制）对应 gid
     */
    @Column(value = "task_id")
    private String taskId;

    /**
     * 存储路径
     */
    @Column(value = "save_path")
    private String savePath;

    /**
     * 文件名
     */
    @Column(value = "save_file_name")
    private String saveFileName;

    /**
     * 文件大小
     */
    @Column(value = "size")
    private Long size;

    /**
     * 任务最终状态（-1初始化、0等待中、1下载中、2已完成、3失败）
     */
    @Column(value = "final_status")
    private DownloadTaskStatusEnum finalStatus;

    /**
     * 资源状态。0无、1只保留视频和音频、2已经移动到影视目录
     */
    @Column(value = "resource_status")
    private Integer resourceStatus;

}