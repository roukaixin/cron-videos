package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.domain.CloudStorageAuth;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.CloudStorageAuthDTO;
import com.roukaixin.cronvideos.domain.vo.CloudStorageAuthVO;
import com.roukaixin.cronvideos.service.CloudStorageAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cloud-storage-auth")
public class CloudStorageAuthController {

    private final CloudStorageAuthService cloudStorageAuthService;

    public CloudStorageAuthController(CloudStorageAuthService cloudStorageAuthService) {
        this.cloudStorageAuthService = cloudStorageAuthService;
    }

    @GetMapping("/list")
    public R<List<CloudStorageAuthVO>> list() {
        return R.<List<CloudStorageAuthVO>>builder().code(200).data(cloudStorageAuthService.list()).build();
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody CloudStorageAuthDTO cloudStorageAuthDto) {
        cloudStorageAuthService.add(cloudStorageAuthDto);
        return R.<String>builder().code(200).message("添加成功").build();
    }

    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable Long id) {
        cloudStorageAuthService.delete(id);
        return R.<String>builder().message("删除成功").code(200).build();
    }

    @PutMapping("/update/{id}")
    public R<String> update(@PathVariable Long id, @RequestBody CloudStorageAuthDTO update) {
        cloudStorageAuthService.update(id, update);
        return R.<String>builder().code(200).message("修改成功").build();
    }
}
