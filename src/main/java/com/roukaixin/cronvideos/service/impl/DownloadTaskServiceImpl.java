package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.enums.MediaResolutionEnum;
import com.roukaixin.cronvideos.mapper.DownloadTaskMapper;
import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.Page;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.DownloadTaskDTO;
import com.roukaixin.cronvideos.domain.vo.DownloadTaskPageVO;
import com.roukaixin.cronvideos.service.DownloadTaskService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【aria2_download_tasks】的数据库操作Service实现
 */
@Service
public class DownloadTaskServiceImpl extends ServiceImpl<DownloadTaskMapper, DownloadTask>
        implements DownloadTaskService {

    private final DownloadTaskMapper downloadTaskMapper;

    public DownloadTaskServiceImpl(DownloadTaskMapper downloadTaskMapper) {
        this.downloadTaskMapper = downloadTaskMapper;
    }


    @Override
    public R<Page<DownloadTaskPageVO>> list(DownloadTaskDTO dto) {
        List<DownloadTaskPageVO> list = downloadTaskMapper.list(dto);
        Integer count = downloadTaskMapper.listCount(dto);
        list.forEach(e -> e.setShortName(MediaResolutionEnum.shortName(e.getVideoWidth(), e.getVideoHeight())));
        return R.<Page<DownloadTaskPageVO>>builder()
                .data(Page.<DownloadTaskPageVO>builder().list(list).total(count).build()).code(200).build();
    }
}




