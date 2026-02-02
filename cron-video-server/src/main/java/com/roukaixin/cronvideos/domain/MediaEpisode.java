package com.roukaixin.cronvideos.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 媒体库剧集信息
 *
 * @TableName media_episode
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MediaEpisode {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 与 media 关联
     */
    private Long mediaId;

    /**
     * 季集
     */
    private Integer seasonNumber;

    /**
     * 剧集
     */
    private Integer episodeNumber;

    /**
     * 播出时间
     */
    private LocalDateTime airDate;

    /**
     * 是否更新（0否，1是）
     */
    private Integer isUpdate;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;
}