package com.roukaixin.cronvideos.config.ddl;

import com.baomidou.mybatisplus.extension.ddl.SimpleDdl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MysqlDdl extends SimpleDdl {
    @Override
    public List<String> getSqlFiles() {
        return List.of("db/mysql/init.sql");
    }

}
