package com.fanser.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fanser.reggie.common.R;
import com.fanser.reggie.entity.User;
import com.fanser.reggie.service.UserService;
import com.fanser.reggie.util.SMSUtils;
import com.fanser.reggie.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if (Strings.isNotEmpty(phone)) {
            //通过util生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4);
            //控制台打印输出，模拟手机接收验证码
            log.info("code:{}", code);

            //调用阿里云api完成短信发送 {【标识名】 【验证码模板】 【手机号】 【验证码】}
//            SMSUtils.sendMessage("瑞吉外卖","",phone,code);

//            需要将生成的验证码保存到session
            session.setAttribute(phone, code);
            return R.success("短信验证码已发送成功");
        }
        return R.error("短信发送失败");

    }

    /**
     * 获取前端返回的手机号和验证码，与服务器端中存入的session比对，如果一致就通过
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
//        log.info(session.getAttribute("phone").toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        String codeInSession = session.getAttribute(phone).toString();

        if (code != null && codeInSession.equals(code)) {
            //如果传入的phone不为空，且与后台session一致，则进行手机号查找
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //判断当前手机号是否为新用户，如果是新用户，直接创建用户
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登陆失败");
    }
}

