package com.roukaixin.cronvideos.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 媒体库剧集信息
 *
 * @TableName media_episode
 */
@TableName(value = "media_episode")
@Data
public class MediaEpisode {
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}