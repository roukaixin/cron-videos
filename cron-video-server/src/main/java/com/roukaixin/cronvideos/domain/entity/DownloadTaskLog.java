package com.roukaixin.cronvideos.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.roukaixin.cronvideos.domain.entity.base.BaseEntity;
import com.roukaixin.cronvideos.enums.DownloadTaskStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 下载任务日志
 *
 * @author roukaixin
 * @date 2026/11/2 19:30
 * @TableName download_task_log
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table("download_task_log")
public class DownloadTaskLog extends BaseEntity {

    /**
     * 主键ID
     */
    @Id
    @Column(value = "id")
    private Long id;

    /**
     * 下载任务ID
     */
    @Column("download_task_id")
    private Long downloadTaskId;

    /**
     * 上一个状态
     */
    @Column(value = "last_status")
    private DownloadTaskStatusEnum lastStatus;

    /**
     * 状态
     */
    @Column(value = "status")
    private DownloadTaskStatusEnum status;

}
