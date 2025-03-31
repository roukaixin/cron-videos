package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.domain.CloudStorageAuth;
import com.roukaixin.cronvideos.service.CloudStorageAuthService;
import com.roukaixin.cronvideos.mapper.CloudStorageAuthMapper;
import org.springframework.stereotype.Service;

/**
 * @author pankx
 * @description 针对表【cloud_storage_auth(网盘认证信息存储)】的数据库操作Service实现
 */
@Service
public class CloudStorageAuthServiceImpl extends ServiceImpl<CloudStorageAuthMapper, CloudStorageAuth>
        implements CloudStorageAuthService {

}




