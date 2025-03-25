package com.roukaixin.cronvideos.pojo.vo;

import lombok.Data;

@Data
public class Aria2DownloadTaskPageVO {

    /**
     * 任务唯一ID
     */
    private Long id;

    /**
     * Aria2 任务 ID（通常是 16/64 位字符串）
     */
    private String gid;

    /**
     * 电影/电视剧名称
     */
    private String name;

    /**
     * 集数
     */
    private Integer episodeNumber;

    /**
     * 存储路径
     */
    private String savePath;

    /**
     * 文件名
     */
    private String outName;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 任务状态（0: 等待中, 1: 下载中, 2: 已完成, 3: 失败）
     */
    private Integer status;
}