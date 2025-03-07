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
            group = matcher.group(1);
        }
        if (log.isInfoEnabled() && group.isEmpty()) {
            log.info("影视名字匹配不成功 -> 规则 -> {} 文件名 -> {}",p.pattern(), fileName);
        }
        return group;
    }

    public static Integer getEpisodeNumber(String episodeRegex) {
        int numer;
        try {
            numer = Integer.parseInt(episodeRegex);
        } catch (NumberFormatException e) {
            log.error("匹配结果不能转化成数字 {}", episodeRegex);
            // 比如: 如果电影名字不是数字,相当于电影的第一集
            numer = 1;
        }
        return numer;
    }

    public static void main(String[] args) {
        Pattern p = Pattern.compile("^\\d+");
        String a = "03 4K.mp4";
        // ^\d{2}\s4K\.(mp4|mkv)$
        // (?i)^\d+.4K\.(mp4|mkv)$
        String input = "24-4k.mp4"; // 输入文件名

        // 正则表达式，忽略大小写，匹配数字后跟 4K 和扩展名 mp4 或 mkv
        // (?i)^\d+-4K\.(mp4|mkv)$
        Pattern pattern = Pattern.compile("(?i)^\\d+-4K\\.(mp4|mkv)$");

        // 创建匹配器
        Matcher matcher = pattern.matcher(input);

        // 使用 matches() 确保整个字符串匹配
        System.out.println(matcher.matches());  // 输出 true，如果完全匹配

        // 只在完全匹配时提取数字部分
        if (matcher.matches()) {
            String number = input.split("-")[0];  // 获取数字部分
            System.out.println(number);  // 输出提取的数字部分
        } else {
            System.out.println("文件名格式不匹配");
        }

    }
}
