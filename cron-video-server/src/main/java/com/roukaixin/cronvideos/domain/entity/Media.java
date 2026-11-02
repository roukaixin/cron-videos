package com.roukaixin.cronvideos.domain.entity;

import com.roukaixin.cronvideos.domain.entity.base.BaseEntity;
import com.roukaixin.cronvideos.enums.MediaTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 媒体列表
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Media extends BaseEntity {

    /**
     * id
     */
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
    private Integer startEpisode;

    /**
     * 首播/上映日期
     */
    private LocalDateTime releaseDate;

    /**
     * tmdb id
     */
    private String tmdbId;

}