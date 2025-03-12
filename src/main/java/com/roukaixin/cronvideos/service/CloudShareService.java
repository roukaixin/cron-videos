package com.roukaixin.cronvideos.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.roukaixin.cronvideos.pojo.CloudShare;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.vo.CloudShareVO;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【cloud_shares(网盘分享链接)】的数据库操作Service
 */
public interface CloudShareService extends IService<CloudShare> {

    /**
     * 获取分享列表
     *
     * @param mediaId 影视id
     * @return R<List < CloudSharesVO>>
     */
    R<List<CloudShareVO>> share(String mediaId);
}
