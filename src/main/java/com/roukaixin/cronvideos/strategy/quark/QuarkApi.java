package com.roukaixin.cronvideos.strategy.quark;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuarkApi {

    private final RestClient restClient;

    public QuarkApi(RestClient restClient) {
        this.restClient = restClient;
    }


    public Map<String, Object> shareSharepageToken(String pwdId) {
        Map<String, Object> map = new HashMap<>();
        String responseBody = restClient
                .post()
                .uri("https://drive-h.quark.cn/1/clouddrive/share/sharepage/token")
                .body(JSONObject.of(
                        "pwd_id", pwdId,
                        "passcode", ""
                ))
                .exchange((clientRequest, clientResponse) -> {
                    map.put("is_ok", clientResponse.getStatusCode().equals(HttpStatus.OK));
                    log(clientRequest, clientResponse);
                    return clientResponse.bodyTo(String.class);
                });
        map.put("data", responseBody);
        return map;
    }

    public String shareSharepageDetail(String pwdId, String stoken, String pdirFid, Integer page) {
        URI uri = getUriComponentsBuilder("https://drive-h.quark.cn/1/clouddrive/share/sharepage/detail")
                .queryParam("pwd_id", "{pwd_id}")
                .queryParam("stoken", "{stoken}")
                .queryParam("pdir_fid", "{pdir_fid}")
                .queryParam("_page", "{page}")
                .queryParam("_size", 100)
                .encode()
                .build()
                .expand(pwdId, stoken, pdirFid, page)
                .toUri();
        return restClient
                .get()
                .uri(uri)
                .exchange((clientRequest, clientResponse) -> {
                    String responseBody = "";
                    if (clientResponse.getStatusCode().equals(HttpStatus.OK)) {
                        responseBody = clientResponse.bodyTo(String.class);
                    }
                    log(clientRequest, clientResponse);
                    return responseBody;
                });
    }

    public String shareSharepageDetail(String pwdId, String pdirFid, Integer page) {
        URI uri = getUriComponentsBuilder("https://pan.quark.cn/1/clouddrive/share/sharepage/v2/detail")
                .build()
                .toUri();
        // size 最大支持 100
        return restClient
                .post()
                .uri(uri)
                .body(JSONObject.of(
                        "pwd_id", pwdId,
                        "pdir_fid", pdirFid,
                        "page", page,
                        "size", 100
                ))
                .exchange((clientRequest, clientResponse) -> {
                    String responseBody = "";
                    if (clientResponse.getStatusCode().equals(HttpStatus.OK)) {
                        responseBody = clientResponse.bodyTo(String.class);
                    }
                    log(clientRequest, clientResponse);
                    return responseBody;
                });
    }


    public String fileSort(String pdirFid, MultiValueMap<String, String> cookies) {
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/file/sort")
                .queryParam("pdir_fid", "{pdir_fid}")
                .encode().build().expand(pdirFid).toUri();
        return exchange(cookies, uri);
    }

    public String file(String pdirFid, String fileName, MultiValueMap<String, String> cookies) {
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/file")
                .build()
                .toUri();
        return restClient
                .post()
                .uri(uri)
                .cookies(cookie -> cookie.addAll(cookies))
                .body(JSONObject.of("pdir_fid", pdirFid, "file_name", fileName))
                .exchange((clientRequest, clientResponse) -> {
                    String responseBody = "";
                    if (clientResponse.getStatusCode().equals(HttpStatus.OK)) {
                        responseBody = clientResponse.bodyTo(String.class);
                    }
                    log(clientRequest, clientResponse);
                    return responseBody;
                });
    }

    public String shareSharepageSave(String pwdId, String stoken, String toPdirFid, List<String> fidList, MultiValueMap<String, String> cookies) {
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/share/sharepage/save")
                .build()
                .toUri();
        return restClient
                .post()
                .uri(uri)
                .cookies(c -> c.addAll(cookies))
                .body(JSONObject.of(
                                "pwd_id", pwdId,
                                "stoken", stoken,
                                "to_pdir_fid", toPdirFid,
                                "fid_list", fidList
                        )
                )
                .exchange((clientRequest, clientResponse) -> {
                    String responseBody = "";
                    if (clientResponse.getStatusCode().equals(HttpStatus.OK)) {
                        responseBody = clientResponse.bodyTo(String.class);
                    }
                    log(clientRequest, clientResponse);
                    return responseBody;
                });
    }

    public String task(String taskId, Integer retryIndex, MultiValueMap<String, String> cookies) {
        URI taskUri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/task")
                .queryParam("task_id", "{task_id}")
                .queryParam("retry_index", "{retry_index}")
                .queryParam("uc_param_str", "{uc_param_str}")
                .encode()
                .build()
                .expand(taskId, retryIndex, "")
                .toUri();
        return exchange(cookies, taskUri);
    }

    public String fileDelete(List<String> filelist, MultiValueMap<String, String> cookies) {
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/file/delete")
                .build()
                .toUri();
        return restClient
                .post()
                .uri(uri)
                .body(JSONObject.of(
                        "action_type", 1,
                        "filelist", filelist,
                        "exclude_fids", new JSONArray())
                )
                .cookies(c -> c.addAll(cookies))
                .exchange((clientRequest, clientResponse) -> {
                    String responseBody = "";
                    if (clientResponse.getStatusCode().equals(HttpStatus.OK)) {
                        responseBody = clientResponse.bodyTo(String.class);
                    }
                    log(clientRequest, clientResponse);
                    return responseBody;
                });
    }

    public Map<String, String> download(List<String> fids, MultiValueMap<String, String> cookies) {
        URI uri = getUriComponentsBuilder("https://drive.quark.cn/1/clouddrive/file/download")
                .build()
                .toUri();
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) quark-cloud-drive/2.5.20 Chrome/100.0.4896.160 Electron/18.3.5.4-b478491100 Safari/537.36 Channel/pckk_other_ch";
        Map<String, String> map = new HashMap<>();
        String response = restClient
                .post()
                .uri(uri)
                .header("User-Agent", userAgent)
                .cookies(c -> c.addAll(cookies))
                .body(JSONObject.of("fids", fids))
                .exchange((clientRequest, clientResponse) -> {
                    String responseBody = "";
                    if (clientResponse.getStatusCode().equals(HttpStatus.OK)) {
                        responseBody = clientResponse.bodyTo(String.class);
                    }
                    log(clientRequest, clientResponse);
                    map.put("cookies", getSetCookie(clientResponse.getHeaders().get("set-cookie")));
                    return responseBody;
                });
        map.put("response", response);
        return map;
    }

    private void log(HttpRequest request, RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("================QuarkApi================");
            log.debug("请求 URL : {}", request.getURI());
            log.debug("请求方法 : {}", request.getMethod());
            log.debug("响应状态 : {}", response.getStatusCode());
            log.debug("响应结果 : {}", response.bodyTo(String.class));
            log.debug("========================================");
        }
    }

    private UriComponentsBuilder getUriComponentsBuilder(String uri) {
        return UriComponentsBuilder
                .fromUriString(uri)
                .queryParam("pr", "ucpro")
                .queryParam("fr", "pc");
    }

    private String getSetCookie(List<String> cookies) {
        String cookie = "";
        if (cookies != null) {
            for (String string : cookies) {
                String[] split = string.split(";");
                cookie = Arrays.stream(split)
                        .filter(a -> a.contains("__puus="))
                        .collect(Collectors.joining("; "));
            }
        }
        return cookie;
    }

    private String exchange(MultiValueMap<String, String> cookies, URI uri) {
        return restClient
                .get()
                .uri(uri)
                .cookies(c -> c.addAll(cookies))
                .exchange((clientRequest, clientResponse) -> {
                    String responseBody = "";
                    if (clientResponse.getStatusCode().equals(HttpStatus.OK)) {
                        responseBody = clientResponse.bodyTo(String.class);
                    }
                    log(clientRequest, clientResponse);
                    return responseBody;
                });
    }
}
