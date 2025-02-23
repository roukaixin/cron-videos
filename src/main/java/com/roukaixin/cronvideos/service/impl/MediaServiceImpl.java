package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.mapper.CloudShareMapper;
import com.roukaixin.cronvideos.mapper.MediaMapper;
import com.roukaixin.cronvideos.pojo.CloudShare;
import com.roukaixin.cronvideos.pojo.Media;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.vo.CloudShareVO;
import com.roukaixin.cronvideos.service.MediaService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author pankx
* @description 针对表【media(影视列表)】的数据库操作Service实现
*/
@Service
public class MediaServiceImpl extends ServiceImpl<MediaMapper, Media>
    implements MediaService {

    private final CloudShareMapper cloudShareMapper;

    public MediaServiceImpl(CloudShareMapper cloudShareMapper) {
        this.cloudShareMapper = cloudShareMapper;
    }

    @Override
    public R<List<CloudShareVO>> shares(String id) {
        List<CloudShare> cloudShares = cloudShareMapper.selectList(Wrappers.<CloudShare>lambdaQuery().eq(CloudShare::getMediaId, id));
        List<CloudShareVO> vos = new ArrayList<>();
        cloudShares.forEach(e -> {
            CloudShareVO vo = new CloudShareVO();
            BeanUtils.copyProperties(e, vo);
            vos.add(vo);
        });
        return R.<List<CloudShareVO>>builder().code(200).data(vos).build();
    }
}




