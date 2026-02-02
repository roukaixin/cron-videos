package com.roukaixin.cronvideos.downloader;

/**
 * 下载器客户端
 *
 * @author roukaixin
 * @date 2026/1/11 10:55
 */
public interface Client {

    /**
     * 执行器
     *
     * @return 执行器
     */
    Executor getExecutor();

    /**
     * 下载器标识
     *
     * @return 标识
     */
    Long identifier();

    /**
     * 权重
     */
    int weight();

    /**
     * 启动
     */
    boolean start();

    /**
     * 停止
     */
    void stop();

    /**
     * 是否可以用
     *
     * @return b
     */
    boolean ping();
}
