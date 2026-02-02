package com.roukaixin.cronvideos.mapper;

import com.roukaixin.cronvideos.domain.CloudStorageAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author pankx
 * @description 针对表【cloud_storage_auth(网盘认证信息存储)】的数据库操作Mapper
 */
@Mapper
public interface CloudStorageAuthMapper {

    @Select("select * from cloud_storage_auth where is_deleted = 0 and provider = #{provider}")
    CloudStorageAuth selectOneByProvider(@Param("provider") int provider);
}




