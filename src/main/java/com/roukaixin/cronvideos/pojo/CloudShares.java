package com.roukaixin.cronvideos.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网盘分享链接
 *
 * @TableName cloud_shares
 */
@TableName(value = "cloud_shares")
@Data
public class CloudShares {
    /**
     * 唯一 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的影视 ID
     */
    private Long mediaId;

    /**
     * 网盘类型 (0=夸克, 1=百度, 2=阿里云, 3=其他)
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
     * 逻辑删除 (0=正常, 1=删除)
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;
}