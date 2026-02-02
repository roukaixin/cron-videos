package com.roukaixin.cronvideos.service;

import com.roukaixin.cronvideos.domain.dto.MediaDTO;
import com.roukaixin.cronvideos.domain.dto.MediaUpdateDTO;
import com.roukaixin.cronvideos.domain.vo.MediaVO;

import java.util.List;

/**
* @author pankx
* @description 针对表【media(影视列表)】的数据库操作Service
*/
public interface MediaService  {

    List<MediaVO> list();

    void updateById(Long id, MediaUpdateDTO update);

    void add(MediaDTO add);
}
