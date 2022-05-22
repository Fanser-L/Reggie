package com.fanser.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanser.reggie.dto.SetmealDto;
import com.fanser.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐的同时删除对应的菜品
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 通过套餐id来修改套餐状态
     * @param status
     * @param ids
     */
    public void updateStatusById(Integer status,List<Long> ids);

    public SetmealDto getDate(Long id);
}
