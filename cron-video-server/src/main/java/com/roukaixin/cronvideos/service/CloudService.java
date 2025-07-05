package com.roukaixin.cronvideos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roukaixin.cronvideos.domain.Cloud;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.vo.CloudShareVO;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【cloud(网盘分享链接)】的数据库操作Service
 */
public interface CloudService extends IService<Cloud> {

    /**
     * 获取分享列表
     *
     * @param mediaId 影视id
     * @return R<List < CloudSharesVO>>
     */
    R<List<CloudShareVO>> share(String mediaId);
}
