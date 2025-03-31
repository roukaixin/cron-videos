package com.roukaixin.cronvideos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roukaixin.cronvideos.domain.Media;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author pankx
 * @description 针对表【media(影视列表)】的数据库操作Mapper
 */
@Mapper
public interface MediaMapper extends BaseMapper<Media> {

}




