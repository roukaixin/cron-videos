package com.roukaixin.cronvideos.api.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Episode {

    /**
     * 季集
     */
    @JsonAlias("season_number")
    private Integer seasonNumber;

    /**
     * 剧集
     */
    @JsonAlias("episode_number")
    private Integer episodeNumber;

    /**
     * 播出时间
     */
    @JsonAlias("air_date")
    private LocalDateTime airDate;
}
