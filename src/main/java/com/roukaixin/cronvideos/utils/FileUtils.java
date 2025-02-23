package com.roukaixin.cronvideos.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.io.FilenameUtils;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtils {

    private static String getSuffix(String mimeType, String fileName) {
        String suffix;
        try {
            suffix = MimeTypes.getDefaultMimeTypes().forName(mimeType).getExtension();
        } catch (MimeTypeException e) {
            log.error("根据 mime 类型获取失败 {} {}", mimeType, e.getMessage());
            suffix = FilenameUtils.getSuffixFromPath(fileName);
        }
        if (log.isInfoEnabled()) {
            log.info("获取后缀 -> 文件名 {} -> 文件类型 {} -> 结果 {}", fileName, mimeType, suffix);
        }
        return suffix.isEmpty() ? FilenameUtils.getSuffixFromPath(fileName) : suffix;
    }

    public static String getName(String title, Integer season, Integer totalEpisodes,
                                 String fileName, String episodeRegex, String mimeType) {
        String out;
        String suffix = getSuffix(mimeType, fileName);
        try {
            Integer numer = Integer.valueOf(episodeRegex);
            out = String.format(
                    "%s S%02dE%0" + totalEpisodes.toString().length() + "d%s",
                    title,
                    season,
                    numer,
                    suffix
            );
        } catch (NumberFormatException e) {
            log.error("影视名匹配出来不是数字 -> {}", episodeRegex);
            out = episodeRegex + suffix;
        }
        return out;
    }

    public static String episodeRegex(Pattern p, String fileName) {
        String group = "";
        Matcher matcher = p.matcher(fileName);
        if (matcher.find()) {
            group = matcher.group();
        }
        if (log.isInfoEnabled() && group.isEmpty()) {
            log.info("影视名字匹配不成功 -> {}", fileName);
        }
        return group;
    }

    public static Integer getEpisodeNumber(String episodeRegex) {
        int numer;
        try {
            numer = Integer.parseInt(episodeRegex);
        } catch (NumberFormatException e) {
            log.error("匹配结果不能转化成数字 {}", episodeRegex);
            numer = 1;
        }
        return numer;
    }

    public static void main(String[] args) {
        System.out.println(getSuffix("video/x-matroska", ""));
    }
}
