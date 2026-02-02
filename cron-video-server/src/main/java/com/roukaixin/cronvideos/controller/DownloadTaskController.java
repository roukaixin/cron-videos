package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.Page;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.DownloadTaskDTO;
import com.roukaixin.cronvideos.domain.vo.DownloadTaskPageVO;
import com.roukaixin.cronvideos.domain.vo.DownloadTaskVO;
import com.roukaixin.cronvideos.enums.MediaResolutionEnum;
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

    @GetMapping("/{mediaId}")
    public R<List<DownloadTaskVO>> catTask(@PathVariable Long mediaId) {
        List<DownloadTask> list = downloadTaskService.catTask(mediaId);
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
        downloadTaskService.deleteById(id);
        return R.<String>builder().code(200).message("删除成功").build();
    }
}
