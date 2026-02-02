package com.roukaixin.cronvideos.downloader;

public interface Executor {

    /**
     * 下载文件
     *
     * @param url 直链地址
     */
    void download(String url);
}
