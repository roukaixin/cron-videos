package com.roukaixin.cronvideos.mapper;

import com.roukaixin.cronvideos.domain.Media;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.ResultHandler;

import java.util.List;


/**
 * @author pankx
 * @description 针对表【media(影视列表)】的数据库操作Mapper
 */
@Mapper
public interface MediaMapper {

    @Select("select * from media where is_deleted = 0")
    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = Integer.MIN_VALUE)
    @ResultType(Media.class)
    void selectAllStream(ResultHandler<Media> resultHandler);

    @Select("select * from media where is_deleted = 0")
    List<Media> selectAll();

    @Update({"update media set type_alias = #{typeAlias}, total_episode = #{totalEpisode}, start_episode = #{startEpisode}, release_date = #{releaseDate}, create_date = #{createDate} where id = #{id} and is_deleted = 0"})
    void updateById(@Param("media") Media media);

    @Select("select * from media where id = #{id} and is_deleted = 0")
    Media selectOneById(@Param("id") Long id);
}




