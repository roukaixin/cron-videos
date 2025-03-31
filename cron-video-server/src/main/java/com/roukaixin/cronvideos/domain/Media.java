package com.roukaixin.cronvideos.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.roukaixin.cronvideos.enums.MediaTypeEnum;
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
    private String name;

    /**
     * 媒体类型: movie / show
     *
     * @see MediaTypeEnum
     */
    private MediaTypeEnum type;

    /**
     * 媒体列别（如 电影=影片，电视剧=剧集）
     */
    private String typeAlias;

    /**
     * 电视剧季号/部数（仅电视剧用、电影为 NULL）
     */
    private Integer seasonNumber;

    /**
     * 总集数（电影默认为1）
     */
    private Integer totalEpisode;

    /**
     * 开始集数（beginEpisode）
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS, insertStrategy = FieldStrategy.ALWAYS)
    private Integer startEpisode;

    /**
     * 首播/上映日期
     */
    private LocalDateTime releaseDate;

    /**
     * tmdb id
     */
    private String tmdbId;


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