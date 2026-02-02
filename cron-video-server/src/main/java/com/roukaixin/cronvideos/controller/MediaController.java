package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.domain.Media;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.MediaDTO;
import com.roukaixin.cronvideos.domain.dto.MediaUpdateDTO;
import com.roukaixin.cronvideos.domain.vo.MediaVO;
import com.roukaixin.cronvideos.service.MediaService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping("/list")
    public R<List<MediaVO>> list() {
        return R.<List<MediaVO>>builder().code(200).data(mediaService.list()).build();
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody MediaDTO mediaDto) {
        mediaService.add(mediaDto);
        return R.<String>builder().code(200).message("添加成功").build();
    }

    @PutMapping("/update/{id}")
    public R<String> update(@PathVariable Long id, @RequestBody MediaUpdateDTO dto){
        mediaService.updateById(id, dto);
        return R.<String>builder().code(200).message("更新成功").build();
    }
}
