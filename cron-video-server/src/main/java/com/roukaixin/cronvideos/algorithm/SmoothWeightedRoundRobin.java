package com.roukaixin.cronvideos.algorithm;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class SmoothWeightedRoundRobin {

    private final Map<Long, Weight> weightMap = new LinkedHashMap<>();

    private SmoothWeightedRoundRobin() {

    }

    public static SmoothWeightedRoundRobin getInstance() {
        return InnerEnum.INSTANCE.getInstance();
    }

    @PostConstruct
    public void init() {

    }

    public Long getDownloaderId() {
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


    @Setter
    @Getter
    @AllArgsConstructor
    public static class Weight {

        private Long id;

        private Integer weight;

        private Integer currentWeight;
    }

    private enum InnerEnum {
        INSTANCE;

        private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;

        InnerEnum() {
            smoothWeightedRoundRobin = new SmoothWeightedRoundRobin();
        }

        public SmoothWeightedRoundRobin getInstance() {
            return smoothWeightedRoundRobin;
        }
    }

}
