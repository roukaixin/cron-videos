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
     * Aria2 任务 ID（通常是 16 位十六进制）
     */
    @Column(value = "gid")
    private String gid;

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
     * 任务状态（0: 等待中, 1: 下载中, 2: 已完成, 3: 失败）
     */
    @Column(value = "status")
    private DownloadTaskStatusEnum status;

    /**
     * 资源状态。0无、1只保留视频和音频、2已经移动到影视目录
     */
    @Column(value = "resource_status")
    private Integer resourceStatus;

}