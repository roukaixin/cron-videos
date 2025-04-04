package com.roukaixin.cronvideos.listener.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadTaskStatus {

    private Long id;

    private int status;

    private String gid;

}
