package com.roukaixin.cronvideos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import com.roukaixin.cronvideos.pojo.Page;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.Aria2DownloadTaskDTO;
import com.roukaixin.cronvideos.pojo.vo.Aria2DownloadTaskPageVO;

/**
 * @author pankx
 * @description 针对表【aria2_download_tasks】的数据库操作Service
 */
public interface Aria2DownloadTasksService extends IService<Aria2DownloadTask> {

    R<Page<Aria2DownloadTaskPageVO>> list(Aria2DownloadTaskDTO dto);
}
