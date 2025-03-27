package com.roukaixin.cronvideos.pojo.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.roukaixin.cronvideos.enums.CloudShareProviderEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private CloudShareProviderEnum provider;

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
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
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
     *  是否失效（0:否、1:是）
     */
    private Integer isLapse;

    /**
     * 失效原因
     */
    private String lapseCause;
}