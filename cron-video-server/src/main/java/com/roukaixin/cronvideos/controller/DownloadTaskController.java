package com.roukaixin.cronvideos.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.enums.MediaResolutionEnum;
import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.Page;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.DownloadTaskDTO;
import com.roukaixin.cronvideos.domain.vo.DownloadTaskPageVO;
import com.roukaixin.cronvideos.domain.vo.DownloadTaskVO;
import com.roukaixin.cronvideos.service.DownloadTaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/download/task")
public class DownloadTaskController {

    private final DownloadTaskService downloadTaskService;


    public DownloadTaskController(DownloadTaskService downloadTaskService) {
        this.downloadTaskService = downloadTaskService;
    }

    @GetMapping("/{id}")
    public R<List<DownloadTaskVO>> catTask(@PathVariable String id) {
        List<DownloadTask> list = downloadTaskService.list(
                Wrappers.<DownloadTask>lambdaQuery().eq(DownloadTask::getMediaId, id));
        List<DownloadTaskVO> vos = new ArrayList<>();
        list.forEach(e -> {
            DownloadTaskVO vo = new DownloadTaskVO();
            BeanUtils.copyProperties(e, vo);
            vo.setShortName(MediaResolutionEnum.shortName(e.getVideoWidth(), e.getVideoHeight()));
            vos.add(vo);
        });
        return R.<List<DownloadTaskVO>>builder().data(vos.stream().sorted(Comparator.comparingInt(DownloadTaskVO::getEpisodeNumber)).toList()).code(200).build();
    }

    @GetMapping("/list")
    public R<Page<DownloadTaskPageVO>> list(DownloadTaskDTO dto) {
        return downloadTaskService.list(dto);
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        downloadTaskService.removeById(id);
        return R.<String>builder().code(200).message("删除成功").build();
    }
}
