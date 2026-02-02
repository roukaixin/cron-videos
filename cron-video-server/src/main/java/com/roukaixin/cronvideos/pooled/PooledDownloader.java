package com.roukaixin.cronvideos.pooled;


import com.roukaixin.cronvideos.downloader.Client;
import com.roukaixin.cronvideos.downloader.Downloader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 下载器池
 *
 * @author roukaixin
 * @date 2026/1/10 15:22
 */
@Slf4j
public class PooledDownloader implements Downloader {

    private final PoolState state = new PoolState(this);


    @Override
    public Client getClient() {
        return state.available.getFirst().getProxyClient();
    }

    public void popClient(Long id) {
        synchronized (state) {
            List<PooledClient> available = state.available;
            List<PooledClient> notAvailable = state.notAvailable;
            Optional<PooledClient> client = available.stream().filter(e -> e.getRealClient().identifier().equals(id)).findFirst();
            if (client.isPresent()) {
                available.remove(client.get());
                return;
            }
            client = available.stream().filter(e -> e.getRealClient().identifier().equals(id)).findFirst();
            client.ifPresent(notAvailable::remove);
        }
    }

    public void pushClient(PooledClient client) {
        synchronized (state) {
            if (Objects.isNull(client)) {
                return;
            }
            if (client.isValid()) {
                state.available.add(client);
            } else {
                state.notAvailable.add(client);
            }
            log.info("available {}", state.available.size());
            log.info("notAvailable {}", state.notAvailable.size());
        }
    }
}
