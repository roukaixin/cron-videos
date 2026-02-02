package com.roukaixin.cronvideos.pooled;

import com.roukaixin.cronvideos.downloader.Client;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 下载器代理类
 *
 * @author roukaixin
 * @date 2026/1/11 12:53
 */
@Slf4j
@Getter
public class PooledClient implements InvocationHandler {

    private static final Class<?>[] FACES = {Client.class};

    private static final String REMOVE = "remove";

    private static final String DISABLE = "disable";

    private static final String START = "start";

    private static final String STOP = "stop";

    private final Client realClient;

    private final Client proxyClient;

    private final PooledDownloader downloader;

    private boolean valid = false;

    public PooledClient(Client client, PooledDownloader downloader) {
        this.realClient = client;
        this.downloader = downloader;
        this.proxyClient = (Client) Proxy.newProxyInstance(Client.class.getClassLoader(), FACES, this);
        this.valid = this.proxyClient.start();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        switch (methodName) {
            case START -> {
                Object invoke = method.invoke(this.getRealClient(), args);
                if (invoke instanceof Boolean status) {
                    if (status) {
                        log.info("[{}]下载器启动成功", this.realClient.identifier());
                    } else {
                        log.error("[{}]下载器启动失败", this.realClient.identifier());
                    }
                }
                return invoke;
            }
            case STOP -> {
                return null;
            }
            case REMOVE -> {
                this.downloader.popClient(this.realClient.identifier());
                return null;
            }
            case DISABLE -> {
                return null;
            }
        }
        return method.invoke(this.getRealClient(), args);
    }
}
