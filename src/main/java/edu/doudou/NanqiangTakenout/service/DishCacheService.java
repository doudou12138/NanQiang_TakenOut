package edu.doudou.NanqiangTakenout.service;

import edu.doudou.NanqiangTakenout.Entity.Dish;
import edu.doudou.NanqiangTakenout.dto.DishDto;

import java.util.List;

public interface DishCacheService {

    /**
     * 根据菜品在缓存中查询菜品列表
     * @param dish
     * @return
     */
    List<DishDto> getDishList(Dish dish);

    /**
     * 在缓存中保存菜品列表
     * @param dish
     * @param dishDtoList
     */
    void saveDishList(Dish dish, List<DishDto> dishDtoList);

    void deleteDishCache(DishDto dishDto);

}
