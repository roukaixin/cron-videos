package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.roukaixin.cronvideos.domain.Cloud;
import com.roukaixin.cronvideos.domain.R;
import com.roukaixin.cronvideos.domain.vo.CloudShareVO;
import com.roukaixin.cronvideos.mapper.CloudMapper;
import com.roukaixin.cronvideos.service.CloudService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pankx
 * @description 针对表【cloud(网盘分享链接)】的数据库操作Service实现
 */
@Service
public class CloudServiceImpl extends CrudRepository<CloudMapper, Cloud>
        implements CloudService {

    @Override
    public R<List<CloudShareVO>> share(String mediaId) {
        List<Cloud> cloudShares = list(Wrappers.<Cloud>lambdaQuery().eq(Cloud::getMediaId, mediaId));
        List<CloudShareVO> vos = new ArrayList<>();
        cloudShares.forEach(e -> {
            CloudShareVO vo = new CloudShareVO();
            BeanUtils.copyProperties(e, vo);
            vos.add(vo);
        });
        return R.<List<CloudShareVO>>builder().code(200).data(vos).build();
    }
}




