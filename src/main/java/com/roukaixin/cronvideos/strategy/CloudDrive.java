package com.roukaixin.cronvideos.strategy;

import com.roukaixin.cronvideos.pojo.CloudShares;
import com.roukaixin.cronvideos.pojo.Media;

public interface CloudDrive {

    void download(CloudShares cloudShares, Media media);
}
