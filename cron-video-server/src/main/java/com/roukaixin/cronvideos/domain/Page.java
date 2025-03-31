package com.roukaixin.cronvideos.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class Page<T> {

    private Integer total;

    private List<T> list;

}
