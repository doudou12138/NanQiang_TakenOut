package edu.doudou.NanqiangTakenout.Entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Employee {
    private static final long serialVersionUID = 1L;

    private long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    //mp提供的注解.fill指定该字段在什么时候填充
    //INSERT指在创建时填充该字段
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //INSERT_UPDATE指在创建和更新时填充该字段
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
