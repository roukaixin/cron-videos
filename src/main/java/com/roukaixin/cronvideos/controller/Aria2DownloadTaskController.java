package com.roukaixin.cronvideos.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.roukaixin.cronvideos.enums.MediaResolutionEnum;
import com.roukaixin.cronvideos.pojo.Aria2DownloadTask;
import com.roukaixin.cronvideos.pojo.Page;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.Aria2DownloadTaskDTO;
import com.roukaixin.cronvideos.pojo.vo.Aria2DownloadTaskPageVO;
import com.roukaixin.cronvideos.pojo.vo.Aria2DownloadTaskVO;
import com.roukaixin.cronvideos.service.Aria2DownloadTasksService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/download/task")
public class Aria2DownloadTaskController {

    private final Aria2DownloadTasksService aria2DownloadTasksService;


    public Aria2DownloadTaskController(Aria2DownloadTasksService aria2DownloadTasksService) {
        this.aria2DownloadTasksService = aria2DownloadTasksService;
    }

    @GetMapping("/{id}")
    public R<List<Aria2DownloadTaskVO>> catTask(@PathVariable String id) {
        List<Aria2DownloadTask> list = aria2DownloadTasksService.list(
                Wrappers.<Aria2DownloadTask>lambdaQuery().eq(Aria2DownloadTask::getMediaId, id));
        List<Aria2DownloadTaskVO> vos = new ArrayList<>();
        list.forEach(e -> {
            Aria2DownloadTaskVO vo = new Aria2DownloadTaskVO();
            BeanUtils.copyProperties(e, vo);
            vo.setShortName(MediaResolutionEnum.shortName(e.getVideoWidth(), e.getVideoHeight()));
            vos.add(vo);
        });
        return R.<List<Aria2DownloadTaskVO>>builder().data(vos.stream().sorted(Comparator.comparingInt(Aria2DownloadTaskVO::getEpisodeNumber)).toList()).code(200).build();
    }

    @GetMapping("/list")
    public R<Page<Aria2DownloadTaskPageVO>> list(Aria2DownloadTaskDTO dto) {
        return aria2DownloadTasksService.list(dto);
    }

    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id) {
        aria2DownloadTasksService.removeById(id);
        return R.<String>builder().code(200).message("删除成功").build();
    }
}
