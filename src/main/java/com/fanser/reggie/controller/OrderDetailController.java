package com.fanser.reggie.controller;

import com.fanser.reggie.service.OrderDetailService;
import com.fanser.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orderDetail")
@Slf4j
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;
}
