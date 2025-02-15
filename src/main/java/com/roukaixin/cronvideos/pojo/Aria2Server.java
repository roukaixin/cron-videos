package com.roukaixin.cronvideos.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * aria2 连接信息
 *
 * @TableName aria2_server
 */
@TableName(value = "aria2_server")
@Data
public class Aria2Server {

    /**
     * 唯一 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * aria2 服务器地址（IP 或域名）
     */
    private String ip;

    /**
     * 端口号，默认 6800
     */
    private Integer port;

    /**
     * RPC 密钥（Token）
     */
    private String secret;

    /**
     * 服务器权重（用于负载均衡）
     */
    private Integer weight;

    /**
     * 当前任务数（用于最少连接数调度）
     */
    private Integer currentTasks;

    /**
     * aria2 在线状态（1: 在线, 0: 离线）
     */
    private Integer isOnline;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除字段（0: 正常, 1: 删除）
     */
    @TableLogic
    private Integer isDeleted;
}