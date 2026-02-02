package com.roukaixin.cronvideos.utils;

import com.roukaixin.cronvideos.algorithm.SnowflakeIdWorker;
import org.jspecify.annotations.NonNull;

public class IdentifierUtils {

    private final static SnowflakeIdWorker snowflakeIdWorker;

    static {
        snowflakeIdWorker = new SnowflakeIdWorker(1, 1);
    }

    public static @NonNull Long nextId() {
        return snowflakeIdWorker.nextId();
    }
}
