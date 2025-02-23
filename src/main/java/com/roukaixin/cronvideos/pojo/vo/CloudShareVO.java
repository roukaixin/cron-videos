package com.roukaixin.cronvideos.pojo.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 网盘分享链接
 *
 * @TableName cloud_shares
 */
@Setter
@Getter
public class CloudShareVO {

    /**
     * 唯一 ID
     */
    private Long id;

    /**
     * 关联的影视 ID
     */
    private Long mediaId;

    /**
     * 网盘提供商（1: 夸克, 2: 阿里云盘, 3: 百度网盘 等）
     */
    private Integer provider;

    /**
     * 分享 ID (路径 ID 或链接)
     */
    private String shareId;

    /**
     * 提取码（部分网盘需要）
     */
    private String shareCode;

    /**
     * 过期时间 (部分网盘有限制)
     */
    private LocalDateTime expiredAt;

    /**
     * 用于提取文件的正则表达式
     */
    private String fileRegex;
}