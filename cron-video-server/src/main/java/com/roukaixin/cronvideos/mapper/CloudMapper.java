package com.roukaixin.cronvideos.mapper;

import com.roukaixin.cronvideos.domain.Cloud;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author roukaixin
 * @date
 * @description 针对表【cloud(网盘分享链接)】的数据库操作Mapper
 */
@Mapper
public interface CloudMapper {

    void updateById(Cloud cloud);

    List<Cloud> selectListByMediaId(@Param("mediaId") Long mediaId);
}




