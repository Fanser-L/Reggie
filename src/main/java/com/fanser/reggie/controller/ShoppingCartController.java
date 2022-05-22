package com.fanser.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanser.reggie.common.BaseContext;
import com.fanser.reggie.common.R;
import com.fanser.reggie.entity.ShoppingCart;
import com.fanser.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info(shoppingCart.toString());

        //设置用户id，指定当前是哪个用户的购物车
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //同一个菜品被多次加入时，查询当前菜品是否已经在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        if (dishId!=null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //SQL：select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if (cartServiceOne!=null){
            //如果已经存在了，在原来的数量基础上 +1
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else{
            //如果不存在，则添加购物车，数量默认就是 1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }
//    @PostMapping("/sub")
//    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
//        //先查询对应userId的购物车
//        Long userId = BaseContext.getCurrentId();
//        //SQL:select * from shopping_cart where user_id = ?
//        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ShoppingCart::getUserId,userId);
//        //查询对应的购物车下的dish
//        Long dishId = shoppingCart.getDishId();
//        //SQL:select * from shopping_cart where user_id = ? and dish_id = ?
//        queryWrapper.eq(ShoppingCart::getDishId,dishId);
//        ShoppingCart shoppingCart1 = shoppingCartService.getById(queryWrapper);
//
//        return null;
//    }
    /**
     * 客户端的套餐或者是菜品数量减少设置
     * 没必要设置返回值
     * @param shoppingCart
     */
    @PostMapping("/sub")
    @Transactional
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){

        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //代表数量减少的是菜品数量
        if (dishId != null){
            //通过dishId查出购物车对象
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            ShoppingCart cart1 = shoppingCartService.getOne(queryWrapper);
            cart1.setNumber(cart1.getNumber()-1);
            //对数据进行更新操作
            Integer number = cart1.getNumber();
            if (number>0){
                shoppingCartService.updateById(cart1);
            }else if (number == 0){
                shoppingCartService.removeById(cart1);
            }else{
                return R.error("操作异常");
            }

            return R.success(cart1);
        }
        Long setmealId = shoppingCart.getSetmealId();
        if (setmealId != null){
            //代表是套餐数量减少
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            ShoppingCart cart2 = shoppingCartService.getOne(queryWrapper);
            cart2.setNumber(cart2.getNumber()-1);
            //对数据进行更新操作
            Integer number = cart2.getNumber();
            if (number>0){
                shoppingCartService.updateById(cart2);
            }else if (number == 0){
                shoppingCartService.removeById(cart2);
            }else{
                return R.error("操作异常");
            }

            return R.success(cart2);
        }
        return R.error("操作异常");
    }
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        //查看购物车
        log.info("查看购物车....");
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        lambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);

        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        //SQL:delete from shopping_cart where user_id = ?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }

}
