package com.roukaixin.cronvideos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.Page;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.DownloadTaskDTO;
import com.roukaixin.cronvideos.domain.vo.DownloadTaskPageVO;

/**
 * @author pankx
 * @description 针对表【aria2_download_tasks】的数据库操作Service
 */
public interface DownloadTaskService extends IService<DownloadTask> {

    R<Page<DownloadTaskPageVO>> list(DownloadTaskDTO dto);
}
