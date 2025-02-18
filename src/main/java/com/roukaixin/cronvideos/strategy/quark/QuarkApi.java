package com.roukaixin.cronvideos.strategy.quark;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

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


    public String shareSharepageToken(String pwdId) {
        ResponseEntity<String> response = restClient
                .post()
                .uri("https://drive-h.quark.cn/1/clouddrive/share/sharepage/token")
                .body(JSONObject.of(
                        "pwd_id", pwdId,
                        "passcode", ""
                ))
                .retrieve()
                .toEntity(String.class);
        log("https://drive-h.quark.cn/1/clouddrive/share/sharepage/token", HttpMethod.GET.name(), response);
        return response.getBody();
    }

    public String shareSharepageDetail(String pwdId, String stoken, String pdirFid) {
        URI uri = getUriComponentsBuilder("https://drive-h.quark.cn/1/clouddrive/share/sharepage/detail")
                .queryParam("pwd_id", "{pwd_id}")
                .queryParam("stoken", "{stoken}")
                .queryParam("pdir_fid", "{pdir_fid}")
                .queryParam("_page", 1)
                .queryParam("_size", 1000)
                .encode()
                .build()
                .expand(pwdId, stoken, pdirFid)
                .toUri();
        ResponseEntity<String> response = restClient
                .get()
                .uri(uri)
                .retrieve().toEntity(String.class);
        log("https://drive-h.quark.cn/1/clouddrive/share/sharepage/detail", HttpMethod.GET.name(), response);
        return response.getBody();
    }

    public String fileSort(String pdirFid, MultiValueMap<String, String> cookies) {
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/file/sort")
                .queryParam("pdir_fid", "{pdir_fid}")
                .encode().build().expand(pdirFid).toUri();
        ResponseEntity<String> response = restClient
                .get()
                .uri(uri)
                .cookies(c -> c.addAll(cookies))
                .retrieve()
                .toEntity(String.class);
        log("https://drive-pc.quark.cn/1/clouddrive/file/sort", HttpMethod.GET.name(), response);
        return response.getBody();
    }

    public String file(String pdirFid, String fileName, MultiValueMap<String, String> cookies) {
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/file")
                .build()
                .toUri();
        ResponseEntity<String> response = restClient
                .post()
                .uri(uri)
                .cookies(cookie -> cookie.addAll(cookies))
                .body(JSONObject.of("pdir_fid", pdirFid, "file_name", fileName))
                .retrieve()
                .toEntity(String.class);
        log("https://drive-pc.quark.cn/1/clouddrive/file", HttpMethod.POST.name(), response);
        return response.getBody();
    }

    public String shareSharepageSave(String pwdId, String stoken, String toPdirFid, List<String> fidList, MultiValueMap<String, String> cookies) {
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/share/sharepage/save")
                .build()
                .toUri();
        ResponseEntity<String> response = restClient
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
                .retrieve()
                .toEntity(String.class);
        log("https://drive-pc.quark.cn/1/clouddrive/share/sharepage/save", HttpMethod.POST.name(), response);
        return response.getBody();
    }

    public String task(String taskId, MultiValueMap<String, String> cookies) {
        URI taskUri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/task")
                .queryParam("task_id", "{task_id}")
                .encode()
                .build()
                .expand(taskId)
                .toUri();
        ResponseEntity<String> response = restClient
                .get()
                .uri(taskUri)
                .cookies(c -> c.addAll(cookies))
                .retrieve()
                .toEntity(String.class);
        log("https://drive-pc.quark.cn/1/clouddrive/task", HttpMethod.GET.name(), response);
        return response.getBody();
    }

    public String fileDelete(List<String> filelist, MultiValueMap<String, String> cookies) {
        URI uri = getUriComponentsBuilder("https://drive-pc.quark.cn/1/clouddrive/file/delete")
                .build()
                .toUri();
        ResponseEntity<String> response = restClient
                .post()
                .uri(uri)
                .body(JSONObject.of(
                        "action_type", 2,
                        "filelist", filelist,
                        "exclude_fids", new JSONArray())
                )
                .cookies(c -> c.addAll(cookies))
                .retrieve()
                .toEntity(String.class);
        log("https://drive-pc.quark.cn/1/clouddrive/file/delete", HttpMethod.POST.name(), response);
        return response.getBody();
    }

    public Map<String, String> download(List<String> fids, MultiValueMap<String, String> cookies) {
        URI uri = getUriComponentsBuilder("https://drive.quark.cn/1/clouddrive/file/download")
                .build()
                .toUri();
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) quark-cloud-drive/2.5.20 Chrome/100.0.4896.160 Electron/18.3.5.4-b478491100 Safari/537.36 Channel/pckk_other_ch";
        ResponseEntity<String> response = restClient
                .post()
                .uri(uri)
                .header("User-Agent", userAgent)
                .cookies(c -> c.addAll(cookies))
                .body(JSONObject.of("fids", fids))
                .retrieve()
                .toEntity(String.class);
        log("https://drive.quark.cn/1/clouddrive/file/download", HttpMethod.POST.name(), response);
        String responseBody = response.getBody();
        Map<String, String> map = new HashMap<>();
        map.put("cookies", getSetCookie(response.getHeaders().get("set-cookie")));
        map.put("response", responseBody);
        return map;
    }

    private <T> void log(String url, String method, ResponseEntity<T> response) {
        log.info("================QuarkApi================");
        log.info("请求 URL : {}", url);
        log.info("请求方法 : {}", method);
        log.info("响应状态 : {}", response.getStatusCode().value());
        log.info("响应结果 : {}", response.getBody());
        log.info("========================================");
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
}
