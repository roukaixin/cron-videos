package com.roukaixin.cronvideos.client;


import java.util.concurrent.TimeUnit;

public interface DownloaderClient {

    boolean start(long timeout, TimeUnit timeUnit) throws InterruptedException;

    void stop();

}
