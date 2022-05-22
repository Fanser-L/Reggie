package com.fanser.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanser.reggie.common.CustomException;
import com.fanser.reggie.dto.SetmealDto;
import com.fanser.reggie.entity.Setmeal;
import com.fanser.reggie.entity.SetmealDish;
import com.fanser.reggie.mapper.SetmealMapper;
import com.fanser.reggie.service.SetmealDishService;
import com.fanser.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        //保存套餐的基本信息，操作setmeal_dish，执行insert操作
        this.save(setmealDto);

        //套餐中联系着多个菜品，但是这些菜品并没有id，所以获取到的时候需要给它设置上id（联表查询）
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        })).collect(Collectors.toList());


        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //首先需要查询套餐的状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //查询对应id的套餐
        queryWrapper.in(Setmeal::getId,ids);
        //查询该id的套餐是否在售
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = (int) this.count(queryWrapper);
        if (count>0){
            //如果不能删除，抛出异常
            throw  new CustomException("套餐正在售卖，不允许删除");
        }

        //如果可以删除，抛出一个业务异常
        this.removeByIds(ids);

        //删除关系表中的数据----setmealDish
        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getDishId,ids);

        setmealDishService.remove(lambdaQueryWrapper);
    }

    @Override
    public void updateStatusById(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.in(ids!=null,Setmeal::getId,ids);
        List<Setmeal> list = this.list(lambdaQueryWrapper);
//        list.stream().map((item)->{
//            item.setStatus(status);
//            return item;
//        }).collect(Collectors.toList());

        for (Setmeal setmeal : list) {
            setmeal.setStatus(status);
            this.updateById(setmeal);
        }
    }

    @Override
    public SetmealDto getDate(Long id) {

        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);

        if (setmeal!=null){
            BeanUtils.copyProperties(setmeal,setmealDto);
            //表结构  ： setmealDto——>setmealDishes && setmeal
            List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
            setmealDto.setSetmealDishes(list);
            return setmealDto;
        }

        return null;
    }
}
