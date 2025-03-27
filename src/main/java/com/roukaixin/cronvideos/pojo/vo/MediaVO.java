package com.roukaixin.cronvideos.pojo.vo;

import com.roukaixin.cronvideos.enums.MediaTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class MediaVO {

    /**
     * id
     */
    private Long id;

    /**
     * 电影/电视剧名称
     */
    private String name;

    /**
     * 类型: movie / show
     */
    private MediaTypeEnum type;

    /**
     * 类型别名（如 电影=影片，电视剧=剧集）
     */
    private String typeAlias;

    /**
     * 电视剧季号/部数（仅电视剧用、电影为 NULL）
     */
    private Integer season;

    /**
     * 总集数（电影默认为1）
     */
    private Integer totalEpisode;

    /**
     * 开始集数（beginEpisode）
     */
    private Integer startEpisode;

    /**
     * 已更新集数
     */
    private Integer currentEpisode;

    /**
     * 电视剧更新日（仅电视剧用、电影为 NULL）
     */
    private List<Integer> updateDay;

    /**
     * 首播/上映日期
     */
    private LocalDateTime releaseDate;
}