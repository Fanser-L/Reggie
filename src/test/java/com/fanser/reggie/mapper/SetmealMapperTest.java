package com.fanser.reggie.mapper;

import com.fanser.reggie.common.BaseContext;
import com.fanser.reggie.entity.Setmeal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class SetmealMapperTest {

    @Autowired
    SetmealMapper setmealMapper;

    @BeforeEach
    void setUp() {
        //模拟登陆后的场景
        BaseContext.setCurrentId(1l);
    }

    @Test
    void select() {
        Assertions.assertInstanceOf(Setmeal.class,setmealMapper.selectById(1597108688957493250l));
    }

    @Test
    @Rollback
    void insert() {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(1);
        setmeal.setCategoryId(1413384954989060097l);
        setmeal.setName("rice");
        setmeal.setPrice(new BigDecimal(2));
        Assertions.assertEquals(1,setmealMapper.insert(setmeal));
    }

    @Test
    @Rollback
    void update() {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(1);
        setmeal.setCategoryId(1413384954989060097l);
        setmeal.setName("rice1");
        setmeal.setPrice(new BigDecimal(3));
        setmeal.setId(1597108688957493250l);
        Assertions.assertEquals(1, setmealMapper.updateById(setmeal));
    }

    @Test
    void delete() {
        setmealMapper.deleteById(1597108688957493250l);
    }
}