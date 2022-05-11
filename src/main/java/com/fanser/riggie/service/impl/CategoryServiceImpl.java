package com.fanser.riggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanser.riggie.common.CustomException;
import com.fanser.riggie.entity.Category;
import com.fanser.riggie.entity.Dish;
import com.fanser.riggie.entity.Setmeal;
import com.fanser.riggie.mapper.CategoryMapper;
import com.fanser.riggie.service.CategoryService;
import com.fanser.riggie.service.DishService;
import com.fanser.riggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = (int) dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果已经关联了菜品，抛出一个业务异常
        if (count>0){
            //说明已经关联了菜品，抛出一个业务异常
            throw new CustomException("当前菜品关联了分类项，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联了套餐，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = (int) setmealService.count(setmealLambdaQueryWrapper);

        if (count1>0){
            //说明已经关联了菜品，抛出一个业务异常
            throw new CustomException("当前菜品关联了套餐，不能删除");
        }
        //正常删除菜品
        super.removeById(id);
    }
}
