package com.roukaixin.cronvideos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roukaixin.cronvideos.pojo.Media;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.vo.CloudSharesVO;

import java.util.List;


/**
* @author pankx
* @description 针对表【media(影视列表)】的数据库操作Service
*/
public interface MediaService extends IService<Media> {

    /**
     * 获取分享列表
     * @param id 影视id
     * @return R<List<CloudSharesVO>>
     */
    R<List<CloudSharesVO>> shares(String id);
}
