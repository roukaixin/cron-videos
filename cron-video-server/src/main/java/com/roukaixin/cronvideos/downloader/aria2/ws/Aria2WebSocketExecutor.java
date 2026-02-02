package com.roukaixin.cronvideos.downloader.aria2.ws;

import com.roukaixin.cronvideos.downloader.Executor;

public class Aria2WebSocketExecutor implements Executor {

    private final Aria2WebSocketClient client;

    protected Aria2WebSocketExecutor(Aria2WebSocketClient client) {
        this.client = client;
    }

    @Override
    public void download(String url) {

    }
}
