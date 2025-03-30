package com.roukaixin.cronvideos.pojo.dto;

import com.roukaixin.cronvideos.enums.DownloaderEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DownloaderDTO {

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
}
