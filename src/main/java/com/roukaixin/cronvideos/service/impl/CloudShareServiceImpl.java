package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.mapper.CloudShareMapper;
import com.roukaixin.cronvideos.pojo.CloudShare;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.vo.CloudShareVO;
import com.roukaixin.cronvideos.service.CloudShareService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankx
 * @description 针对表【cloud_shares(网盘分享链接)】的数据库操作Service实现
 */
@Service
public class CloudShareServiceImpl extends ServiceImpl<CloudShareMapper, CloudShare>
        implements CloudShareService {

    @Override
    public R<List<CloudShareVO>> share(String mediaId) {
        List<CloudShare> cloudShares = list(Wrappers.<CloudShare>lambdaQuery().eq(CloudShare::getMediaId, mediaId));
        List<CloudShareVO> vos = new ArrayList<>();
        cloudShares.forEach(e -> {
            CloudShareVO vo = new CloudShareVO();
            BeanUtils.copyProperties(e, vo);
            vos.add(vo);
        });
        return R.<List<CloudShareVO>>builder().code(200).data(vos).build();
    }
}




