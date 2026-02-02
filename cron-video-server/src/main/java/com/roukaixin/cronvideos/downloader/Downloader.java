package com.roukaixin.cronvideos.downloader;

/**
 * 下载器
 *
 * @author roukaixin
 * @date 2026/1/10 15:20
 */
public interface Downloader {

    /**
     * 获取客户端
     *
     * @return 下载器客户端
     */
    Client getClient();

}
