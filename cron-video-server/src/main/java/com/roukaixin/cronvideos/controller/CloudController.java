package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.domain.Cloud;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.CloudShareDTO;
import com.roukaixin.cronvideos.domain.vo.CloudShareVO;
import com.roukaixin.cronvideos.service.CloudService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cloud")
public class CloudController {

    private final CloudService cloudService;

    public CloudController(CloudService cloudService) {
        this.cloudService = cloudService;
    }

    @PostMapping("/add")
    private R<String> add(@RequestBody CloudShareDTO cloudSharesDto) {
        Cloud cloudShare = new Cloud();
        BeanUtils.copyProperties(cloudSharesDto, cloudShare);
        cloudService.save(cloudShare);
        return R.<String>builder().code(200).message("添加成功").build();
    }

    @GetMapping("/{mediaId}")
    public R<List<CloudShareVO>> share(@PathVariable("mediaId") String mediaId){
        return cloudService.share(mediaId);
    }

    @PutMapping("/{id}")
    public R<String> update(@PathVariable("id") Long id, @RequestBody CloudShareDTO update) {
        Cloud cloudShare = new Cloud();
        BeanUtils.copyProperties(update, cloudShare);
        cloudShare.setId(id);
        cloudShare.setMediaId(null);
        cloudService.updateById(cloudShare);
        return R.<String>builder().code(200).message("编辑成功").build();
    }
}
