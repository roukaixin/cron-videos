package com.roukaixin.cronvideos.domain.base;

import lombok.*;

import java.time.LocalDateTime;

/**
 *
 * @author roukaixin
 * @date 2026/1/12 19:44
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 逻辑删除字段（0: 正常, 1: 删除）
     */
    private Integer isDeleted;
}
