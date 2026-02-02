package com.roukaixin.cronvideos.mapper;

import com.roukaixin.cronvideos.domain.MediaEpisode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author pankx
 * @description 针对表【media_episode( 媒体库剧集信息)】的数据库操作Mapper
 * @date  2025-03-30 22:44:13
 * @Entity com.roukaixin.cronvideos.pojo.MediaEpisode
 */
@Mapper
public interface MediaEpisodeMapper {

    List<MediaEpisode> selectEpisodeNumberByMediaId(@Param("mediaId") Long mediaId,
                                                    @Param("airDate") LocalDateTime airDate);

    @Select("select * from media_episode where media_id = #{mediaId} and episode_number = #{episodeNumber}")
    MediaEpisode selectOne(@Param("mediaId") Long mediaId, @Param("episodeNumber") Integer episodeNumber);

    @Update("update media_episode set is_update = #{isUpdate} where id = #{id}")
    void updateIsUpdateById(@Param("isUpdate") int isUpdate,@Param("id") Long id);

    @Select("select * from media_episode where media_id = #{mediaId}")
    List<MediaEpisode> selectOneByMediaId(@Param("mediaId") Long mediaId);

    void updateBatch(@Param("update") List<MediaEpisode> update);

    void insertBatch(@Param("add") List<MediaEpisode> add);
}




