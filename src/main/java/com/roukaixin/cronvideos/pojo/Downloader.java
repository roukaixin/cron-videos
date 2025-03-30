package com.roukaixin.cronvideos.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.roukaixin.cronvideos.enums.DownloaderEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * aria2 连接信息
 *
 * @TableName downloader
 */
@TableName(value = "downloader")
@Data
public class Downloader {

    /**
     * 唯一 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 下载器类型。（0->aria2、1->qbittorrent）
     */
    private DownloaderEnum type;

    /**
     * 协议。ws/http
     */
    private String protocol;

    /**
     * 下载器主机地址（IP 或域名）
     */
    private String host;

    /**
     * 监听端口
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
     * 在线状态（1: 在线, 0: 离线）
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