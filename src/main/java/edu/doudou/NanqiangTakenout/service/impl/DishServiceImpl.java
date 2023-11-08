package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.Category;
import edu.doudou.NanqiangTakenout.Entity.Dish;
import edu.doudou.NanqiangTakenout.Entity.DishFlavor;
import edu.doudou.NanqiangTakenout.common.CustomException;
import edu.doudou.NanqiangTakenout.dto.DishDto;
import edu.doudou.NanqiangTakenout.mapper.DishMapper;
import edu.doudou.NanqiangTakenout.service.CategoryService;
import edu.doudou.NanqiangTakenout.service.DishCacheService;
import edu.doudou.NanqiangTakenout.service.DishFlavorService;
import edu.doudou.NanqiangTakenout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>  implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishCacheService dishCacheService;

    @Autowired
    private TransactionTemplate transactionTemplate;
    /**
     * 添加菜品信息和菜品和口味的关系
     * @param dishDto
     */
    @Override
//    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        transactionTemplate.executeWithoutResult((status) -> {
            try {
                //保存菜品基本信息
                this.save(dishDto);
                //保存dish 和 口味的关系
                addDishFlavorsWithAdjust(dishDto);
                dishCacheService.deleteDishCache(dishDto);
            }catch (Exception e){
                status.setRollbackOnly();
                throw e;
            }
        });


        //更改后的菜品列表信息同步到缓存
        List<DishDto> dishDtoList = this.tolist(dishDto);
        dishCacheService.saveDishList(dishDto,dishDtoList);

    }


    /**
     * 分页查询得到菜品列表(可选的根据菜名模糊查询)
     * 并且将其中的categoryId转化为categoryName
     * @param page
     * @param pageSize
     * @param name
     * //todo: 优化查询
     * @return
     */
    @Override
    public Page toPage(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //模糊查询,(根据名字)分页查询所有的菜品
        queryWrapper.like(name!=null,Dish::getName,name);

        this.page(pageInfo,queryWrapper);

        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);

        //拷贝页的一些数据,例如页面数,页面size等,
        //Page中的List<T> records 其实就是目标对象的列表.因为我们要手动处理其中的属性,所以不copy
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //将Dish转化为DishDto
        List<Dish> records = pageInfo.getRecords();

        //将categoryId转化为categoryName
        dishDtoPage.setRecords( records.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            //将categoryId转化为categoryName
            Category category = categoryService.getById(dish.getCategoryId());
            dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList()));

        return dishDtoPage;
    }

    /**
     * 根据id来查询菜品信息,并菜品口味id改为口味name
     * @return
     * @param id
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto result = new DishDto();

        //将dish的基本属性都放到dishDto
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish,result);

        //进行查询,查询某菜品的风味s
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        //设置dishDto的风味
        result.setFlavors(flavors);
        return result;
    }

    /**
     * 更新菜品信息和菜品和口味的关系
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //清除dish的原口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        transactionTemplate.executeWithoutResult((transactionStatus)->{
            try {
                //删除缓存
                dishCacheService.deleteDishCache(dishDto);

                //保存dish基本信息
                this.updateById(dishDto);
                //保存dish 和 口味的关系
                addDishFlavorsWithAdjust(dishDto);
            }catch (Exception e){
                transactionStatus.setRollbackOnly();
                throw new CustomException("更新菜品信息失败");
            }
        });

        //添加缓存
        dishCacheService.saveDishList(dishDto,this.tolist(dishDto));

    }

    /**
     * 返回菜品列表
     * @param dish: 筛选条件,现在是根据种类查询
     * @return dishDto类型
     * todo: 优化查询
     */
    @Override
    public List<DishDto> tolist(Dish dish) {
        List<DishDto> result = new ArrayList<>();
        //1. 到缓存中查询,命中则返回
        List<DishDto> dishDtoList = dishCacheService.getDishList(dish);
        if(dishDtoList!=null){
            return dishDtoList;
        }

        //2.如果没有则到数据库中查询,并将其放入缓存中
        //2.1 查询
       List<Dish> dishes = getDishListFromDb(dish);

        //2.2 转化
        result = dishes.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //根据分类id查找分类名,根据菜品id查找口味
            Category category = categoryService.getById(item.getCategoryId());
            if(category!=null){
                dishDto.setCategoryName(category.getName());
            }

            return dishDto;
        }).collect(Collectors.toList());

        //加入缓存
        dishCacheService.saveDishList(dish,result);

        return result;
    }


    private void addDishFlavorsWithAdjust(DishDto dishDto){
        Long dishId = dishDto.getId();
        //添加dish的新口味,注意:原本传进来的数据的flavors只有口味,没有对应的dishId
        //调整数据结构
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) ->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //批量加入
        dishFlavorService.saveBatch(flavors);
    }


    /**
     * 到数据库中查询菜品列表,并根据分类id和菜品状态进行筛选
     * @param dish
     * @return
     */
    private List<Dish> getDishListFromDb(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort);
        List<Dish> dishes = this.list(queryWrapper);
        return dishes;
    }

}
