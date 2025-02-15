package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.pojo.CloudStorageAuth;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.CloudStorageAuthDTO;
import com.roukaixin.cronvideos.pojo.vo.CloudStorageAuthVO;
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
        List<CloudStorageAuth> list = cloudStorageAuthService.list();
        List<CloudStorageAuthVO> vos = new ArrayList<>();
        list.forEach(e -> {
            CloudStorageAuthVO vo = new CloudStorageAuthVO();
            BeanUtils.copyProperties(e, vo);
            vos.add(vo);
        });
        return R.<List<CloudStorageAuthVO>>builder().code(200).data(vos).build();
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody CloudStorageAuthDTO cloudStorageAuthDto) {
        CloudStorageAuth cloudStorageAuth = new CloudStorageAuth();
        BeanUtils.copyProperties(cloudStorageAuthDto, cloudStorageAuth);
        cloudStorageAuthService.save(cloudStorageAuth);
        return R.<String>builder().code(200).message("添加成功").build();
    }

    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable Long id) {
        cloudStorageAuthService.removeById(id);
        return R.<String>builder().message("删除成功").code(200).build();
    }

    @PutMapping("/update/{id}")
    public R<String> update(@PathVariable Long id, @RequestBody CloudStorageAuthDTO cloudStorageAuthDto) {
        CloudStorageAuth cloudStorageAuth = new CloudStorageAuth();
        BeanUtils.copyProperties(cloudStorageAuthDto, cloudStorageAuth);
        cloudStorageAuth.setId(id);
        cloudStorageAuthService.updateById(cloudStorageAuth);
        return R.<String>builder().code(200).message("修改成功").build();
    }
}
