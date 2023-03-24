package com.fanser.reggie.controller;


import com.fanser.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/orderdetail")
@Slf4j
public class OrderDetailController {

    @Resource
    private OrdersService ordersService;

}