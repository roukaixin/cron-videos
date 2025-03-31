package com.roukaixin.cronvideos.strategy.domain;

import com.roukaixin.cronvideos.enums.CloudShareProviderEnum;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {

    /**
     * 网盘类型
     */
    private CloudShareProviderEnum provider;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 父文件夹id（保存到网盘的父目录。夸克 to_pdir_fid、阿里云 to_parent_file_id）
     */
    private String parentFileId;

    /**
     * 分享 id
     */
    private String shareId;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 媒体元数据
     */
    private MediaMetadata mediaMetadata;

    /**
     * 分享 token。夸克网盘 stoken
     */
    private String shareToken;

    /**
     * 驱动 id。阿里云盘 to_drive_id
     */
    private String driverId;

    /**
     * 集数
     */
    private int episodeNumber;

}
