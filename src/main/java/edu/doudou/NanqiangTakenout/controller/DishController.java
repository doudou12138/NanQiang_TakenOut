package edu.doudou.NanqiangTakenout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.doudou.NanqiangTakenout.Entity.Dish;
import edu.doudou.NanqiangTakenout.common.Res;
import edu.doudou.NanqiangTakenout.dto.DishDto;
import edu.doudou.NanqiangTakenout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping
    public Res<String> save(@RequestBody DishDto dishDto){
        log.info("正在请求添加菜品....");

        dishService.saveWithFlavor(dishDto);

        return Res.success("添加菜品成功");
    }

    @GetMapping("/page")
    public Res<Page> page(int page, int pageSize, String name){
        log.info("正在请求分页查询菜品");

        return Res.success(dishService.toPage(page,pageSize,name));
    }

    @GetMapping("/{id}")
    public Res<DishDto> get(@PathVariable Long id){
        log.info("正在根据dishId请求查询菜品....");
        return Res.success(dishService.getByIdWithFlavor(id));
    }

    @PutMapping
    public Res<String> update(@RequestBody DishDto dishDto){
        log.info("正在请求修改菜品....");

        dishService.updateWithFlavor(dishDto);

        return Res.success("修改菜品成功");
    }


    /**
     * 根据条件来查询菜品列表
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Res<List<DishDto>> list(Dish dish){
        log.info("正在请求获取彩屏列表");

        return Res.success(dishService.tolist(dish));
    }

}
