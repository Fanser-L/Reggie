package com.fanser.reggie.controller;

import com.alibaba.fastjson.JSON;
import com.fanser.reggie.common.BaseContext;
import com.fanser.reggie.dto.SetmealDto;
import com.fanser.reggie.entity.Employee;
import com.fanser.reggie.entity.Setmeal;
import com.fanser.reggie.entity.SetmealDish;
import com.fanser.reggie.service.CategoryService;
import com.fanser.reggie.service.SetmealService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.LinkedList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebAppConfiguration //使用这个Annotate会在跑单元测试的时候真实的启一个web服务，然后开始调用Controller的Rest API，待单元测试跑完之后再将web服务停掉
//@ExtendWith(MockitoExtension.class)//用于mock对象
@RunWith(SpringRunner.class)
//@ContextConfiguration
@AutoConfigureMockMvc //使用这个注解可以自动注入mockMvc，但是不会启动web服务，需要搭配SpringBootTest使用
@SpringBootTest
//@Transactional
//@WebMvcTest(SetmealController.class) //集成测试使用这个注解以后，需要把所有使用到的的service层代码mock掉
class SetmealControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    SetmealService setmealService;
    @MockBean
    CategoryService categoryService;
//    @InjectMocks
//    SetmealController setmealController;
    MockHttpSession session;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        session = new MockHttpSession();
        BaseContext.setCurrentId(1l);
    }

    @SneakyThrows
    @Test
    @Rollback
    void save() {
        //前端请求参数 setmealDto type：post contentType：json url：/setmeal
        //需要先进行登陆操作,如果使用的是jwt的方式，那么还需要公私钥进行解密，这部分暂时不讨论
        Employee employee = new Employee();
        employee.setUsername("admin");
        employee.setPassword("123456");
        mockMvc.perform(post("/employee/login").content(JSON.toJSONString(employee))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).session(session))
                .andExpect(status().isOk());
        //************************************************************************************
        SetmealDto setmealDto = new SetmealDto();
        LinkedList<SetmealDish> setmealDishes = new LinkedList<>();

        SetmealDish setmealDish = new SetmealDish();
        setmealDish.setSetmealId(1413342269393674242l);
        setmealDish.setDishId(1524332019493920770l);
        setmealDish.setCopies(1);
        setmealDish.setName("aaa");
        setmealDish.setPrice(new BigDecimal(23400));

        setmealDishes.add(setmealDish);

        setmealDto.setSetmealDishes(setmealDishes);
        setmealDto.setDescription("");
        setmealDto.setPrice(new BigDecimal(123400));
        setmealDto.setName("超值套餐1号");
        setmealDto.setCategoryName("商务套餐");
        setmealDto.setStatus(1);
        setmealDto.setImage("b7918ba8-36e7-4260-ab12-38e26f996d1f.jpg");
        setmealDto.setCategoryId(1413342269393674242l);

        MockHttpServletRequestBuilder content = post("/setmeal").session(session)
                .contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(setmealDto));
        MvcResult mvcResult = this.mockMvc.perform(content).andExpect(status().isOk()).andDo(print()).andReturn();
        System.out.println(mvcResult);
        //Assertions.assertEquals("创建套餐成功",mvcResult.getResponse());
    }

    @SneakyThrows
    @Test
    void list() {
        Setmeal setmeal = new Setmeal();
        setmeal.setPrice(new BigDecimal(500));
        setmeal.setName("叫花鸡");
        setmeal.setStatus(1);
        setmeal.setCategoryId(1413342269393674242l);
        setmeal.setDescription("= = = = =");
        this.mockMvc.perform(get("/setmeal/list").contentType(MediaType.APPLICATION_JSON).content(JSON.toJSONString(setmeal)))
                .andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    void getById() {

    }
//    /**
//     * 获取登入信息session
//     * @return
//     * @throws Exception
//     */
//    private HttpSession getLoginSession() throws Exception{
//        // mock request get login session
//        // url = /xxx/xxx/{username}/{password}
//        Employee employee = new Employee();
//        employee.setUsername("admin");
//        employee.setPassword("123456");
//        MvcResult result = this.mockMvc
//                .perform((get("/employee/login")).content(JSON.toJSONString(employee)))
//                .andExpect(status().isOk())
//                .andReturn();
//        return result.getRequest().getSession();
//    }
}