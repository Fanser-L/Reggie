package com.fanser.riggie.service.impl;


import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanser.riggie.entity.DishFlavor;
import com.fanser.riggie.mapper.DishFlavorMapper;
import com.fanser.riggie.service.DishFlavorService;
import com.fanser.riggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {
}
