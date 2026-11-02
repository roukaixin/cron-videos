package com.roukaixin.cronvideos.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.roukaixin.cronvideos.domain.entity.base.BaseEntity;
import com.roukaixin.cronvideos.enums.DownloaderProtocolEnum;
import com.roukaixin.cronvideos.enums.DownloaderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


/**
 * 下载器信息
 * 表名 downloader
 *
 * @author roukaixin
 * @date 2026/1/12 08:09
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(value = "downloader")
public class Downloader extends BaseEntity {

    /**
     * 唯一 ID
     */
    @Id
    @Column(value = "id")
    private Long id;

    /**
     * 下载器类型。（0->aria2、1->qbittorrent）
     */
    @Column(value = "type")
    private DownloaderTypeEnum type;

    /**
     * 协议。ws/http
     */
    @Column(value = "protocol")
    private DownloaderProtocolEnum protocol;

    /**
     * 下载器主机地址（IP 或域名）
     */
    @Column(value = "host")
    private String host;

    /**
     * 监听端口
     */
    @Column(value = "port")
    private Integer port;

    /**
     * 账号
     */
    @Column(value = "access_key")
    private String accessKey;

    /**
     * RPC 密钥（Token）
     */
    @Column(value = "secret_key")
    private String secretKey;

    /**
     * 服务器权重（用于负载均衡）
     */
    @Column(value = "weight")
    private Integer weight;

    /**
     * 是否在线（1:在线、0:离线）
     */
    @Column(value = "is_online")
    private Integer isOnline;

    /**
     * 启用状态 1启动 0禁用
     */
    @Column(value = "status")
    private Integer status;
}