package edu.doudou.NanqiangTakenout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.doudou.NanqiangTakenout.Entity.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
