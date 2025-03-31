package com.roukaixin.cronvideos.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.roukaixin.cronvideos.api.domain.Episode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Component
public class TmdbApi {

    private final RestClient restClient;

    public TmdbApi(RestClient restClient) {
        this.restClient = restClient;
    }

    // https://api.themoviedb.org/3/tv/229192/season/0?api_key=db55323b8d3e4154498498a75642b381&language=zh-CN

    public List<Episode> tvEpisodes(String seriesId, Integer seasonNumber) {
        List<Episode> episodesList = null;
        String body = restClient
                .get()
                .uri("https://api.themoviedb.org/3/tv/{seriesId}/season/{seasonNumber}?api_key={apiKey}&language={language}",
                        seriesId, seasonNumber, "db55323b8d3e4154498498a75642b381", "zh-CN")
                .retrieve()
                .body(String.class);
        JSONObject bodyJson = JSON.parseObject(body);
        if (Objects.nonNull(bodyJson)) {
            if (!Boolean.FALSE.equals(bodyJson.getBoolean("success"))) {
                JSONArray episodes = bodyJson.getJSONArray("episodes");
                episodesList = episodes.toList(Episode.class);
            }
        }
        return episodesList;
    }
}
