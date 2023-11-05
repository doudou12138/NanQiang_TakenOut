package edu.doudou.NanqiangTakenout.controller;

import edu.doudou.NanqiangTakenout.Entity.Orders;
import edu.doudou.NanqiangTakenout.common.Res;
import edu.doudou.NanqiangTakenout.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 提交订单的请求
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Res<String> submit(@RequestBody Orders orders){
        log.info("正在请求提交订单....");
        ordersService.submit(orders);
        return Res.success("下单成功");
    }

    @GetMapping("/page")
    public Res<List<Orders>> page(){
        log.info("正在请求分页显示商家订单....");
        return null;
    }
}
