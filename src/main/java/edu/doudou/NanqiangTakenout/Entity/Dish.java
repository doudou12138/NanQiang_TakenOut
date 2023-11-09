package edu.doudou.NanqiangTakenout.Entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Dish implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId
    private long id;

    private String name;

    //菜品分类id
    private Long categoryId;

    private BigDecimal price;

    //商品码
    private String code;

    //图片
    private String image;

    //菜品描述
    private String description;

    //0 停售,1 可售
    private Integer status;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
