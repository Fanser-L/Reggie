package com.fanser.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充
 * 实现更新人和更新信息时间的自动填充
 */
@Configuration
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[INSERT]");
        log.info(metaObject.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);
        metaObject.setValue("createTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentId());
//        metaObject.setValue("createUser",new Long(1));
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
//        metaObject.setValue("updateUser",new Long(1));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[UPDATE]");
        log.info(metaObject.toString());

        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}
