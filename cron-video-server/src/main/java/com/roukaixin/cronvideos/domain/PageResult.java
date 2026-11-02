package com.roukaixin.cronvideos.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页返回数据结构
 *
 * @author roukaixin
 * @date 2026/4/1 09:26
 */
@Setter
@Getter
@Builder
public class PageResult<T> {

    private long total;

    private List<T> list;

}
