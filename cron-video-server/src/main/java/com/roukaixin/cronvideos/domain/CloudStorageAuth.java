package com.roukaixin.cronvideos.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * 网盘认证信息存储
 * 表名 cloud_storage_auth
 *
 * @author roukaixin
 * @date 2026/1/12 18:36
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CloudStorageAuth {

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

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除字段（0: 正常, 1: 已删除）
     */
    private Integer isDeleted;
}