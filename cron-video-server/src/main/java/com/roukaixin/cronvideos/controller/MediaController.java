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
        List<Media> mediaList = mediaService.list();
        List<MediaVO> vos = new ArrayList<>();
        mediaList.forEach(media -> {
            MediaVO vo = new MediaVO();
            BeanUtils.copyProperties(media, vo);
            vos.add(vo);
        });
        return R.<List<MediaVO>>builder().code(200).data(vos).build();
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody MediaDTO mediaDto) {
        Media media = new Media();
        BeanUtils.copyProperties(mediaDto, media);
        mediaService.save(media);
        return R.<String>builder().code(200).message("添加成功").build();
    }

    @PutMapping("/update/{id}")
    public R<String> update(@PathVariable Long id, @RequestBody MediaUpdateDTO dto){
        Media media = new Media();
        BeanUtils.copyProperties(dto, media);
        media.setId(id);
        mediaService.updateById(media);
        return R.<String>builder().code(200).message("更新成功").build();
    }
}
