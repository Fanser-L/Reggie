package com.fanser.riggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanser.riggie.entity.Dish;
import com.fanser.riggie.mapper.DishMapper;
import com.fanser.riggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
