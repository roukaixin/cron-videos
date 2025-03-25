package com.roukaixin.cronvideos.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.springframework.util.ObjectUtils;

import java.io.*;
import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SshUtils {

    private static final SshClient sshClient = SshClient.setUpDefaultClient();

    private static final Map<String, ClientSession> CLIENT_SESSION_MAP = new HashMap<>();

    static {
        sshClient.start();
        // 确保程序退出时关闭
        Runtime.getRuntime().addShutdownHook(new Thread(sshClient::stop));
    }

    @SneakyThrows
    public static ClientSession init(String username, String host, int port, String password) {
        String key = username + "@" + host + ":" + port;
        ClientSession clientSession = CLIENT_SESSION_MAP.get(key);
        if (!ObjectUtils.isEmpty(clientSession) && !clientSession.isClosed()) {
            return clientSession;
        }
        clientSession = sshClient.connect(username, host, port).verify().getClientSession();
        clientSession.addPasswordIdentity(password);
        clientSession.auth().verify();
        CLIENT_SESSION_MAP.put(key, clientSession);
        return clientSession;
    }

    @SneakyThrows
    public static boolean execFfmpeg(ClientSession session, String command) {
        ChannelExec channel = session.createExecChannel(command);
        ByteArrayOutputStream info = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        channel.setOut(info);
        channel.setErr(err);
        channel.open().verify();
        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), Duration.ofMinutes(10));

        if (ObjectUtils.isEmpty(channel.getExitStatus()) || channel.getExitStatus() != 0) {
            log.warn("ffmpeg command -> {}", command);
        }
        log.warn("ffmpeg exit status -> {}", channel.getExitStatus());
        log.info("ffmpeg info -> {}", info);
        log.error("ffmpeg err -> {}", err);
        return !ObjectUtils.isEmpty(channel.getExitStatus()) && channel.getExitStatus() == 0;
    }

    @SneakyThrows
    public static String execFfprobe(ClientSession session, String command) {
        ByteArrayOutputStream info = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        ChannelExec channel = session.createExecChannel(command);
        channel.setErr(err);
        channel.setOut(info);
        channel.open().verify();
        channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), Duration.ofMinutes(10));
        log.warn("ffprobe command -> {}", command);
        log.warn("ffprobe exit status -> {}", channel.getExitStatus());
        if (!ObjectUtils.isEmpty(channel.getExitStatus()) && channel.getExitStatus() == 0) {
            return info.toString();
        } else {
            log.info("ffprobe info -> {}", info);
            log.error("ffprobe err -> {}", err);
        }
        return "";
    }


    public static void move(ClientSession sourceSession,
                            ClientSession targetSession,
                            String sourcePath,
                            String targetPath,
                            String savePath,
                            String outName) throws IOException {
        SftpClientFactory factory = SftpClientFactory.instance();
        sourceSession.getFactoryManager().getProperties().put("idle-timeout", 60 * 1000 * 30);
        targetSession.getFactoryManager().getProperties().put("idle-timeout", 60 * 1000 * 30);
        try (SftpClient sftpSource = factory.createSftpClient(sourceSession);
             SftpClient sftpTarget = factory.createSftpClient(targetSession)) {
            String sourceFilePath = getFilePath(sourcePath, savePath, outName);
            String destinationDir = targetPath + savePath;
            String destinationFilePath = getFilePath(targetPath, savePath, outName);
            if (log.isInfoEnabled()) {
                log.info("正在将文件从 {} 传输到 {}", sourceFilePath, destinationFilePath);
            }
            createDirectories(sftpTarget, destinationDir);
            try (InputStream read = sftpSource.read(sourceFilePath);
                 OutputStream write = sftpTarget.write(destinationFilePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = read.read(buffer)) != -1) {
                    write.write(buffer, 0, bytesRead);
                }
                write.flush();
            }
            // 删除源文件
            sftpSource.remove(sourceFilePath);
        }

    }

    private static void createDirectories(SftpClient sftp, String path) throws IOException {
        String[] folders = path.split("/");
        StringBuilder currentPath = new StringBuilder();
        for (String folder : folders) {
            if (!folder.isEmpty()) {
                currentPath.append("/").append(folder);
                // 检查当前路径是否存在
                if (!pathExists(sftp, currentPath.toString())) {
                    // 创建目录
                    sftp.mkdir(currentPath.toString());
                    log.info("Created directory: {}", currentPath);
                }
            }
        }
    }

    private static boolean pathExists(SftpClient sftp, String path) {
        try {
            // 检查路径是否存在
            sftp.stat(path);
            return true;
        } catch (IOException e) {
            // 路径不存在
            log.error("检查路径失败", e);
            return false;
        }
    }

    private static String getFilePath(String path, String savePath, String outName) {
        return path + savePath + "/" + FilenameUtils.getBaseName(outName) + ".mkv";
    }
}
