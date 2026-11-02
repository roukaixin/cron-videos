package com.roukaixin.cronvideos.domain.entity;

import com.roukaixin.cronvideos.domain.entity.base.BaseEntity;
import com.roukaixin.cronvideos.enums.PlatformTypeEnum;
import com.roukaixin.cronvideos.strategy.domain.FilePath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 下载文件信息
 *
 * @TableName download_file_info
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DownloadFileInfo extends BaseEntity {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 下载任务ID
     */
    private Long downloadTaskId;

    /**
     * 网盘分享链接ID
     */
    private Long storageShareLinkId;

    /**
     * 网盘平台类型
     */
    private PlatformTypeEnum platform;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 父文件夹ID
     */
    private String parentFileId;

    /**
     * 文件路径（分享连接中，文件所在的层级目录）
     */
    private FilePath filePath;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private Integer fileType;

    /**
     * 文件 mime 类型
     */
    private String mimeType;

}