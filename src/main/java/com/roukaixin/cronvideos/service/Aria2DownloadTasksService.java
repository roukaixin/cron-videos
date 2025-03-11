package com.roukaixin.cronvideos.service;

import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.Aria2DownloadTaskDTO;

import java.util.Map;

/**
 * @author pankx
 * @description 针对表【aria2_download_tasks】的数据库操作Service
 */
public interface Aria2DownloadTasksService extends IService<Aria2DownloadTask> {

    R<Map<String, Object>> list(Aria2DownloadTaskDTO dto);
}
