package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.pojo.Downloader;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.DownloaderDTO;
import com.roukaixin.cronvideos.pojo.vo.DownloaderVO;
import com.roukaixin.cronvideos.service.DownloaderService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/aria2")
public class DownloaderController {

    private final DownloaderService downloaderService;

    public DownloaderController(DownloaderService downloaderService) {
        this.downloaderService = downloaderService;
    }

    @GetMapping("/list")
    public R<List<DownloaderVO>> list() {
        List<Downloader> list = downloaderService.list();
        List<DownloaderVO> vos = new ArrayList<>();
        list.forEach(aria2 -> {
            DownloaderVO vo = new DownloaderVO();
            BeanUtils.copyProperties(aria2, vo);
            vos.add(vo);
        });
        return R.<List<DownloaderVO>>builder().code(200).data(vos).build();
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody DownloaderDTO downloaderDto) {
        return downloaderService.add(downloaderDto);
    }

    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable("id") Long id) {
        return downloaderService.delete(id);
    }

    @PutMapping("/update/{id}")
    public R<String> update(@PathVariable("id") Long id, @RequestBody DownloaderDTO downloaderDto) {
        return downloaderService.update(id, downloaderDto);
    }

}
