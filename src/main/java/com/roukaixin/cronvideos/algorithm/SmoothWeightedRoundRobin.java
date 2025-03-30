package com.roukaixin.cronvideos.algorithm;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import com.roukaixin.cronvideos.pojo.Downloader;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class SmoothWeightedRoundRobin {


    private final Map<Long, Weight> weightMap = new HashMap<>();

    private final DownloaderMapper downloaderMapper;

    public SmoothWeightedRoundRobin(DownloaderMapper downloaderMapper) {
        this.downloaderMapper = downloaderMapper;
    }

    @PostConstruct
    public void init() {
        List<Downloader> downloaders = downloaderMapper.selectList(Wrappers.<Downloader>lambdaQuery().eq(Downloader::getIsOnline, 1));
        if (!downloaders.isEmpty()) {
            downloaders.forEach(aria2Connection -> weightMap.put(aria2Connection.getId(), new Weight(aria2Connection.getId(), aria2Connection.getWeight(), 0)));
        }

    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Weight {

        private Long id;

        private Integer weight;

        private Integer currentWeight;
    }

    public Long getAria2ServerId() {
        Long id = null;
        AtomicReference<Integer> totalWeight = new AtomicReference<>(0);
        weightMap.forEach((key, value) -> {
            value.setCurrentWeight(value.getCurrentWeight() + value.getWeight());
            totalWeight.updateAndGet(v -> v + value.getWeight());
        });
        Weight maxWeight = null;
        for (Map.Entry<Long, Weight> entry : weightMap.entrySet()) {
            if (maxWeight == null || entry.getValue().getCurrentWeight() > maxWeight.getCurrentWeight()) {
                maxWeight = entry.getValue();
            }
        }
        if (maxWeight != null) {
            maxWeight.setCurrentWeight(maxWeight.getCurrentWeight() - totalWeight.get());
            id = maxWeight.getId();
        }
        return id;
    }

    public void put(Long key, Integer weight) {
        weightMap.put(key, new Weight(key, weight, 0));
        weightMap.forEach((k,v) -> v.setCurrentWeight(0));
    }

    public void remove(Long key) {
        weightMap.remove(key);
        weightMap.forEach((k, v) -> v.setCurrentWeight(0));
    }

    public int size() {
        return weightMap.size();
    }

}
