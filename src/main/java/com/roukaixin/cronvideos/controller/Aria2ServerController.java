package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.algorithm.SmoothWeightedRoundRobin;
import com.roukaixin.cronvideos.handler.Aria2Handler;
import com.roukaixin.cronvideos.mapper.Aria2DownloadTasksMapper;
import com.roukaixin.cronvideos.pojo.Aria2Server;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.Aria2ServerDTO;
import com.roukaixin.cronvideos.pojo.vo.Aria2ServerVO;
import com.roukaixin.cronvideos.pool.Aria2WebSocketPool;
import com.roukaixin.cronvideos.service.Aria2ServerService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/aria2")
public class Aria2ServerController {

    private final Aria2ServerService aria2ServerService;

    private final Aria2WebSocketPool aria2WebSocketPool;

    private final Aria2DownloadTasksMapper aria2DownloadTasksMapper;

    private final SmoothWeightedRoundRobin smoothWeightedRoundRobin;

    public Aria2ServerController(Aria2ServerService aria2ServerService,
                                 Aria2WebSocketPool aria2WebSocketPool,
                                 Aria2DownloadTasksMapper aria2DownloadTasksMapper,
                                 SmoothWeightedRoundRobin smoothWeightedRoundRobin) {
        this.aria2ServerService = aria2ServerService;
        this.aria2WebSocketPool = aria2WebSocketPool;
        this.aria2DownloadTasksMapper = aria2DownloadTasksMapper;
        this.smoothWeightedRoundRobin = smoothWeightedRoundRobin;
    }

    @GetMapping("/list")
    public R<List<Aria2ServerVO>> list() {
        List<Aria2Server> list = aria2ServerService.list();
        List<Aria2ServerVO> vos = new ArrayList<>();
        list.forEach(aria2 -> {
            Aria2ServerVO vo = new Aria2ServerVO();
            BeanUtils.copyProperties(aria2, vo);
            vos.add(vo);
        });
        return R.<List<Aria2ServerVO>>builder().code(200).data(vos).build();
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody Aria2ServerDTO aria2ServerDto) {
        Aria2Server aria2Server = new Aria2Server();
        BeanUtils.copyProperties(aria2ServerDto, aria2Server);
        aria2ServerService.save(aria2Server);
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                client,
                new Aria2Handler(aria2DownloadTasksMapper, aria2Server.getId(), aria2WebSocketPool),
                "ws://" + aria2Server.getIp() + ":" + aria2Server.getPort() + "/jsonrpc"
        );
        manager.start();
        aria2WebSocketPool.put(aria2Server.getId(), manager);
        smoothWeightedRoundRobin.put(aria2Server.getId(), aria2Server.getWeight());
        return R.<String>builder().code(200).message("添加成功").build();
    }

    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable("id") Long id) {
        aria2ServerService.removeById(id);
        WebSocketConnectionManager manager = aria2WebSocketPool.getOrDefault(id);
        if (manager != null) {
            manager.stop();
        }
        aria2WebSocketPool.remove(id);
        smoothWeightedRoundRobin.remove(id);
        return R.<String>builder().code(200).message("删除成功").build();
    }

    @PutMapping("/update/{id}")
    public R<String> update(@PathVariable("id") Long id, @RequestBody Aria2ServerDTO aria2ServerDto) {
        Aria2Server aria2Server = new Aria2Server();
        BeanUtils.copyProperties(aria2ServerDto, aria2Server);
        aria2Server.setId(id);
        aria2ServerService.updateById(aria2Server);
        WebSocketConnectionManager manager = aria2WebSocketPool.getOrDefault(id);
        if (manager != null) {
            manager.stop();
        }
        StandardWebSocketClient client = new StandardWebSocketClient();
        manager = new WebSocketConnectionManager(
                client,
                new Aria2Handler(aria2DownloadTasksMapper, aria2Server.getId(), aria2WebSocketPool),
                "ws://" + aria2Server.getIp() + ":" + aria2Server.getPort() + "/jsonrpc"
        );
        manager.start();
        aria2WebSocketPool.put(aria2Server.getId(), manager);
        smoothWeightedRoundRobin.update(aria2Server.getId(), aria2Server.getWeight());
        return R.<String>builder().code(200).message("修改成功").build();
    }

}
