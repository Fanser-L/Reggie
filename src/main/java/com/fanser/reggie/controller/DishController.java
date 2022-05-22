package com.fanser.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanser.reggie.common.R;
import com.fanser.reggie.dto.DishDto;
import com.fanser.reggie.entity.Category;
import com.fanser.reggie.entity.Dish;
import com.fanser.reggie.entity.DishFlavor;
import com.fanser.reggie.service.CategoryService;
import com.fanser.reggie.service.DishFlavorService;
import com.fanser.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){//提交的json数据需要requestBody注解

        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        //改进一下，准确的清理指定的套餐,注意：前端返回的菜品只有状态为在售的才会显示，所以这里直接使用 “_1”
        String key = "dish_" + dishDto.getCategoryId() +"_1";
        redisTemplate.delete(key);

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

        //清理所有的菜品缓存,将dish_ 开头的key全部清理掉
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

        //改进一下，准确的清理指定的套餐,注意：前端返回的菜品只有状态为在售的才会显示，所以这里直接使用 “_1”
        String key = "dish_" + dishDto.getCategoryId() +"_1";
        redisTemplate.delete(key);


        return R.success((DishDto)dish);
    }

    /**
     * 获取菜品分类列表
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        log.info("dish,categoryId{}",dish);
//
//        //构造查询条件对象
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        //添加查询条件，查询状态在售的菜品
//        lambdaQueryWrapper.eq(Dish::getStatus,1);
//
//        //添加排序条件
//        lambdaQueryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(lambdaQueryWrapper);
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;

        //动态的构造key
        String key = "dish_" + dish.getCategoryId() + "_" +dish.getStatus();

        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if (dishDtoList != null){
            //如果存在，直接返回，无需查询数据库
            return R.success(dishDtoList);
        }



        //构造查询条件对象
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加查询条件，查询状态在售的菜品
        lambdaQueryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        //新增功能，为前台页面设计的，选择口味所以需要先展示出有哪些口味可选
        dishDtoList = list.stream().map((item) -> {
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
            //当前菜品id
            Long dishId  = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，查询数据库，将查询到是菜品数据缓存到redis中
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable int status,@RequestParam List<Long> ids){
        log.info("status:{},ids:{}",status,ids);
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }
}
