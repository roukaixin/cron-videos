package com.roukaixin.cronvideos.service;

import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.dto.CloudShareDTO;
import com.roukaixin.cronvideos.domain.vo.CloudShareVO;

import java.util.List;

/**
 * @author pankx
 * @description 针对表【cloud(网盘分享链接)】的数据库操作Service
 */
public interface CloudService {

    /**
     * 获取分享列表
     *
     * @param mediaId 影视id
     * @return R<List < CloudSharesVO>>
     */
    R<List<CloudShareVO>> share(Long mediaId);

    void add(CloudShareDTO add);

    void update(Long id, CloudShareDTO update);
}
