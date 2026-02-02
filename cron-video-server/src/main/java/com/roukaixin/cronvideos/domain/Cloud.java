package com.roukaixin.cronvideos.domain;

import com.roukaixin.cronvideos.enums.CloudProviderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 网盘分享链接
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cloud {

    /**
     * 唯一 ID
     */
    private Long id;

    /**
     * 关联的影视 ID
     */
    private Long mediaId;

    /**
     * 网盘提供商（1: 夸克, 2: 阿里云盘, 3: 百度网盘 等
     *
     * @see CloudProviderEnum
     */
    private CloudProviderEnum provider;

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

    /**
     * 匹配集数规则（用于匹配出重命名后文件名）
     */
    private String episodeRegex;

    /**
     * 只在指定目录下有效的目录路径
     */
    private String onlyInDir;

    /**
     * 排除的目录
     */
    private List<String> excludedDir;

    /**
     * 是否失效（0:否、1:是）
     */
    private Integer isLapse;

    /**
     * 失效原因
     */
    private String lapseCause;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除 (0=正常, 1=删除)
     */
    private Integer isDeleted;
}