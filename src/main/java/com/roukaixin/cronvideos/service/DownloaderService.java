package com.roukaixin.cronvideos.service;

import com.roukaixin.cronvideos.pojo.Downloader;
import com.baomidou.mybatisplus.extension.service.IService;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.DownloaderDTO;

/**
 * @author pankx
 * @description 针对表【aria2_connection(aria2 连接信息)】的数据库操作Service
 */
public interface DownloaderService extends IService<Downloader> {

    R<String> add(DownloaderDTO downloaderDto);

    R<String> delete(Long id);

    R<String> update(Long id, DownloaderDTO downloaderDto);
}
