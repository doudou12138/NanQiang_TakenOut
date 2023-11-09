package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.Setmeal;
import edu.doudou.NanqiangTakenout.Entity.SetmealDish;
import edu.doudou.NanqiangTakenout.common.CustomException;
import edu.doudou.NanqiangTakenout.dto.SetMealDto;
import edu.doudou.NanqiangTakenout.mapper.SetMealMapper;
import edu.doudou.NanqiangTakenout.service.CategoryService;
import edu.doudou.NanqiangTakenout.service.SetMealDishService;
import edu.doudou.NanqiangTakenout.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加套餐.
     * 1. 修改套餐基本信息表
     * 2. 修改套餐菜品关系表
     * @param setMealDto
     */
    @Caching(evict = {
            @CacheEvict(value = "setmealListCache",key = "#setMealDto.categoryId+'_'+#setMealDto.status"),
            @CacheEvict(value = "setmealPageCache",allEntries = true)
    })
    @Transactional
    @Override
    public void saveWithDishes(SetMealDto setMealDto) {
        //保存套餐基本信息
        this.save(setMealDto);

        //修改套餐菜品关系表
        addSetMealDishesWithAdjust(setMealDto);
    }

    /**
     *
     * 分表查询套餐的信息.(可选地筛选条件name)
     * @param page
     * @param pageSize
     * @param name
     * @return
     * todo:将查询从流中移除
     */
    @Cacheable(value = "setmealPageCache",key = "#page+'_'+#pageSize+'_'+#name")
    @Override
    public Page tolist(int page, int pageSize, String name) {
        Page<SetMealDto> result = new Page<>(page,pageSize);
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);

        //查询构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null, Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //分页查询
        this.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,result,"records");

        //等你优化
        result.setRecords(pageInfo.getRecords().stream().map(setmeal -> {
            SetMealDto setMealDto = new SetMealDto();
            BeanUtils.copyProperties(setmeal,setMealDto);
            setMealDto.setCategoryName(categoryService.getById(setmeal.getCategoryId()).getName());
            return setMealDto;
        }).collect(Collectors.toList()));

        //结果在pageInfo中.
        return result;
    }

    /**
     * 删除套餐,及其所带的菜品关系
     * @param ids
     * //todo:事务优化
     */
    @Caching(evict = {
            @CacheEvict(value = "setmealPageCache",allEntries = true),
            @CacheEvict(value = "setmealListCache",allEntries = true)
    })
    @Override
    public void removeWithDishes(List<Long> ids) {
        //查询套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖中,不能删除!");
        }

        //删除套餐菜品关系
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.execute((i)->{
            //删除套餐本身信息
            this.removeByIds(ids);

            //删除对应的套餐菜品关系
            LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.in(SetmealDish::getSetmealId,ids);
            setMealDishService.remove(queryWrapper1);
            return true;
        });


    }

    /**
     * 根据套餐id查询套餐信息.并且对查询结果进行缓存
     * @param setMeal
     * @return
     */
    @Cacheable(value = "setmealListCache",key = "#setMeal.categoryId+'_'+#setMeal.status")
    @Override
    public List<Setmeal> tolist(Setmeal setMeal) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<Setmeal>();
        queryWrapper.eq(setMeal.getCategoryId()!=null, Setmeal::getCategoryId,setMeal.getCategoryId());
        queryWrapper.eq(setMeal.getStatus()!=null, Setmeal::getStatus,setMeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = this.list(queryWrapper);
        return list;

    }

    /**
     * 根据setMealDto在 套餐菜品关系数据库中添加记录
     * @param setMealDto
     */
    private void addSetMealDishesWithAdjust(SetMealDto setMealDto){
        Long setMealId = setMealDto.getId();
        List<SetmealDish> dishes = setMealDto.getSetMealDishList();
        if(dishes==null){
            return;
        }
        List<SetmealDish> setMealDishList= dishes.stream().map((item) -> {
            item.setId(setMealId);
            return item;
        }).collect(Collectors.toList());

        //批量添加
        setMealDishService.saveBatch(setMealDishList);
    }

}
