package edu.doudou.NanqiangTakenout.service.impl;

import edu.doudou.NanqiangTakenout.Entity.Dish;
import edu.doudou.NanqiangTakenout.constants.DishCacheConstants;
import edu.doudou.NanqiangTakenout.dto.DishDto;
import edu.doudou.NanqiangTakenout.service.DishCacheService;
import edu.doudou.NanqiangTakenout.utils.redis.RedisUtil;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishCacheServiceImpl implements DishCacheService {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<DishDto> getDishList(Dish dish) {
        String key = getDishListKey(dish);
        Object list =  redisUtil.get(key);
        if(list==null){
            return null;
        }else{
            return (List<DishDto>) list;
        }
    }

    @Override
    public void saveDishList(Dish dish, List<DishDto> dishDtoList) {
        String key = getDishListKey(dish);
        redisUtil.set(key,dishDtoList);
    }

    /**
     * 删除对应口味菜品列表缓存
     * @param dishDto
     */
    @Override
    public void deleteDishCache(DishDto dishDto) {
        String key = getDishListKey(dishDto);
        redisUtil.delete(key);
    }



    private String getDishListKey(Dish dish){
        return DishCacheConstants.DISH_LIST_KEY_PREFIX + dish.getCategoryId()+DishCacheConstants.DISH_LIST_KEY_EXTRACT_INFO;
    }

}
