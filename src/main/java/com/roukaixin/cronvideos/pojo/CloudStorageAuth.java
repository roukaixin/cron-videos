package com.roukaixin.cronvideos.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网盘认证信息存储
 *
 * @TableName cloud_storage_auth
 */
@TableName(value = "cloud_storage_auth")
@Data
public class CloudStorageAuth {

    /**
     * 唯一 ID
     */
    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除字段（0: 正常, 1: 已删除）
     */
    @TableLogic
    private Integer isDeleted;
}