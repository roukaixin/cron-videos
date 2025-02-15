package com.roukaixin.cronvideos.pojo.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 网盘认证信息存储
 *
 * @TableName cloud_storage_auth
 */
@Setter
@Getter
public class CloudStorageAuthVO {

    /**
     * 唯一 ID
     */
    private Long id;

    /**
     * 网盘提供商（1: 夸克, 2: 阿里云盘, 3: 百度网盘 等）
     */
    private Integer provider;

    /**
     * 访问令牌（某些网盘用）
     */
    private String accessToken;

    /**
     * 刷新令牌（某些网盘用）
     */
    private String refreshToken;

    /**
     * 网盘 Cookie（如夸克需要的）
     */
    private String cookie;
}