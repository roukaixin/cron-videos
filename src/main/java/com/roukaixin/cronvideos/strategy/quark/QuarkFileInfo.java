package com.roukaixin.cronvideos.strategy.quark;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuarkFileInfo {

    /**
     * fid 标识文件唯一 id
     */
    private String fid;

    /**
     * 文件名
     */
    @JsonAlias("file_name")
    private String fileName;

    /**
     * 父目录 fid
     */
    @JsonAlias("pdir_fid")
    private String pdirFid;

    /**
     * 类别。0、文件夹，1、文件
     */
    private Integer category;

    /**
     * 视频宽度
     */
    @JsonAlias("video_width")
    private Integer videoWidth;

    /**
     * 视频高度
     */
    @JsonAlias("video_height")
    private Integer videoHeight;

    /**
     * 文件类别。video(视频)
     */
    @JsonAlias("obj_category")
    private String objCategory;

    /**
     * 文件大小
     */
    private Long size;
}
