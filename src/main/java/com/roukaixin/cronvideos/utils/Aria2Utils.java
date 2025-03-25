package com.roukaixin.cronvideos.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Base64;

@Slf4j
public class Aria2Utils {

    private final static RestClient restClient = RestClient.builder()
            .requestFactory(new BufferingClientHttpRequestFactory(new JdkClientHttpRequestFactory()))
            .build();

    public static String getDir(String ip, Integer port, String secret) {
        JSONArray params = new JSONArray();
        if (secret != null && !secret.isEmpty()) {
            params.add("token:" + secret);
        }
        String body = exchange(ip, port, "aria2.getGlobalOption", params.toJSONString());
        String dir = "";
        if (body != null) {
            dir = JSONObject.parseObject(body).getJSONObject("result").getString("dir");
        }
        return dir;
    }

    private static String getUri(String ip, Integer port) {
        return getUri("http", ip, port);
    }

    public static String getUri(String scheme, String ip, Integer port) {
        return  scheme + "://" + ip + ":" + port + "/jsonrpc";
    }

    public static String removeDownloadResult(String ip, Integer port, String paramsJsonString) {
        return removeDownloadResult(System.currentTimeMillis(), ip, port, paramsJsonString);
    }

    public static String removeDownloadResult(Long id, String ip, Integer port, String paramsJsonString) {
        return exchange(id, ip, port, "aria2.removeDownloadResult", paramsJsonString);
    }

    public static String tellStatus(String ip, Integer port, String paramsJsonString) {
        return tellStatus(System.currentTimeMillis(), ip, port, paramsJsonString);
    }

    public static String tellStatus(Long id, String ip, Integer port, String paramsJsonString) {
        return exchange(id, ip, port, "aria2.tellStatus", paramsJsonString);
    }

    public static String addUri(String ip, Integer port, String paramsJsonString) {
        return addUri(System.currentTimeMillis(), ip, port, paramsJsonString);
    }

    public static String addUri(Long id, String ip, Integer port, String paramsJsonString) {
        return exchange(id, ip, port, "aria2.addUri", paramsJsonString);
    }

    public static String exchange(String ip, Integer port, String method, String paramsJsonString) {
        return exchange(System.currentTimeMillis(), ip, port, method, paramsJsonString);
    }

    public static String exchange(Long id, String ip, Integer port, String method, String paramsJsonString) {
        URI uri = UriComponentsBuilder
                .fromUriString(getUri(ip, port))
                .queryParam("id", "{id}")
                .queryParam("method", "{method}")
                .queryParam("params", "{params}")
                .buildAndExpand(id, method, Base64.getEncoder().encodeToString(paramsJsonString.getBytes()))
                .toUri();
        return restClient
                .get()
                .uri(uri)
                .exchange((clientRequest, clientResponse) -> {
                    String response = "";
                    if (clientResponse.getStatusCode().equals(HttpStatus.OK)) {
                        response = clientResponse.bodyTo(String.class);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("==================ARIA2=================");
                        log.debug("请求 URL : {}", clientRequest.getURI());
                        log.debug("请求方法 : {}", clientRequest.getMethod());
                        log.debug("请求参数 : {}", JSONObject.of(
                                "id", id,
                                "method", method,
                                "params", paramsJsonString
                        ));
                        log.debug("响应状态 : {}", clientResponse.getStatusCode());
                        log.debug("响应结果 : {}", clientResponse.bodyTo(String.class));
                        log.debug("========================================");
                    }
                    return response;
                });
    }

}
