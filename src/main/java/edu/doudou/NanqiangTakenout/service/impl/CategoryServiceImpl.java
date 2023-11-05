package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.Category;
import edu.doudou.NanqiangTakenout.Entity.Dish;
import edu.doudou.NanqiangTakenout.Entity.Setmeal;
import edu.doudou.NanqiangTakenout.common.CustomException;
import edu.doudou.NanqiangTakenout.mapper.CategoryMapper;
import edu.doudou.NanqiangTakenout.service.CategoryService;
import edu.doudou.NanqiangTakenout.service.DishService;
import edu.doudou.NanqiangTakenout.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetMealService setMealService;

    @Override
    public Page toPage(int page, int pageSize) {
        Page<Category> pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByAsc(Category::getSort);

        this.page(pageInfo,queryWrapper);

        return pageInfo;
    }

    /**
     * 删除之前先需判断当前分类是否关联菜品/套餐
     * @param id
     * @return
     */
    @Override
    public String remove(Long id) {
        //构造dish的查询构造器
        LambdaQueryWrapper<Dish> dishQueryWrapper= new LambdaQueryWrapper<Dish>();
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishQueryWrapper);

        //
        if(count1>0){
            throw new CustomException("当前分类项已关联菜品,不能删除");
        }

        LambdaQueryWrapper<Setmeal> setMealQueryWrapper= new LambdaQueryWrapper<Setmeal>();
        setMealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setMealService.count(setMealQueryWrapper);

        if(count2>0){
            throw new CustomException("当前分类项已关联套餐,不能删除");
        }

        this.removeById(id);
        return null;
    }

    @Override
    public List<Category> toListByType(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());

        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = this.list(queryWrapper);

        return list;
    }

}
