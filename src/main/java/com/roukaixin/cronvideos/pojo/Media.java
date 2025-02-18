package com.roukaixin.cronvideos.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 影视列表
 *
 * @TableName media
 */
@TableName(value = "media", autoResultMap = true)
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
     * 类型别名（如 电影=影片，电视剧=剧集）
     */
    private String typeAlias;

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
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> updateDays;

    /**
     * 首播/上映日期
     */
    private LocalDateTime releaseDate;

    /**
     * 匹配集数规则（仅电视剧用）
     */
    private String episodeRegex;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除字段（0未删除,1已删除）
     */
    @TableLogic
    private Integer isDeleted;
}