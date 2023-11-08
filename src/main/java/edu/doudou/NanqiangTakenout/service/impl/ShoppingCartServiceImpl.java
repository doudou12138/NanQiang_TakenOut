package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.ShoppingCart;
import edu.doudou.NanqiangTakenout.common.BaseContext;
import edu.doudou.NanqiangTakenout.mapper.ShoppingCartMapper;
import edu.doudou.NanqiangTakenout.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper,ShoppingCart> implements ShoppingCartService {

    @Override
    public ShoppingCart add(ShoppingCart shoppingCart){
        ShoppingCart cartServiceOne = getDishOrSetmeal(shoppingCart);

        if(cartServiceOne != null){
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            this.updateById(cartServiceOne);
        }else{
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return cartServiceOne;

    }

    /**
     * 1.判断购物车中是否已经存在该菜品或者套餐
     * 2.如果存在，返回该菜品或者套餐购物车
     * 3.如果不存在，返回null
     * @param shoppingCart
     * @return
     */
    private ShoppingCart getDishOrSetmeal(ShoppingCart shoppingCart) {
        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);

        }else{
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //查询当前菜品或者套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartServiceOne = this.getOne(queryWrapper);
        return cartServiceOne;
    }

    @Override
    public List<ShoppingCart> tolist() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public void clear() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        this.remove(queryWrapper);
    }

    /**
     * 1.判断购物车中是否已经存在该菜品或者套餐
     * 2.如果存在，就在原来数量基础上减一
     * 3.如果数量减到一，则删除该购物车数据
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart sub(ShoppingCart shoppingCart) {
        ShoppingCart cartServiceOne = getDishOrSetmeal(shoppingCart);

        if(cartServiceOne!= null){
            //如果存在，就在原来数量基础上减一
            Integer number = cartServiceOne.getNumber();
            if(number > 1){
                cartServiceOne.setNumber(number - 1);
                this.updateById(cartServiceOne);
            }else{
                //如果数量减到一，则删除该购物车数据
                cartServiceOne = null;
                this.removeById(cartServiceOne.getId());
            }
        }
        return cartServiceOne;
    }

}
