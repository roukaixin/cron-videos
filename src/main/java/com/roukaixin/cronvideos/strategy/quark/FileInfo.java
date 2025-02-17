package com.roukaixin.cronvideos.strategy.quark;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileInfo {

    private String fid;

    @JsonAlias("file_name")
    private String fileName;

    @JsonAlias("pdir_fid")
    private String pdirFid;

    private Integer category;

    @JsonAlias("share_fid_token")
    private String shareFidToken;
}
