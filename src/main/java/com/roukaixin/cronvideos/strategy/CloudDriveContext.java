package com.roukaixin.cronvideos.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class CloudDriveContext {


    private final Map<String, CloudDrive> cloudDriveMap;

    public CloudDriveContext(Map<String, CloudDrive> cloudDriveMap) {
        this.cloudDriveMap = cloudDriveMap;
    }


    public CloudDrive getCloudDrive(Integer key) {
        return cloudDriveMap.get("cloud_drive_" + key);
    }
}
