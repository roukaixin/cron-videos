package com.roukaixin.cronvideos.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.roukaixin.cronvideos.domain.entity.base.BaseEntity;
import com.roukaixin.cronvideos.enums.LinkUsageType;
import com.roukaixin.cronvideos.enums.PlatformTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 网盘分享链接
 * 表名：storage_share_link
 *
 * @author roukaixin
 * @date 2026/4/10 15:34
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table("storage_share_link")
public class StorageShareLink extends BaseEntity {

    /**
     * 唯一 ID
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * 网盘提供商（1: 夸克, 2: 阿里云盘, 3: 百度网盘 等
     *
     * @see PlatformTypeEnum
     */
    @Column("platform")
    private PlatformTypeEnum platform;

    /**
     * 分享 ID (路径 ID)
     */
    @Column("share_id")
    private String shareId;

    /**
     * 提取码（部分网盘需要）
     */
    @Column("share_code")
    private String shareCode;

    /**
     * 过期时间 (部分网盘有限制)
     */
    @Column("expired_at")
    private LocalDateTime expiredAt;

    /**
     * 用途类型
     * {@link LinkUsageType}
     */
    private LinkUsageType linkUsage;

    /**
     * 是否失效（0:否、1:是）
     */
    @Column("is_lapse")
    private Integer isLapse;

    /**
     * 失效原因
     */
    @Column("lapse_cause")
    private String lapseCause;

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

}