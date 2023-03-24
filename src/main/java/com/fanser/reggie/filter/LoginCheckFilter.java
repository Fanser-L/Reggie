package com.fanser.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.fanser.reggie.common.BaseContext;
import com.fanser.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
@Component//不加这个注解会出现莫名其妙的错误
//@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/backend")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        //拦截请求
//        log.info("拦截到的请求：{}",request.getRequestURI());
//        //定义不需要处理的请求路径
//        ArrayList<String> urls = new ArrayList<String>();
//        urls.add("/employee/login");
//        urls.add("/employee/logout");
//        urls.add("/backend/**");
//        urls.add("/front/**");
//        urls.add("/backend/page/login/login.html");
//
//        //过滤器，从session中获取用户
//        Employee employee = (Employee) request.getSession().getAttribute("employee");
//        log.info("userInfo:{}",employee);
//
//        String requestURL = request.getRequestURI();
//        if (urls.contains(requestURL)){
//            filterChain.doFilter(request,response);
//        }
//        if (employee==null && !urls.contains(requestURL)){
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
                "/common/**",
                "/user/sendMsg",
                "/user/login"
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
            Long empId = (Long) request.getSession().getAttribute("employee");

            log.info("用户已经登陆,id为：{}",empId);
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }
        //4-2 判断登陆状态，若已经登陆直接放行(移动端)
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，id为：{}", request.getSession().getAttribute("user"));
            Long empId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(empId);
            long id = Thread.currentThread().getId();
            log.info("线程ID为：{}", id);
            filterChain.doFilter(request, response);
            return;
        }

        //5.如果未登陆，直接返回登陆页面，通过输出流的方式向客户端响应数据
        log.info("用户未登陆");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

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
