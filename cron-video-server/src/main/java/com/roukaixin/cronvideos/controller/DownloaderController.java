package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.DownloaderDTO;
import com.roukaixin.cronvideos.domain.vo.DownloaderVO;
import com.roukaixin.cronvideos.service.DownloaderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/downloader")
public class DownloaderController {

    private final DownloaderService downloaderService;

    public DownloaderController(DownloaderService downloaderService) {
        this.downloaderService = downloaderService;
    }

    @GetMapping("/list")
    public R<List<DownloaderVO>> list() {
        return R.<List<DownloaderVO>>builder().code(200).data(downloaderService.list()).build();
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody DownloaderDTO downloaderDto) {
        downloaderService.add(downloaderDto);
        return R.<String>builder().code(200).build();
    }

    @PutMapping("/update/{id}")
    public R<String> update(@PathVariable Long id, @RequestBody DownloaderDTO downloaderDto) {
        downloaderService.update(id, downloaderDto);
        return R.<String>builder().code(200).build();
    }

    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable Long id) {
        downloaderService.delete(id);
        return R.<String>builder().code(200).build();
    }

}
