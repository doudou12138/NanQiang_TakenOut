package edu.doudou.NanqiangTakenout.controller;

import edu.doudou.NanqiangTakenout.Entity.ShoppingCart;
import edu.doudou.NanqiangTakenout.common.Res;
import edu.doudou.NanqiangTakenout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Res<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("正在请求添加购物车....");

        return Res.success(shoppingCartService.add(shoppingCart));
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public Res<List<ShoppingCart>> list() {
        log.info("查看购物车...");

        return Res.success(shoppingCartService.tolist());
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public Res<String> clean() {
        //SQL:delete from shopping_cart where user_id = ?
        log.info("正在请求清空购物车....");
        shoppingCartService.clear();
        return Res.success("清空购物车成功");
    }

}