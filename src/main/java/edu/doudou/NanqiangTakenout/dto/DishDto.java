package edu.doudou.NanqiangTakenout.dto;

import edu.doudou.NanqiangTakenout.Entity.Dish;
import edu.doudou.NanqiangTakenout.Entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;

}
