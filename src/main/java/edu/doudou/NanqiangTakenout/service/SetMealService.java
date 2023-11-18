package edu.doudou.NanqiangTakenout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.doudou.NanqiangTakenout.Entity.Setmeal;
import edu.doudou.NanqiangTakenout.dto.SetMealDto;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {

    void saveWithDishes(SetMealDto setMealDto);

    Page toPage(int page, int pageSize, String name);

    void removeWithDishes(List<Long> ids);

    List<Setmeal> toList(Setmeal setMeal);
}

