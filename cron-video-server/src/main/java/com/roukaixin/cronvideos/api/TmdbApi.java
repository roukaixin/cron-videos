package com.roukaixin.cronvideos.api;

import com.roukaixin.cronvideos.api.domain.Episode;
import com.roukaixin.cronvideos.utils.JsonUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;

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
        JsonNode bodyJson = JsonUtils.readTree(body);
        if (Objects.nonNull(bodyJson)) {
            if (bodyJson.get("success").asBoolean()) {
                ArrayNode episodes = bodyJson.withArrayProperty("episodes");
                episodesList = JsonUtils.readValue(episodes, new TypeReference<>() {
                });
            }
        }
        return episodesList;
    }
}
