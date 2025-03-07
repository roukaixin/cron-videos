package com.roukaixin.cronvideos.pojo.vo;

import lombok.Data;

@Data
public class Aria2DownloadTaskVO {

    /**
     * 任务唯一ID
     */
    private Long id;

    /**
     * Aria2 任务 ID（通常是 16/64 位字符串）
     */
    private String gid;

    /**
     * 关联的媒体 ID
     */
    private Long mediaId;

    /**
     * 关联的 aria2 服务 ID
     */
    private Long aria2ServiceId;

    /**
     * 集数
     */
    private Integer episodeNumber;

    /**
     * 存储路径
     */
    private String savePath;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 任务状态（0: 等待中, 1: 下载中, 2: 已完成, 3: 失败）
     */
    private Integer status;
}