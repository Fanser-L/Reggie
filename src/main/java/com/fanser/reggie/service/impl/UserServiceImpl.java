package com.fanser.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fanser.reggie.entity.User;
import com.fanser.reggie.mapper.UserMapper;
import com.fanser.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}