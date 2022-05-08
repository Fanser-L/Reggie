package com.fanser.riggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanser.riggie.common.R;
import com.fanser.riggie.entity.Category;
import com.fanser.riggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category.toString());
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 菜品分类分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        log.info("currentPage:{},pageSize:{},String name:{}",page,pageSize,name);
        //1.构造分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //2.构建条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //3.添加排序条件
        lqw.orderByAsc(Category::getSort);
//        log.info("name:{}",name);
//        lqw.like(StringUtils.hasLength(name),Category::getName,name);
        //4.执行查询
        categoryService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }

    /**
     * 根据行id来删除对应的菜品分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(HttpServletRequest request,Long id){
        log.info("菜品名称：{},菜品ID：{}",request.getSession().getAttribute("category"),id);

        categoryService.removeById(id);
        return R.success("删除成功");
    }

}


