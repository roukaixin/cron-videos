package com.roukaixin.cronvideos.service.impl;

import com.roukaixin.cronvideos.domain.dto.CloudStorageAuthDTO;
import com.roukaixin.cronvideos.domain.vo.CloudStorageAuthVO;
import com.roukaixin.cronvideos.service.CloudStorageAuthService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【cloud_storage_auth(网盘认证信息存储)】的数据库操作Service实现
 */
@Service
public class CloudStorageAuthServiceImpl implements CloudStorageAuthService {

    @Override
    public void update(Long id, CloudStorageAuthDTO update) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void add(CloudStorageAuthDTO add) {

    }

    @Override
    public List<CloudStorageAuthVO> list() {
        return List.of();
    }
}




