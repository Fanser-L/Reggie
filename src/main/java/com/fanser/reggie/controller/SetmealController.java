package com.fanser.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanser.reggie.common.R;
import com.fanser.reggie.dto.SetmealDto;
import com.fanser.reggie.entity.Category;
import com.fanser.reggie.entity.Setmeal;
import com.fanser.reggie.service.CategoryService;
import com.fanser.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);

        setmealService.saveWithDish(setmealDto);
        return R.success("创建套餐成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids==>{}" + ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("修改套餐信息{}", setmealDto);
        // 执行更新。
        setmealService.updateWithSetmeal(setmealDto);
        return R.success("套餐修改成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        // 构造查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 根据id查询套餐信息
     *(套餐信息的回显)
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        log.info("根据id查询套餐信息:{}", id);
        // 调用service执行查询
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);

        //基本上所有的mp的方法都需要加上对应的实体类对象
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper();
        //添加查询条件，根据name进行like模糊查询，查询指定套餐
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //到这里其实页面的大部分内容已经有了，但是还缺少一个部分，套餐名字没有，setmeal里是没有套餐名的，使用dto对象来装入名字
        setmealService.page(pageInfo, lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        List<Setmeal> records = pageInfo.getRecords();


        List<SetmealDto> list = records.stream().map((item -> {

            //对象拷贝
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);

            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id来获取对应的类名
            Category category = categoryService.getById(categoryId);
            //有了id之后就可以获取类名，然后将类名给dto对象了
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        })).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 更新套餐是否在售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> setmealStatusChange(@PathVariable Integer status,Long[] ids ){
        for (int i = 0; i < ids.length; i++) {
            // 获取菜品
            Setmeal setmeal = setmealService.getById(ids[i]);
            setmeal.setStatus(status);
            // 修改状态
            setmealService.updateById(setmeal);
        }
        return R.success("修改成功");
    }
}
