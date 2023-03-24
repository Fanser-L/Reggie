package com.fanser.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 员工实体
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;//身份证号码

    private Integer status;

    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private Long updateUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id.equals(employee.id) && Objects.equals(username, employee.username) && Objects.equals(name, employee.name) && Objects.equals(password, employee.password) && Objects.equals(phone, employee.phone) && Objects.equals(sex, employee.sex) && Objects.equals(idNumber, employee.idNumber) && Objects.equals(status, employee.status) && Objects.equals(createTime, employee.createTime) && Objects.equals(updateTime, employee.updateTime) && Objects.equals(createUser, employee.createUser) && Objects.equals(updateUser, employee.updateUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, name, password, phone, sex, idNumber, status, createTime, updateTime, createUser, updateUser);
    }
}
