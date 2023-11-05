package edu.doudou.NanqiangTakenout.dto;

import edu.doudou.NanqiangTakenout.Entity.Setmeal;
import edu.doudou.NanqiangTakenout.Entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetMealDto extends Setmeal {

    private List<SetmealDish> setMealDishList;

    private String categoryName;

}
