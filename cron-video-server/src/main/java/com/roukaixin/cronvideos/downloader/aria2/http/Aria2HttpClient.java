package com.roukaixin.cronvideos.downloader.aria2.http;

import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.downloader.Client;
import com.roukaixin.cronvideos.downloader.Executor;
import com.roukaixin.cronvideos.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

@Slf4j
public class Aria2HttpClient implements Client {

    private final Aria2HttpExecutor executor = new Aria2HttpExecutor();

    private final Long identifier;

    private final String scheme;

    private final String host;

    private final Integer port;

    private final String password;

    private final int weight;


    public Aria2HttpClient(Downloader downloader) {
        this.identifier = downloader.getId();
        this.scheme = downloader.getProtocol().name().toLowerCase(Locale.ROOT);
        this.host = downloader.getHost();
        this.port = downloader.getPort();
        this.password = downloader.getSecret();
        this.weight = downloader.getWeight();
    }


    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public Long identifier() {
        return this.identifier;
    }

    @Override
    public int weight() {
        return this.weight;
    }

    @Override
    public boolean start() {
        return ping();
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean ping() {
        try {
            String params = "";
            if (StringUtils.hasText(password)) {
                params = Base64.getEncoder().encodeToString(("[\"token:" + password + "\"]").getBytes(StandardCharsets.UTF_8));
                URLEncoder.encode(params, StandardCharsets.UTF_8);
            }
            HttpRequest request = HttpUtils.HttpRequest.builder()
                    .GET()
                    .url(this.scheme + "://" + this.host + ":" + this.port + "/jsonrpc")
                    .query("id", identifier + System.currentTimeMillis() + "")
                    .query("method", "aria2.getVersion")
                    .query("params", params)
                    .build();
            HttpUtils.HttpResponse<String> response = HttpUtils.send(request);
            if (log.isDebugEnabled()) {
                log.debug("http aria2 发生请求结果[code:{}][body:{}]", response.getStatusCode(), response.getBody());
            }
            return response.getStatusCode() == 200;
        } catch (Exception e) {
            log.error("http aria2 连接异常", e);
            return false;
        }
    }
}
