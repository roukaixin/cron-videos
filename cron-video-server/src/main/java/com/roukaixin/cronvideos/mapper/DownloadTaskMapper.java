package com.roukaixin.cronvideos.mapper;

import com.roukaixin.cronvideos.domain.DownloadTask;
import com.roukaixin.cronvideos.domain.dto.DownloadTaskDTO;
import com.roukaixin.cronvideos.domain.vo.DownloadTaskPageVO;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.ResultHandler;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【aria2_download_tasks】的数据库操作Mapper
 */
@Mapper
public interface DownloadTaskMapper {

    List<DownloadTaskPageVO> list(DownloadTaskDTO dto);

    Integer listCount(DownloadTaskDTO dto);

    @Select("select * from download_task where media_id = #{mediaId} and is_deleted = 0")
    void selectStreamByMediaId(@Param("mediaId") Long mediaId, ResultHandler<DownloadTask> resultHandler);

    @Select("select * from download_task where media_id = #{mediaId} and is_deleted = 0")
    List<DownloadTask> selectListByMediaId(@Param("mediaId") Long mediaId);

    @Delete("delete from download_task where id = #{id}")
    void deleteById(@Param("id") Long id);

    @Update("update download_task set status = #{status} where is_deleted = 0 and gid = #{gid} and status = #{originalStatus} and downloader_id = #{downloaderId}")
    void updateStatus(@Param("status") int status,
                      @Param("gid") String gid,
                      @Param("originalStatus") int originalStatus,
                      @Param("downloaderId") Long downloaderId);

    @Select("select * from download_task where media_id = #{mediaId} and episode_number = #{episodeNumber} and is_deleted = 0")
    DownloadTask selectOneByMediaIdAndEpisodeNumber(@Param("mediaId") Long mediaId, @Param("episodeNumber") int episodeNumber);

    @Select("")
    void insertOrUpdate(DownloadTask downloadTask);

    @Update("")
    void updateById(DownloadTask downloadTask);
}




