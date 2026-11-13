package com.roukaixin.cronvideos.util.algorithm;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class SmoothWeightedRoundRobin {


    private volatile AtomicReference<Integer> total;

    /**
     * 每个权重
     */
    private final List<Weight> weights = new CopyOnWriteArrayList<>();

    private SmoothWeightedRoundRobin() {

    }

    public static SmoothWeightedRoundRobin getInstance() {
        return InnerEnum.INSTANCE.getInstance();
    }

    public Long next() {
        // 1、当前权重 + 自身权重
        // 2、选中的 减去总权重
        Long id = null;
        total = new AtomicReference<>(0);
        weights.forEach(e -> {
            total.updateAndGet(v -> v + e.getWeight());
            e.setCurrentWeight(e.getWeight() + e.getCurrentWeight());
        });
        Weight maxWeight = null;
        for (Weight weight : weights) {
            if (maxWeight == null || weight.getCurrentWeight() > maxWeight.getCurrentWeight()) {
                maxWeight = weight;
            }
        }
        if (maxWeight != null) {
            maxWeight.setCurrentWeight(maxWeight.getCurrentWeight() - total.get());
            if (maxWeight.remove) {
                if (maxWeight.getCurrentWeight() == 0) {
                    weights.remove(maxWeight);
                }
                id = next();
            } else {
                id = maxWeight.getId();
            }
        }
        return id;
    }

    public void add(Long key, Integer weight) {
        weights.add(Weight.builder().id(key).weight(weight).build());
    }

    public void remove(Long key) {
        weights.forEach(e -> {
            if (key.equals(e.getId())) {
                if (e.getCurrentWeight() == 0) {
                    weights.remove(e);
                } else {
                    e.setRemove(true);
                }
            }
        });
    }

    public int size() {
        return weights.size();
    }


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Weight {

        private Long id;

        private Integer weight;

        private int currentWeight;

        @Builder.Default
        private boolean remove = false;
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
