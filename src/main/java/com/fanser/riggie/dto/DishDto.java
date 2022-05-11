package com.fanser.riggie.dto;


import com.fanser.riggie.entity.Dish;
import com.fanser.riggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * date transfer object 数据传输对象
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
