package com.fanser.reggie.service;

import com.fanser.reggie.ReggieApplication;
import com.fanser.reggie.common.BaseContext;
import com.fanser.reggie.common.CustomException;
import com.fanser.reggie.dto.SetmealDto;
import com.fanser.reggie.entity.Setmeal;
import com.fanser.reggie.entity.SetmealDish;
import com.fanser.reggie.mapper.SetmealMapper;
import com.fanser.reggie.service.impl.SetmealServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

//按依赖注入，可以避免整个工程的启动,困难点在于，所有的依赖得理清楚
//@ContextConfiguration(classes = {SetmealServiceImpl.class,SetmealService.class, SetmealMapper.class, Setmeal.class})

@Transactional
@Rollback
@RunWith(SpringRunner.class)
@SpringBootTest                         //默认启动整个程序
//@SpringBootTest(classes = Test.class)          //启动Test类
class SetmealServiceTest {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    private SetmealService setmealService;

    @BeforeAll
    static void beforeAll() {
        BaseContext.setCurrentId(1l);
    }

    @Test
//    @Commit
    void testSaveWithDish() {
        SetmealDto setmealDto = new SetmealDto();
        LinkedList<SetmealDish> setmealDishList = new LinkedList<SetmealDish>();

        SetmealDish setmealDish = new SetmealDish();
        setmealDish.setSetmealId(123l);
        setmealDish.setDishId(123l);
        setmealDish.setName("香辣");
        setmealDish.setPrice(new BigDecimal(50));
        setmealDish.setCopies(1);

        setmealDishList.add(setmealDish);

        setmealDto.setSetmealDishes(setmealDishList);
        setmealDto.setCategoryName("kfc 50");
        setmealDto.setCategoryId(123l);
        setmealDto.setName("50吃到爽");
        setmealDto.setPrice(new BigDecimal(50));
        setmealDto.setStatus(1);
        setmealDto.setDescription("快 v 我 50");
        setmealService.saveWithDish(setmealDto);
    }

    @Test
    void removeWithDishKO() {
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(1597141336945876993l);
        //在售套餐不可删除
        Assertions.assertThrows(CustomException.class,() ->setmealService.removeWithDish(ids));
    }

    @Test
    void removeWithDishOK() {
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(1597141336945876994l);
        //不在售套餐可删除
        Assertions.assertDoesNotThrow(() -> setmealService.removeWithDish(ids));
    }

    @Test
    void updateWithSetmeal() {
        SetmealDto setmealDto = new SetmealDto();
        LinkedList<SetmealDish> setmealDishList = new LinkedList<SetmealDish>();

        SetmealDish setmealDish = new SetmealDish();
        setmealDish.setSetmealId(123l);
        setmealDish.setDishId(123l);
        setmealDish.setName("香辣");
        setmealDish.setPrice(new BigDecimal(50));
        setmealDish.setCopies(1);

        setmealDishList.add(setmealDish);

        setmealDto.setSetmealDishes(setmealDishList);
        setmealDto.setCategoryName("kfc 50");
        setmealDto.setCategoryId(123l);
        setmealDto.setName("50吃到爽");
        setmealDto.setPrice(new BigDecimal(50));
        setmealDto.setStatus(1);
        setmealDto.setDescription("快 v 我 50");
        setmealDto.setId(1597108688957493250l);
        setmealService.updateWithSetmeal(setmealDto);
    }

    @Test
    @DisplayName("根据ID查询套餐")
    void getByIdWithDish() {

    }
}