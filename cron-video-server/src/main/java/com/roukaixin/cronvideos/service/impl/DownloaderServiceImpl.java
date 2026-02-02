package com.roukaixin.cronvideos.service.impl;

import com.roukaixin.cronvideos.algorithm.SnowflakeIdWorker;
import com.roukaixin.cronvideos.domain.Downloader;
import com.roukaixin.cronvideos.domain.dto.DownloaderDTO;
import com.roukaixin.cronvideos.domain.vo.DownloaderVO;
import com.roukaixin.cronvideos.listener.event.DownloaderEvent;
import com.roukaixin.cronvideos.mapper.DownloaderMapper;
import com.roukaixin.cronvideos.pooled.PooledDownloader;
import com.roukaixin.cronvideos.service.DownloaderService;
import com.roukaixin.cronvideos.utils.EventUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pankx
 * @description 针对表【aria2_connection(aria2 连接信息)】的数据库操作Service实现
 */
@Service
@Slf4j
public class DownloaderServiceImpl implements DownloaderService {

    private final DownloaderMapper downloaderMapper;

    private final SnowflakeIdWorker snowflakeIdWorker;

    private final PooledDownloader pooledDownloader;


    public DownloaderServiceImpl(DownloaderMapper downloaderMapper,
                                 SnowflakeIdWorker snowflakeIdWorker,
                                 PooledDownloader pooledDownloader) {
        this.downloaderMapper = downloaderMapper;
        this.snowflakeIdWorker = snowflakeIdWorker;
        this.pooledDownloader = pooledDownloader;
    }

    @Override
    public List<DownloaderVO> list() {
        List<Downloader> downloaderList = downloaderMapper.selectAll();
        List<DownloaderVO> vos = new ArrayList<>();
        downloaderList.forEach(aria2 -> {
            DownloaderVO vo = new DownloaderVO();
            BeanUtils.copyProperties(aria2, vo);
            vos.add(vo);
        });
        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(DownloaderDTO add) {
        Downloader downloader = new Downloader();
        downloader.setId(snowflakeIdWorker.nextId());
        buildDownloader(add, downloader);
        LocalDateTime now = LocalDateTime.now();
        downloader.setCreateDate(now);
        downloader.setUpdateDate(now);
        downloaderMapper.insert(downloader);
        EventUtils.publishEvent(new DownloaderEvent(DownloaderEvent.Operation.SAVE, downloader));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, DownloaderDTO update) {
        Downloader downloader = new Downloader();
        downloader.setId(id);
        buildDownloader(update, downloader);
        LocalDateTime now = LocalDateTime.now();
        downloader.setUpdateDate(now);
        downloaderMapper.updateById(downloader);
        EventUtils.publishEvent(new DownloaderEvent(DownloaderEvent.Operation.SAVE, downloader));
    }

    @Override
    public void delete(Long id) {
        downloaderMapper.deleteById(id);
        EventUtils.publishEvent(new DownloaderEvent(DownloaderEvent.Operation.SAVE, Downloader.builder().id(id).build()));
    }

    private void buildDownloader(DownloaderDTO update, Downloader downloader) {
        downloader.setType(update.getType());
        downloader.setProtocol(update.getProtocol());
        downloader.setHost(update.getHost());
        downloader.setPort(update.getPort());
        downloader.setSecret(update.getSecret());
        downloader.setWeight(update.getWeight());
    }
}




