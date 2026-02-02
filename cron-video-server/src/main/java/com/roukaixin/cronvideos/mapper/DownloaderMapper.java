package com.roukaixin.cronvideos.mapper;

import com.roukaixin.cronvideos.domain.Downloader;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.ResultHandler;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【aria2_server(aria2 连接信息)】的数据库操作Mapper
 */
@Mapper
public interface DownloaderMapper {

    @Select("select * from downloader where is_deleted = 0")
    List<Downloader> selectAll();

    @Select("select * from downloader where is_deleted = 0")
    @Options(resultSetType = ResultSetType.FORWARD_ONLY, fetchSize = Integer.MIN_VALUE)
    @ResultType(Downloader.class)
    void selectAllStream(ResultHandler<Downloader> result);

    @Select("select * from downloader where id = #{id} and is_deleted = 0")
    Downloader selectOneById(@Param("id") Long id);

    @Insert("insert into downloader(id, type, protocol, host, port, secret, weight, create_date, update_date) value (#{downloader.id}, #{downloader.type}, #{downloader.protocol}, #{downloader.host}, #{downloader.port}, #{downloader.secret}, #{downloader.weight}, #{downloader.createDate}, #{downloader.updateDate})")
    void insert(@Param("downloader") Downloader downloader);

    @Update("update downloader set is_deleted = 1 where id = #{id} and is_deleted = 0")
    void deleteById(@Param("id") Long id);

    @Update("update downloader set type = #{downloader.type}, protocol = #{downloader.protocol}, host = #{downloader.host}, port = #{downloader.port}, secret = #{downloader.secret}, weight = #{downloader.weight}, update_date = #{downloader.updateDate} where id = #{downloader.id} and is_deleted = 0")
    void updateById(@Param("downloader") Downloader downloader);

    @Update("update downloader set is_online = #{isOnline} where id = #{id} and is_deleted = 0")
    void updateIsOnlineById(@Param("id") Long id, @Param("isOnline") int isOnline);
}




