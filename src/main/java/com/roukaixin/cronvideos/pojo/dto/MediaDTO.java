package com.roukaixin.cronvideos.pojo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class MediaDTO {

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
    private List<String> updateDays;

    /**
     * 首播/上映日期
     */
    private LocalDateTime releaseDate;

    /**
     * 匹配集数规则（仅电视剧用）
     */
    private String episodeRegex;
}