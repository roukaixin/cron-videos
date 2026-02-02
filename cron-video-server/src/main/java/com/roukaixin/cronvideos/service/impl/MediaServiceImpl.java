package com.roukaixin.cronvideos.service.impl;

import com.roukaixin.cronvideos.domain.Media;
import com.roukaixin.cronvideos.domain.dto.MediaDTO;
import com.roukaixin.cronvideos.domain.dto.MediaUpdateDTO;
import com.roukaixin.cronvideos.domain.vo.MediaVO;
import com.roukaixin.cronvideos.mapper.MediaMapper;
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
public class MediaServiceImpl implements MediaService {

    private final MediaMapper mediaMapper;

    public MediaServiceImpl(MediaMapper mediaMapper) {
        this.mediaMapper = mediaMapper;
    }

    @Override
    public List<MediaVO> list() {
        List<Media> mediaList = mediaMapper.selectAll();
        List<MediaVO> vos = new ArrayList<>();
        mediaList.forEach(media -> {
            MediaVO vo = new MediaVO();
            BeanUtils.copyProperties(media, vo);
            vos.add(vo);
        });
        return vos;
    }

    @Override
    public void updateById(Long id, MediaUpdateDTO update) {
        Media media = new Media();
        media.setId(id);
        media.setTypeAlias(update.getTypeAlias());
        media.setTotalEpisode(update.getTotalEpisode());
        media.setStartEpisode(update.getStartEpisode());
        media.setReleaseDate(update.getReleaseDate());
        mediaMapper.updateById(media);
    }

    @Override
    public void add(MediaDTO add) {

    }
}




