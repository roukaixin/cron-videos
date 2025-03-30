package com.roukaixin.cronvideos.mapper;

import com.roukaixin.cronvideos.pojo.Downloader;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author pankx
 * @description 针对表【aria2_server(aria2 连接信息)】的数据库操作Mapper
 */
@Mapper
public interface DownloaderMapper extends BaseMapper<Downloader> {

}




