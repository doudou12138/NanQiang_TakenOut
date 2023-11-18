package edu.doudou.NanqiangTakenout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    Page<DishDto> getDishPage(String name, int page, int pageSize);

    /**
     * 在缓存中保存菜品列表
     * @param dish
     * @param dishDtoList
     */
    void saveDishList(Dish dish, List<DishDto> dishDtoList);

    /**
     * 在缓存中保存菜品分页列表
     * @param dishDtoList
     * @param page
     * @param pageSize
     */
    void saveDishPage(String name, Page<DishDto> dishDtoList , int page, int pageSize);

    void deleteDishCache(DishDto dishDto);

    void deleteDishListCache(DishDto dishDto);


}
