package com.roukaixin.cronvideos.domain.entity.base;

import com.mybatisflex.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 实体类共用字段
 *
 * @author roukaixin
 * @date 2026/1/12 19:44
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseEntity {

    /**
     * 创建时间
     */
    @Column(value = "create_date")
    private LocalDateTime createDate;

    /**
     * 更新时间
     */
    @Column(value = "update_date")
    private LocalDateTime updateDate;

    /**
     * 逻辑删除字段（0: 正常, 1: 删除）
     */
    @Column(value = "is_deleted", isLogicDelete = true)
    private Long isDeleted;
}
