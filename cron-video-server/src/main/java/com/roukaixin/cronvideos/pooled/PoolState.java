package com.roukaixin.cronvideos.pooled;

import java.util.ArrayList;
import java.util.List;

public class PoolState {

    private final PooledDownloader downloader;

    protected final List<PooledClient> available = new ArrayList<>();

    protected final List<PooledClient> notAvailable = new ArrayList<>();

    protected PoolState(PooledDownloader downloader) {
        this.downloader = downloader;
    }

}
