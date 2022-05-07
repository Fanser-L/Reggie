package com.fanser.riggie.filter;

import com.alibaba.fastjson.JSON;
import com.fanser.riggie.common.R;
import com.fanser.riggie.entity.Employee;
import com.fanser.riggie.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/*
* 过滤所有请求
* */
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        //过滤器，从session中获取用户
//        Employee employee = (Employee) request.getSession().getAttribute(Constants.USER_SESSION);
//        if (employee==null){
//            response.sendRedirect(request.getContextPath()+"/backend/page/login/login.html");
//        }else{
//            log.info("拦截请求：{}",request.getRequestURI());
//            filterChain.doFilter(request,response);
//        }
        //1.获取本次请求的uri
        String requestURI = request.getRequestURI();

        log.info("拦截到请求："+requestURI);
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
        };
        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3.如果不需要处理，直接放行
        if (check){
            log.info("本次请求不需要进行处理");
            filterChain.doFilter(request,response);
            return;
        }
        //4.获取session对象，判断是否已经登陆
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已经登陆");
            filterChain.doFilter(request,response);
            return;
        }
        //5.如果未登陆，直接返回登陆页面，通过输出流的方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("用户已经登陆");
        return;


    }
    /*
    * 路径匹配检测是否需要放行
    * */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
