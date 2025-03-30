package com.roukaixin.cronvideos.mapper;

import com.roukaixin.cronvideos.domain.MediaEpisode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author pankx
 * @description 针对表【media_episode( 媒体库剧集信息)】的数据库操作Mapper
 * @createDate 2025-03-30 22:44:13
 * @Entity com.roukaixin.cronvideos.pojo.MediaEpisode
 */
@Mapper
public interface MediaEpisodeMapper extends BaseMapper<MediaEpisode> {

}




