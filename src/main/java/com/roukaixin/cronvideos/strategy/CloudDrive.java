package com.roukaixin.cronvideos.strategy;

import com.roukaixin.cronvideos.pojo.CloudShare;
import com.roukaixin.cronvideos.pojo.Media;

public interface CloudDrive {

    Integer download(CloudShare cloudShares, Media media);
}
