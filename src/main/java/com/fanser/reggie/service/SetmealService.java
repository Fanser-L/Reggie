package com.fanser.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fanser.reggie.dto.SetmealDto;
import com.fanser.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐，同时需要保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);
    //删除套餐，删除套餐与菜品之间的关系
    public void removeWithDish(List<Long>ids);
    //修改套餐
    public void updateWithSetmeal(SetmealDto setmealDto);
    //根据ID查询套餐信息
    public SetmealDto getByIdWithDish(Long id);
}
