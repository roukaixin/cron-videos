package com.roukaixin.cronvideos.strategy.domain;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaMetadata {

    /**
     * 宽度
     */
    private int width;

    /**
     * 高度
     */
    private int height;
}
