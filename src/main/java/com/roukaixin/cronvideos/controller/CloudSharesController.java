package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.pojo.CloudShares;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.CloudSharesDTO;
import com.roukaixin.cronvideos.service.CloudSharesService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cloud-shares")
public class CloudSharesController {

    private final CloudSharesService cloudSharesService;

    public CloudSharesController(CloudSharesService cloudSharesService) {
        this.cloudSharesService = cloudSharesService;
    }

    @PostMapping("/add")
    private R<String> add(@RequestBody CloudSharesDTO cloudSharesDto) {
        CloudShares cloudShares = new CloudShares();
        BeanUtils.copyProperties(cloudSharesDto, cloudShares);
        cloudSharesService.save(cloudShares);
        return R.<String>builder().code(200).message("添加成功").build();
    }
}
