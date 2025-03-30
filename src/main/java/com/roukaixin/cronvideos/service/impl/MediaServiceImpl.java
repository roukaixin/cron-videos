package com.roukaixin.cronvideos.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.roukaixin.cronvideos.mapper.MediaMapper;
import com.roukaixin.cronvideos.domain.Media;
import com.roukaixin.cronvideos.service.MediaService;
import org.springframework.stereotype.Service;

/**
* @author pankx
* @description 针对表【media(影视列表)】的数据库操作Service实现
*/
@Service
public class MediaServiceImpl extends ServiceImpl<MediaMapper, Media>
    implements MediaService {

}




