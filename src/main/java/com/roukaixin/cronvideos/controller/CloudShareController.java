package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.pojo.CloudShare;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.CloudShareDTO;
import com.roukaixin.cronvideos.service.CloudShareService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cloud-shares")
public class CloudShareController {

    private final CloudShareService cloudShareService;

    public CloudShareController(CloudShareService cloudShareService) {
        this.cloudShareService = cloudShareService;
    }

    @PostMapping("/add")
    private R<String> add(@RequestBody CloudShareDTO cloudSharesDto) {
        CloudShare cloudShare = new CloudShare();
        BeanUtils.copyProperties(cloudSharesDto, cloudShare);
        cloudShareService.save(cloudShare);
        return R.<String>builder().code(200).message("添加成功").build();
    }
}
