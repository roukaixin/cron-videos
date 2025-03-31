package com.roukaixin.cronvideos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.dto.DownloadTaskDTO;
import com.roukaixin.cronvideos.domain.vo.DownloadTaskPageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【aria2_download_tasks】的数据库操作Mapper
 */
@Mapper
public interface DownloadTaskMapper extends BaseMapper<DownloadTask> {

    List<DownloadTaskPageVO> list(DownloadTaskDTO dto);

    Integer listCount(DownloadTaskDTO dto);
}




