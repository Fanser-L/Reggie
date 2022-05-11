package com.fanser.riggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanser.riggie.common.R;
import com.fanser.riggie.dto.DishDto;
import com.fanser.riggie.entity.Category;
import com.fanser.riggie.entity.Dish;
import com.fanser.riggie.service.CategoryService;
import com.fanser.riggie.service.DishFlavorService;
import com.fanser.riggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired//波浪线 因为没有构造器导致
    private DishFlavorService dishFlavorService;
    @PostMapping
    public R<String> save(HttpServletRequest request ,@RequestBody DishDto dishDto){//提交的json数据需要requestBody注解
        log.info("数据传输对象：{}",dishDto);
//
//        Long id = (Long) request.getSession().getAttribute("dishDto");
////        log.info();
//        dishDto.setUpdateUser(id);
        log.info("dishId:{}",dishDto);
        dishService.savaWithFlavor(dishDto);


        return R.success("新增菜品成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dtoPage = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Strings.isNotEmpty(name),Dish::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,lambdaQueryWrapper);

        //对象拷贝,忽略records集合（这不就等于把所有查询的数据都丢了吗？？）
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoList = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            //获取对应的id下的categoryName
            Long categoryId = item.getCategoryId();
            if (categoryId!=null) {
                Category category = categoryService.getById(categoryId);
                String categoryName = category.getName();
                //把这些name塞进dishDto里
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dishDtoList);

        return R.success(dtoPage);
    }
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id ){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        log.info("dishDto:{}",dishDto);
        return R.success(dishDto);
    }
    @PutMapping
    public R<DishDto> update(DishDto dishDto){
        Dish dish = dishService.getById(dishDto.getId());
        dishService.updateWithFlavor(dishDto);


        return R.success((DishDto)dish);
    }
}
