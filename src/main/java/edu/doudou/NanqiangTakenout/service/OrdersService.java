package edu.doudou.NanqiangTakenout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.doudou.NanqiangTakenout.Entity.Orders;
import edu.doudou.NanqiangTakenout.common.Res;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);

    Page<Orders> userPage(int page, int pageSize, Long currentId);

    Page<Orders> employeePage(int page, int pageSize,Long employeeId);
}
