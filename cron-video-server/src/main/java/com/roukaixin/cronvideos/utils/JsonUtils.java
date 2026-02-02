package com.roukaixin.cronvideos.utils;


import org.springframework.util.CollectionUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

public class JsonUtils {

    private JsonUtils() {
    }

    private final static JsonMapper JSON_MAPPER = new JsonMapper();

    public static String toJSONString(Object object) {
        try {
            return JSON_MAPPER.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayNode createArrayNode() {
        return JSON_MAPPER.createArrayNode();
    }

    public static ObjectNode createObjectNode() {
        return JSON_MAPPER.createObjectNode();
    }

    public static JsonNode readTree(String json) {
        try {
            return JSON_MAPPER.readTree(json);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(String json, Class<T> clazz) {
        try {
            return JSON_MAPPER.readValue(json, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> readValue(String json, TypeReference<List<T>> clazz) {
        try {
            return JSON_MAPPER.readValue(json, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> readValue(JsonNode tree, TypeReference<List<T>> clazz) {
        try {
            return JSON_MAPPER.treeToValue(tree, clazz);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> ArrayNode convertArrayNode(List<T> source) {
        ArrayNode target = createArrayNode();
        if (!CollectionUtils.isEmpty(source)) {
            source.forEach(target::addPOJO);
        }
        return target;
    }
}
