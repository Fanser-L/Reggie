package com.fanser.riggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/*
* 统一返回前端的数据，服务器端响应的数据都会封装成同一种类对象返回到前端
*
* */
@Data
public class R <T>{
    private Integer code;//编码校验，1为成功，0和其他数字都表示失败。
    private String msg;//返回到页面的错误信息
    private T data; //返回前端页面的json数据

    private Map map = new HashMap();//动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.code = 0;
        return r;
    }
    public R<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }
}
