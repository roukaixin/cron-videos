package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.enums.MediaResolutionEnum;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import com.roukaixin.cronvideos.pojo.Page;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.Aria2DownloadTaskDTO;
import com.roukaixin.cronvideos.pojo.vo.Aria2DownloadTaskPageVO;
import com.roukaixin.cronvideos.service.Aria2DownloadTasksService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【aria2_download_tasks】的数据库操作Service实现
 */
@Service
public class Aria2DownloadTasksServiceImpl extends ServiceImpl<Aria2DownloadTasksMapper, Aria2DownloadTask>
        implements Aria2DownloadTasksService {

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    public Aria2DownloadTasksServiceImpl(Aria2DownloadTasksMapper aria2DownloadTasksMapper) {
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
    }


    @Override
    public R<Page<Aria2DownloadTaskPageVO>> list(Aria2DownloadTaskDTO dto) {
        List<Aria2DownloadTaskPageVO> list = aria2DownloadTasksMapper.list(dto);
        Integer count = aria2DownloadTasksMapper.listCount(dto);
        list.forEach(e -> e.setShortName(MediaResolutionEnum.shortName(e.getVideoWidth(), e.getVideoHeight())));
        return R.<Page<Aria2DownloadTaskPageVO>>builder()
                .data(Page.<Aria2DownloadTaskPageVO>builder().list(list).total(count).build()).code(200).build();
    }
}




