package com.roukaixin.cronvideos.service;

import com.roukaixin.cronvideos.domain.dto.DownloaderDTO;
import com.roukaixin.cronvideos.domain.vo.DownloaderVO;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【aria2_connection(aria2 连接信息)】的数据库操作Service
 */
public interface DownloaderService {

    List<DownloaderVO> list();

    void add(DownloaderDTO add);

    void update(Long id, DownloaderDTO update);

    void delete(Long id);

}
