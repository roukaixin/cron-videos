package com.roukaixin.cronvideos.service;

import com.roukaixin.cronvideos.domain.dto.CloudStorageAuthDTO;
import com.roukaixin.cronvideos.domain.vo.CloudStorageAuthVO;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【cloud_storage_auth(网盘认证信息存储)】的数据库操作Service
 */
public interface CloudStorageAuthService  {

    void update(Long id, CloudStorageAuthDTO update);

    void delete(Long id);

    void add(CloudStorageAuthDTO add);

    List<CloudStorageAuthVO> list();

}
