package com.roukaixin.cronvideos.service;

import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.Page;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.DownloadTaskDTO;
import com.roukaixin.cronvideos.domain.vo.DownloadTaskPageVO;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【aria2_download_tasks】的数据库操作Service
 */
public interface DownloadTaskService  {

    R<Page<DownloadTaskPageVO>> list(DownloadTaskDTO dto);

    void deleteById(Long id);

    List<DownloadTask> catTask(Long mediaId);
}
