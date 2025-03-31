package com.roukaixin.cronvideos.mapper;

import com.roukaixin.cronvideos.domain.CloudStorageAuth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author pankx
 * @description 针对表【cloud_storage_auth(网盘认证信息存储)】的数据库操作Mapper
 */
@Mapper
public interface CloudStorageAuthMapper extends BaseMapper<CloudStorageAuth> {

}




