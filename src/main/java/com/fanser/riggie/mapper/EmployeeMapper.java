package com.fanser.riggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanser.riggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
