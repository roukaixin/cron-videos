package com.roukaixin.cronvideos.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.roukaixin.cronvideos.mapper.DownloadTaskMapper;
import com.roukaixin.cronvideos.mapper.MediaMapper;
import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.Media;
import com.roukaixin.cronvideos.utils.SshUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.common.SftpException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@Slf4j
public class MediaEventListener {

    private final MediaMapper mediaMapper;

    private final DownloadTaskMapper downloadTaskMapper;

    public MediaEventListener(MediaMapper mediaMapper,
                              DownloadTaskMapper downloadTaskMapper) {
        this.mediaMapper = mediaMapper;
        this.downloadTaskMapper = downloadTaskMapper;
    }

    @EventListener
    @Async
    public void formatAndMoveMedia(DownloadTask downloadTask) {
        if (log.isDebugEnabled()) {
            log.info("监听到格式化和移动视频 -> {}", JSON.toJSONString(downloadTask));
        }
        if (!ObjectUtils.isEmpty(downloadTask.getOutName())) {
            log.info("开始转化和移动视频 -> {}", downloadTask.getOutName());
            String savePath = downloadTask.getSavePath();
            String outName = downloadTask.getOutName();
            String targetOutName = FilenameUtils.getBaseName(outName);
            ClientSession sourceSession = SshUtils.init("tnt", "127.0.0.1", 22, "230553");
            ClientSession targetSession = null;
            // getMapCommand
            String commandMaps = getFfmpegMapCommand(sourceSession, savePath, outName);
            String ffmpeg = getFfmpeg(savePath, outName, targetOutName, commandMaps);
            if (SshUtils.execFfmpeg(sourceSession, ffmpeg)) {
                // 执行成功
                downloadTask.setResourceStatus(1);
                downloadTaskMapper.updateById(downloadTask);
                // 删除源文件
                if (SshUtils.execFfmpeg(sourceSession, getRm(savePath, outName))) {
                    log.info("删除视频成功 -> {}", outName);
                }
                // 修改回原来的名字
                if (SshUtils.execFfmpeg(sourceSession, getMv(savePath, targetOutName))) {
                    // 修改成功
                    log.info("视频使用 ffmpeg 转化成功 -> {}", outName);
                }
                Media media = mediaMapper.selectById(downloadTask.getMediaId());
                if (!ObjectUtils.isEmpty(media.getTypeAlias())) {
                    try {
                        // 媒体服务器 ip 地址 session
                        targetSession = SshUtils.init("tnt", "127.0.0.1", 22, "230553");
                        SshUtils.move(
                                sourceSession,
                                targetSession,
                                "/home/tnt/job/IdeaProjects/docker-software/local/download",
                                "/home/tnt/Videos" + "/" + media.getTypeAlias(),
                                downloadTask.getSavePath(),
                                outName
                        );
                        downloadTask.setResourceStatus(2);
                        downloadTaskMapper.updateById(downloadTask);
                    } catch (IOException e) {
                        if (e instanceof SftpException sftpException && sftpException.getMessage().equals("No such file")) {
                            // 文件不存在
                            downloadTask.setResourceStatus(2);
                            downloadTaskMapper.updateById(downloadTask);
                        } else {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            try {
                sourceSession.close();
                if (targetSession != null) {
                    targetSession.close();
                }
            } catch (IOException e) {
                log.error("ssh session 关闭异常", e);
            }
            log.info("转化和移动结束视频 -> {}", downloadTask.getOutName());
        }
    }

    private String getFfmpegMapCommand(ClientSession tnt, String savePath, String outName) {
        StringBuilder exclude_maps = new StringBuilder();
        String ffprobe = SshUtils.execFfprobe(tnt, getFfprobe(savePath, outName));
        JSONObject ffprobeJson = JSONObject.parseObject(ffprobe);
        if (!ObjectUtils.isEmpty(ffprobeJson)) {
            JSONArray streams = ffprobeJson.getJSONArray("streams");
            for (int i = 0; i < streams.size(); i++) {
                JSONObject stream = streams.getJSONObject(i);
                JSONObject disposition = stream.getJSONObject("disposition");
                String codecName = stream.getString("codec_name");
                String codecType = stream.getString("codec_type");
                Integer index = stream.getInteger("index");
                if (!"unknown".equals(codecName)) {
                    int attachedPic = disposition.getIntValue("attached_pic");
                    if ("video".equals(codecType) && attachedPic != 1) {
                        exclude_maps.append("-map").append(" ").append("0:").append(index).append(" ");
                    }
                    if ("audio".equals(codecType)) {
                        exclude_maps.append("-map").append(" ").append("0:").append(index).append(" ");
                    }
                }
            }
        }
        return exclude_maps.toString();
    }

    private static String getInputFile(String savePath, String outName) {
        return "'/home/tnt/job/IdeaProjects/docker-software/local/download" + savePath + "/" + outName + "'";
    }

    private static String getTargetFile(String savePath, String targetOutName) {
        return "'/home/tnt/job/IdeaProjects/docker-software/local/download" + savePath + "/" + targetOutName + "_target.mkv'";
    }

    private static String getFilePath(String savePath, String targetOutName) {
        return "'/home/tnt/job/IdeaProjects/docker-software/local/download" + savePath + "/" + targetOutName + ".mkv'";
    }

    private static String getRm(String savePath, String outName) {
        String[] command = {
                "rm",
                getInputFile(savePath, outName)
        };
        return String.join(" ", command);
    }

    private static String getMv(String savePath, String targetOutName) {
        String[] command = {
                "mv",
                getTargetFile(savePath, targetOutName),
                getFilePath(savePath, targetOutName)
        };
        return String.join(" ", command);
    }

    private static String getFfmpeg(String savePath, String outName, String targetOutName, String commandMaps) {
        // ffmpeg -y -loglevel repeat+level+error -i intput.mp4 -c copy -map 0:v -map 0:a -map -0:v:m:attached_pic -metadata title='' output.mkv
        String[] command = {
                "ffmpeg",
                "-y",
                "-loglevel", "repeat+level+error",
                "-i", getInputFile(savePath, outName),
                "-c", "copy",
                StringUtils.hasText(commandMaps) ? commandMaps : "",
                "-metadata", "title=''",
                getTargetFile(savePath, targetOutName)
        };
        return String.join(" ", command);
    }

    public static String getFfprobe(String savePath, String outName) {
        // ffprobe -v error -of json -show_optional_fields always -show_streams -show_entries stream=index,codec_name,codec_type 画江湖之不良人\ S07E01.mp4
        String[] command = {
                "ffprobe",
                "-v", "error",
                "-of", "json",
                "-show_optional_fields", "always",
                "-show_streams",
                "-show_entries", "stream=index,codec_name,codec_type",
                getInputFile(savePath, outName)
        };
        return String.join(" ", command);
    }

}
