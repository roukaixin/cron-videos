package com.roukaixin.cronvideos.pojo.dto;

import lombok.Data;

@Data
public class Aria2DownloadTaskDTO {

    /**
     * 电影/电视剧名称
     */
    private String title;

    /**
     * 任务状态（0: 等待中, 1: 下载中, 2: 已完成, 3: 失败）
     */
    private Integer status;

    private Integer page = 1;

    private Integer pageSize = 10;
}