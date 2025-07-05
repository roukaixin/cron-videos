package com.roukaixin.cronvideos.strategy;

import com.roukaixin.cronvideos.domain.Cloud;
import com.roukaixin.cronvideos.domain.Media;
import com.roukaixin.cronvideos.strategy.domain.FileInfo;

import java.util.List;

public interface CloudDrive {

    /**
     * 获取文件列表
     *
     * @param media               订阅媒体
     * @param cloud               网盘分享信息
     * @param needDownloadEpisode 未下载的集数
     * @return 文件列表
     */
    List<FileInfo> getFileList(Media media, Cloud cloud, List<Integer> needDownloadEpisode);

    /**
     * 下载文件
     *
     * @param media       订阅信息
     * @param filterVideo 视频文件
     * @return 下载成功
     */
    int download(Media media, FileInfo filterVideo);
}
