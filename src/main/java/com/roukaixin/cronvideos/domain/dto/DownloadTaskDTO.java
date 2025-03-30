package com.roukaixin.cronvideos.domain.dto;

import lombok.Data;

@Data
public class DownloadTaskDTO {

    /**
     * 电影/电视剧名称
     */
    private String name;

    /**
     * 任务状态（0: 等待中, 1: 下载中, 2: 已完成, 3: 失败）
     */
    private Integer status;

    private Integer page = 1;

    private Integer pageSize = 10;
}