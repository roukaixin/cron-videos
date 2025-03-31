package com.roukaixin.cronvideos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.cronvideos.domain.CloudShare;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author pankx
 * @description 针对表【cloud_shares(网盘分享链接)】的数据库操作Mapper
 */
@Mapper
public interface CloudShareMapper extends BaseMapper<CloudShare> {

}




