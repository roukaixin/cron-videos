package com.roukaixin.cronvideos.pojo.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Aria2ServerVO {

    /**
     * 唯一 ID
     */
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

}
