package com.roukaixin.cronvideos.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Slf4j
public class Aria2Utils {

    private final static RestClient restClient = RestClient.create();

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
        return "http://" + ip + ":" + port + "/jsonrpc";
    }

    private static String getBody(String method, JSONArray params) {
        return getBody(System.currentTimeMillis(), method, params);
    }

    private static String getBody(Long id, String method, JSONArray params) {
        return JSONObject.of(
                "id", id,
                "jsonrpc", "2.0",
                "method", method,
                "params", params
        ).toJSONString();
    }

    private static void print(HttpRequest request, ClientHttpResponse response) throws IOException {
        log.info("请求失败 {} {}", response.getStatusCode(), response.getStatusText());
        log.info("具体原因 {}", new String(response.getBody().readAllBytes()));
    }

    public static String addUri(String ip, Integer port, String paramsJsonString) {
        return addUri(System.currentTimeMillis(), ip, port, paramsJsonString);
    }

    public static String addUri(Long id, String ip, Integer port, String paramsJsonString) {
        return exchange(id, ip, port, "aria2.addUri", paramsJsonString);
    }

    private static String exchange(String ip, Integer port, String method, String paramsJsonString) {
        return exchange(System.currentTimeMillis(), ip, port, method, paramsJsonString);
    }

    private static String exchange(Long id, String ip, Integer port, String method, String paramsJsonString) {

        URI uri = UriComponentsBuilder
                .fromUriString(getUri(ip, port))
                .queryParam("id", "{id}")
                .queryParam("method", "{method}")
                .queryParam("params", "{params}")
                .buildAndExpand(id, method, Base64Util.encode(paramsJsonString))
                .toUri();

        return restClient
                .get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, Aria2Utils::print)
                .body(String.class);
    }

}
