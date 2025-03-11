package com.roukaixin.cronvideos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import com.roukaixin.cronvideos.pojo.dto.Aria2DownloadTaskDTO;
import com.roukaixin.cronvideos.pojo.vo.Aria2DownloadTaskPageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【aria2_download_tasks】的数据库操作Mapper
 */
@Mapper
public interface Aria2DownloadTasksMapper extends BaseMapper<Aria2DownloadTask> {

    List<Aria2DownloadTaskPageVO> list(Aria2DownloadTaskDTO dto);

    Integer listCount(Aria2DownloadTaskDTO dto);
}




