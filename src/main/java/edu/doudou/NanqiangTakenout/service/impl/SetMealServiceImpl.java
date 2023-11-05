package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.Setmeal;
import edu.doudou.NanqiangTakenout.Entity.SetmealDish;
import edu.doudou.NanqiangTakenout.common.CustomException;
import edu.doudou.NanqiangTakenout.dto.SetMealDto;
import edu.doudou.NanqiangTakenout.mapper.SetMealMapper;
import edu.doudou.NanqiangTakenout.service.SetMealDishService;
import edu.doudou.NanqiangTakenout.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {

    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 添加套餐.
     * 1. 修改套餐基本信息表
     * 2. 修改套餐菜品关系表
     * @param setMealDto
     */
    @Transactional
    @Override
    public void saveWithDishes(SetMealDto setMealDto) {
        //保存套餐基本信息
        this.save(setMealDto);

        //修改套餐菜品关系表
        addSetMealDishesWithAdjust(setMealDto);
    }

    /**
     * todo:根据categoryId查询name
     *
     * 分表查询套餐的信息.(可选地筛选条件name)
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
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

        //结果在pageInfo中.
        return result;
    }

    /**
     * 删除套餐,及其所带的菜品关系
     * //todo: 事务可优化
     * @param ids
     */
    @Transactional
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
        //删除套餐本身信息
        this.removeByIds(ids);

        //删除对应的套餐菜品关系
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setMealDishService.remove(queryWrapper1);

    }

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
        List<SetmealDish> setMealDishList= setMealDto.getSetMealDishList().stream().map((item) -> {
            item.setId(setMealId);
            return item;
        }).collect(Collectors.toList());

        //批量添加
        setMealDishService.saveBatch(setMealDishList);
    }

}
