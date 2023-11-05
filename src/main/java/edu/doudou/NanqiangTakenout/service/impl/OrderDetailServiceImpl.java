package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.OrderDetail;
import edu.doudou.NanqiangTakenout.mapper.OrderDetailMapper;
import edu.doudou.NanqiangTakenout.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
