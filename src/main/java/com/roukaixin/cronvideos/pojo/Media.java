package com.roukaixin.cronvideos.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 影视列表
 *
 * @TableName media
 */
@TableName(value = "media")
@Data
public class Media {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 电影/电视剧名称
     */
    private String title;

    /**
     * 类型: 电影 (movie) / 电视剧 (tv)
     */
    private String type;

    /**
     * 电视剧季号/部数（仅电视剧用）
     */
    private Integer seasonNumber;

    /**
     * 总集数（电影可以为 NULL）
     */
    private Integer totalEpisodes;

    /**
     * 已更新集数（仅电视剧用）
     */
    private Integer currentEpisode;

    /**
     * 电视剧更新日（仅电视剧用）
     */
    private String updateDays;

    /**
     * 首播/上映日期
     */
    private LocalDateTime releaseDate;

    /**
     * 网盘类型
     */
    private Integer storageType;

    /**
     * 匹配集数规则（仅电视剧用）
     */
    private String episodeRegex;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 逻辑删除字段（0未删除,1已删除）
     */
    private Integer isDeleted;
}