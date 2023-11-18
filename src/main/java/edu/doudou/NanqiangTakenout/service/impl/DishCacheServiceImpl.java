package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.doudou.NanqiangTakenout.Entity.Dish;
import edu.doudou.NanqiangTakenout.constants.CommonConstants;
import edu.doudou.NanqiangTakenout.constants.DishCacheConstants;
import edu.doudou.NanqiangTakenout.dto.DishDto;
import edu.doudou.NanqiangTakenout.service.DishCacheService;
import edu.doudou.NanqiangTakenout.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
    public Page getDishPage(String name, int page, int pageSize) {
        String key = getDishPageKey(name,page,pageSize);
        Object list =  redisUtil.get(key);
        if(list==null){
            return null;
        }else{
            return (Page<DishDto>) list;
        }
    }

    @Override
    public void saveDishList(Dish dish, List<DishDto> dishDtoList) {
        String key = getDishListKey(dish);
        redisUtil.set(key,dishDtoList);
    }

    @Override
    public void saveDishPage(String name, Page<DishDto> dishDtoPage, int page, int pageSize) {
        String key = getDishPageKey(name,page,pageSize);
        redisUtil.set(key,dishDtoPage);
    }

    @Override
    public void deleteDishCache(DishDto dishDto) {
        this.deleteDishListCache(dishDto);
        String key = getDishPageKey();
        redisUtil.delete(key,1);
    }


    /**
     * 删除对应口味菜品列表缓存
     * @param dishDto
     */
    @Override
    public void deleteDishListCache(DishDto dishDto) {
        String key = getDishListKey(dishDto);
        redisUtil.delete(key);
    }



    private String getDishListKey(Dish dish){
        return DishCacheConstants.DISH_LIST_KEY_PREFIX + dish.getCategoryId()+DishCacheConstants.DISH_LIST_KEY_EXTRACT_INFO;
    }


    private String getDishPageKey(String name, int page, int pageSize) {
        return DishCacheConstants.DISH_PAGE_KEY_PREFIX + name+page+" "+pageSize+DishCacheConstants.DISH_PAGE_KEY_EXTRACT_INFO;
    }

    private String getDishPageKey(){
        return DishCacheConstants.DISH_PAGE_KEY_PREFIX+"*";
    }

}
