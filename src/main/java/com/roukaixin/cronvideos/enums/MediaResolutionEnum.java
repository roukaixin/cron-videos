package com.roukaixin.cronvideos.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 媒体分辨率
 */
@Getter
@AllArgsConstructor
public enum MediaResolutionEnum {

    SD_480("SD(标清)", 720, 480, "480p", (double) 4 / 3, 480),
    SD_576("SD(标清)", 720, 576, "576p", (double) 16 / 9, 576),
    HD("HD(高清)", 1280, 720, "750p", (double) 16 / 9, 720),
    FULL_HD("Full HD(标准高清)", 1920, 1080, "1080p", (double) 16 / 9, 1080),
    QHD("QHD/2K", 2560, 1440, "2K", (double) 16 / 9, 1440),
    UHD_4K("4K UHD", 3840, 2160, "4K", (double) 16 / 9, 2160),
    UHD_8K("8K UHD", 7680, 4320, "8K", (double) 16 / 9, 4320),


    DCI_2K("2K DCI", 2048, 1080, "2K", (double) 17 / 9, 1440),
    DCI_4K("4K DCI", 4096, 2160, "4K", (double) 17 / 9, 2160),
    DCI_8K("8K DCI", 7680, 4320, "8K", (double) 17 / 9, 4320),

    ULTRA_WIDE_FULL_HD("标准高清", 1920, 800, "1080p", (double) 24 / 10, 1080),
    ULTRA_WIDE_QHD("2K", 2560, 1067, "2K", (double) 24 / 10, 1440),
    ULTRA_WIDE_UHD_4K("4K", 3840, 1600, "4K", (double) 24 / 10, 2160),


    OTHER("OTHER", 0, 0, "其他", 0, 0),


    ;

    // 名称
    private final String name;

    // 宽度
    private final int width;

    // 高度
    private final int height;

    // 简称
    private final String shortName;

    // 长宽比
    private final double aspectRatio;

    private final int shortNameNumber;

    public static int shortNameNumber(int width, int height) {
        double aspectRatio = (double) width / height;
        List<MediaResolutionEnum> list = new ArrayList<>();
        for (MediaResolutionEnum value : values()) {
            if (Math.abs(value.getAspectRatio() - aspectRatio) <= 0.05) {
                list.add(value);
            }
        }

        for (MediaResolutionEnum anEnum : list) {
            if (anEnum.getWidth() == width || anEnum.getHeight() == height) {
                return anEnum.getShortNameNumber();
            }
        }

        MediaResolutionEnum shortNumber = list.stream()
                .min(Comparator.comparingInt(r -> Math.abs(r.height - height)))
                .orElse(OTHER);
        if (Math.abs(shortNumber.height - height) <= 30) {
            return shortNumber.getShortNameNumber();
        }

        // 3. 全局查找最接近的分辨率（宽高均接近）
        shortNumber = Arrays.stream(values()).toList().stream()
                .min(Comparator.comparingDouble(r ->
                        Math.abs(r.width - width) + Math.abs(r.height - height)))
                .orElse(OTHER);
        return shortNumber.getShortNameNumber();
    }
}
