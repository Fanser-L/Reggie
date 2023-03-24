package com.fanser.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fanser.reggie.entity.Orders;


public interface OrdersService extends IService<Orders> {


    /**
     * 提交用户订单
     * @param orders
     */
    void submit(Orders orders);
}
