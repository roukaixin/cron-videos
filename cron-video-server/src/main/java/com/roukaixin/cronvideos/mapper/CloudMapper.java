package com.roukaixin.cronvideos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.cronvideos.domain.Cloud;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author pankx
 * @description 针对表【cloud(网盘分享链接)】的数据库操作Mapper
 */
@Mapper
public interface CloudMapper extends BaseMapper<Cloud> {

}




