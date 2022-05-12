package com.fanser.reggie.dto;


import com.fanser.reggie.entity.Setmeal;
import com.fanser.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
