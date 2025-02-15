package com.roukaixin.cronvideos.service;

import com.roukaixin.cronvideos.pojo.Aria2Server;
import com.baomidou.mybatisplus.extension.service.IService;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.Aria2ServerDTO;

/**
 * @author pankx
 * @description 针对表【aria2_connection(aria2 连接信息)】的数据库操作Service
 */
public interface Aria2ServerService extends IService<Aria2Server> {

    R<String> add(Aria2ServerDTO aria2ServerDto);

    R<String> delete(Long id);

    R<String> update(Long id, Aria2ServerDTO aria2ServerDto);
}
