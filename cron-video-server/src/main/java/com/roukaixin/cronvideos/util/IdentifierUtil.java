package com.roukaixin.cronvideos.util;

import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import org.jspecify.annotations.NonNull;

public class IdentifierUtil {

    private final static SnowFlakeIDKeyGenerator ID_KEY_GENERATOR;

    static {
        ID_KEY_GENERATOR = new SnowFlakeIDKeyGenerator();
    }

    public static @NonNull Long nextId() {
        return ID_KEY_GENERATOR.nextId();
    }
}
