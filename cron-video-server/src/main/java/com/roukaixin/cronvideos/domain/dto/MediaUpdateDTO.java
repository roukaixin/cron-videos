package com.roukaixin.cronvideos.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class MediaUpdateDTO {


    /**
     * 类型别名（如 电影=影片，电视剧=剧集）
     */
    private String typeAlias;


    /**
     * 总集数（电影可以为 NULL）
     */
    private Integer totalEpisode;

    /**
     * 开始集数（beginEpisode）
     */
    private Integer startEpisode;

    /**
     * 电视剧更新日（仅电视剧用）
     */
    private List<Integer> updateDay;

    /**
     * 首播/上映日期
     */
    private LocalDateTime releaseDate;

}