package com.roukaixin.cronvideos.controller;

import com.roukaixin.cronvideos.pojo.Aria2Server;
import com.roukaixin.cronvideos.pojo.R;
import com.roukaixin.cronvideos.pojo.dto.Aria2ServerDTO;
import com.roukaixin.cronvideos.pojo.vo.Aria2ServerVO;
import com.roukaixin.cronvideos.service.Aria2ServerService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/aria2")
public class Aria2ServerController {

    private final Aria2ServerService aria2ServerService;

    public Aria2ServerController(Aria2ServerService aria2ServerService) {
        this.aria2ServerService = aria2ServerService;
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
        return aria2ServerService.add(aria2ServerDto);
    }

    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable("id") Long id) {
        return aria2ServerService.delete(id);
    }

    @PutMapping("/update/{id}")
    public R<String> update(@PathVariable("id") Long id, @RequestBody Aria2ServerDTO aria2ServerDto) {
        return aria2ServerService.update(id, aria2ServerDto);
    }

}
