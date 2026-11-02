package com.roukaixin.cronvideos.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.roukaixin.cronvideos.domain.entity.base.BaseEntity;
import com.roukaixin.cronvideos.strategy.domain.FilePath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 下载文件信息
 *
 * @author roukaixin
 * @date 2026/11/2 18:53
 * @TableName download_file_info
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table("download_file_info")
public class DownloadFileInfo extends BaseEntity {

    /**
     * 主键ID
     */
    @Id
    @Column("id")
    private Long id;

    /**
     * 网盘分享链接ID
     */
    @Column("storage_share_link_id")
    private Long storageShareLinkId;

    /**
     * 文件ID
     */
    @Column("file_id")
    private String fileId;

    /**
     * 文件名
     */
    @Column("file_name")
    private String fileName;

    /**
     * 父文件夹ID
     */
    @Column("parent_file_id")
    private String parentFileId;

    /**
     * 文件路径（分享连接中，文件所在的层级目录）
     */
    @Column("file_path")
    private FilePath filePath;

    /**
     * 文件大小
     */
    @Column("file_size")
    private Long fileSize;

    /**
     * 文件类型
     */
    @Column("file_type")
    private Integer fileType;

    /**
     * 文件 mime 类型
     */
    @Column("mime_type")
    private String mimeType;

}