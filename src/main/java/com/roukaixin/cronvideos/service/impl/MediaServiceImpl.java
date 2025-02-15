package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.mapper.CloudSharesMapper;
import com.roukaixin.cronvideos.mapper.MediaMapper;
import com.roukaixin.cronvideos.pojo.CloudShares;
import com.roukaixin.cronvideos.pojo.Media;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.vo.CloudSharesVO;
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

    private final CloudSharesMapper cloudSharesMapper;

    public MediaServiceImpl(CloudSharesMapper cloudSharesMapper) {
        this.cloudSharesMapper = cloudSharesMapper;
    }

    @Override
    public R<List<CloudSharesVO>> shares(String id) {
        List<CloudShares> cloudShares = cloudSharesMapper.selectList(Wrappers.<CloudShares>lambdaQuery().eq(CloudShares::getMediaId, id));
        List<CloudSharesVO> vos = new ArrayList<>();
        cloudShares.forEach(e -> {
            CloudSharesVO vo = new CloudSharesVO();
            BeanUtils.copyProperties(e, vo);
            vos.add(vo);
        });
        return R.<List<CloudSharesVO>>builder().code(200).data(vos).build();
    }
}




