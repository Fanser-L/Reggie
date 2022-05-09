package com.fanser.riggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanser.riggie.entity.Setmeal;
import com.fanser.riggie.mapper.SetmealMapper;
import com.fanser.riggie.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
