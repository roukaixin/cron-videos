package com.roukaixin.cronvideos.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum MediaTypeEnum {

    TV(1),
    MOVIE(2);

    @JsonValue
    private final int id;

    private final static List<MediaTypeEnum> V;

    static {
        V = List.of(values());
    }
}
