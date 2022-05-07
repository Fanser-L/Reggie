package com.fanser.riggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fanser.riggie.common.R;
import com.fanser.riggie.entity.Employee;
import com.fanser.riggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * The type Employee controller.
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Login r.
     *
     * @param request  the request
     * @param employee the employee
     * @return the r
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的用户username来查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);//从数据库中获取单个对象
        //3.如果没有查询到对应的username，返回失败
        if (emp == null){
            return R.error("login failure!");
        }
        //4.密码比对，如果账号密码不一致，返回登陆失败
        if (!emp.getPassword().equals(password)){
            return R.error("login failure!");
        }
        //5.查看员工的状态，是否被封号了
        if (emp.getStatus()==0){
            return R.error("账号已被封禁");
        }
        //6.登陆成功，将员工id存入Session对象，并返回登陆成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * Logout r.
     *
     * @param request the request
     * @return the r
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * Save r.
     *
     * @param request  the request
     * @param employee the employee
     * @return the r
     */
    @PostMapping
    public R<String> save(HttpServletRequest request , @RequestBody Employee employee){
        log.info("新增员工，员工信息:{}",employee.toString());

        //设置初始密码123456，使用md5进行加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //获取当前用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return null;
    }

    /**
     * 员工分页查询
     *
     * @param currentPage the current page
     * @param pageSize    the page size
     * @param name        the name
     * @return the r
     */
    @GetMapping("/page")
    public R<Page> page( int currentPage, int pageSize,String name){

        log.info("currentPage:{},pageSize:{},String name:{}",currentPage,pageSize,name);
        //1.构造分页构造器
        Page pageInfo = new Page(currentPage,pageSize);
        //2.构建条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //3.添加过滤条件
        log.info("name:{}",name);
        lqw.like(StringUtils.hasLength(name),Employee::getName,name);
        //4.执行查询
        employeeService.page(pageInfo,lqw);

        return R.success(pageInfo);
    }

    /**
     * 修改是否封号状态
     *
     * @param request  the request
     * @param employee the employee
     * @return the r
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){

        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);

        employeeService.updateById(employee);
        return R.success("success!");
    }
    @GetMapping("{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        if (id!=null) {
            Employee employee = employeeService.getById(id);
            return R.success(employee);
        }
        return null;
    }
}
