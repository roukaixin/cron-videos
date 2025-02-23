package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.mapper.CloudShareMapper;
import com.roukaixin.cronvideos.pojo.CloudShare;
import com.roukaixin.cronvideos.service.CloudShareService;
import org.springframework.stereotype.Service;

/**
 * @author pankx
 * @description 针对表【cloud_shares(网盘分享链接)】的数据库操作Service实现
 */
@Service
public class CloudShareServiceImpl extends ServiceImpl<CloudShareMapper, CloudShare>
        implements CloudShareService {

}




