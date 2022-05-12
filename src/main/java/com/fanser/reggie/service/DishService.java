package com.fanser.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanser.reggie.dto.DishDto;
import com.fanser.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要调用两张表：dish 、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品的信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //修改菜品信息
    public void updateWithFlavor(DishDto dishDto);
}
