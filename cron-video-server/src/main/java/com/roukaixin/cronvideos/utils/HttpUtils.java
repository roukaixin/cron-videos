package com.roukaixin.cronvideos.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class HttpUtils {

    private HttpUtils() {

    }

    public final static HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();


    public static HttpResponse<String> send(java.net.http.HttpRequest request) {
        try {
            java.net.http.HttpResponse.BodyHandler<String> string = java.net.http.HttpResponse.BodyHandlers.ofString();
            java.net.http.HttpResponse<String> send = HTTP_CLIENT.send(request, string);
            return HttpResponse.<String>builder().statusCode(send.statusCode()).body(send.body()).build();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class HttpRequest {

        public static HttpRequestBuilder builder() {
            return new HttpRequestBuilder();
        }


        public static class HttpRequestBuilder {

            private String url;

            private String method;

            private Object body;

            private final Map<String, String> query = new HashMap<>();

            public HttpRequestBuilder method(String method) {
                this.method = method;
                return this;
            }

            public HttpRequestBuilder GET() {
                this.method = "GET";
                return this;
            }

            public HttpRequestBuilder POST() {
                this.method = "POST";
                return this;
            }

            public HttpRequestBuilder url(String url) {
                this.url = url;
                return this;
            }

            public HttpRequestBuilder query(String k, String v) {
                query.put(k, v);
                return this;
            }

            public HttpRequestBuilder body(Object body) {
                this.body = body;
                return this;
            }


            public java.net.http.HttpRequest build() {
                assert method != null;
                assert !method.isEmpty();
                if (method.equals("GET")) {
                    this.url = this.url + "?" + query.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
                }
                return java.net.http.HttpRequest.newBuilder()
                        .uri(URI.create(this.url))
                        .method(this.method, Objects.nonNull(body) ? java.net.http.HttpRequest.BodyPublishers.ofString(JsonUtils.toJSONString(body)) : java.net.http.HttpRequest.BodyPublishers.noBody())
                        .build();
            }

        }
    }

    @Setter
    @Getter
    @Builder
    public static class HttpResponse<T> {

        private int statusCode;

        private T body;
    }
}
