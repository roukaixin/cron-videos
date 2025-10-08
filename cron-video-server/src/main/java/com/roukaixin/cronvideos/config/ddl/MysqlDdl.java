package com.roukaixin.cronvideos.config.ddl;

import com.baomidou.mybatisplus.extension.ddl.IDdl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.function.Consumer;

@Component
public class MysqlDdl implements IDdl {

    @Resource
    private DataSource dataSource;

    @Override
    public void runScript(Consumer<DataSource> consumer) {
        consumer.accept(dataSource);
    }

    @Override
    public List<String> getSqlFiles() {
        return List.of("db/mysql/init.sql");
    }

}
