package com.roukaixin.cronvideos.domain.dto;

import com.roukaixin.cronvideos.enums.DownloaderProtocolEnum;
import com.roukaixin.cronvideos.enums.DownloaderTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DownloaderDTO {

    /**
     * 下载器类型。（0->aria2、1->qbittorrent）
     */
    private DownloaderTypeEnum type;

    /**
     * 协议。ws/http
     */
    private DownloaderProtocolEnum protocol;

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
