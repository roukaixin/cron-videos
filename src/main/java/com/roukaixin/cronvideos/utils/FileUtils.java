package com.roukaixin.cronvideos.utils;

import com.roukaixin.cronvideos.enums.MediaResolutionEnum;
import com.roukaixin.cronvideos.enums.MediaTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.io.FilenameUtils;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
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
        return suffix;
    }

    /**
     * 从正则表达式匹配出集数。如果类型是 movie 返回 1。tv 如果是匹配出 null 返回 -1 表示失败(需要过滤掉)
     *
     * @param type     媒体类型
     * @param p        正则表达式
     * @param fileName 文件名
     * @return 集数
     */
    public static int getEpisodeNumber(MediaTypeEnum type, Pattern p, String fileName) {
        int numer = -1;
        switch (type) {
            case tv -> {
                String group = getMatcherString(p, fileName);
                numer = getEpisodeNumber(group);
            }
            case movie -> numer = 1;
        }
        return numer;
    }

    private static String episodeRegex(Pattern p, String fileName) {
        return getMatcherString(p, fileName);
    }

    private static String getMatcherString(Pattern p, String fileName) {
        String group = "";
        Matcher matcher = p.matcher(fileName);
        if (matcher.find()) {
            if (matcher.group(1) != null) {
                group = matcher.group(1);
            } else {
                group = matcher.group(2);
            }
        }
        if (log.isInfoEnabled() && group.isEmpty()) {
            log.info("影视名字匹配不成功 -> 规则 -> {} 文件名 -> {}", p.pattern(), fileName);
        }
        return group;
    }

    private static Integer getEpisodeNumber(String episodeRegex) {
        int numer = -1;
        if (ObjectUtils.isEmpty(episodeRegex)) {
            return numer;
        }
        try {
            numer = Integer.parseInt(episodeRegex);
        } catch (NumberFormatException e) {
            // 表示失败
            log.error("匹配结果不能转化成数字 {}", episodeRegex);
        }
        return numer;
    }

    public static String getOutName(MediaTypeEnum type,
                                    String name,
                                    Integer season,
                                    Integer totalEpisode,
                                    String fileName,
                                    int episodeNumber,
                                    String mimeType) {
        String outName = "";
        // 获取后缀
        String suffix = getSuffix(mimeType, fileName);
        switch (type) {
            case tv -> outName = String.format(
                    "%s S%02dE%0" + totalEpisode.toString().length() + "d%s",
                    name,
                    season,
                    episodeNumber,
                    suffix
            );
            case movie -> outName = name + suffix;
        }
        return outName;
    }

    public static void main(String[] args) {

        // ^\d{2}\s4K\.(mp4|mkv)$
        // (?i)^\d+.4K\.(mp4|mkv)$
        // 正则表达式，忽略大小写，匹配数字后跟 4K 和扩展名 mp4 或 mkv
        // (?i)^\d+-4K\.(mp4|mkv)$

        // eg : filename -> 06 4K.mp4,ZCJHM 01 4K.mp4   regex -> (\d+)\s+4K\.mp4$  (\d+)\s*\S*$
        // eg : filename -> S01E34.mp4   regex -> ^S\d+E(\d+)
        // eg : filename -> Throne.of.Seal.2022.S01E012.2160p.WEB-DL.H265.DDP.2Audios.mp4,06 4K.mp4   regex -> (?:^|E)(\d+)
        // eg : filename -> Secrets.in.the.Lattice.2021.EP01.HD1080P.X264.AAC.Mandarin.CHS.Mp4er.mp4   regex -> EP(\d{2})
        // eg : filename -> S02E36.mp4、43 4K高码.mkv   regex -> ^(?:S\d{2}E(\d{2})|(\d+) +(?i:4k)[\u4e00-\u9fa5]+)\.(?:mp4|mkv)$

        List<String> list = Arrays.asList("The.Knockout.S01E01.2023.2160.mkv", "43 4K高码.mkv", "43 4k.mkv");

        Pattern pattern = Pattern.compile("(?:^|E)(\\d+)");
        for (String s : list) {
            System.out.println(episodeRegex(pattern, s));
            test_share(pattern, s);
        }

        System.out.println(MediaResolutionEnum.shortNameNumber(1920, 800));

    }

    private static void test_share(Pattern pattern, String name) {
        System.out.println(pattern.matcher(name).matches());
    }
}
