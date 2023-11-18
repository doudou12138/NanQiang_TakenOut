package edu.doudou.NanqiangTakenout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.doudou.NanqiangTakenout.Entity.Setmeal;
import edu.doudou.NanqiangTakenout.common.Res;
import edu.doudou.NanqiangTakenout.dto.SetMealDto;
import edu.doudou.NanqiangTakenout.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

    @PostMapping
    public Res<String> save(@RequestBody SetMealDto setMealDto){
        log.info("正在请求添加套餐....");
        setMealService.saveWithDishes(setMealDto);
        return Res.success("添加套餐成功");

    }


    @GetMapping("/page")
    public Res<Page> page(int page,int pageSize,String name){
        log.info("正在请求分页显示套餐....");
        return Res.success(setMealService.toPage(page,pageSize,name));
    }

    @DeleteMapping
    public Res<String> delete(@RequestBody List<Long> ids){
        log.info("正在请求删除套餐....");
        setMealService.removeWithDishes(ids);
        return Res.success("删除套餐成功");
    }

    @GetMapping("/list")
    public Res<List<Setmeal>> list(Setmeal setMeal){
        log.info("正在请求查询套餐列表....");
        return Res.success(setMealService.toList(setMeal));

    }

}
