package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.DishFlavor;
import edu.doudou.NanqiangTakenout.mapper.DishFlavorMapper;
import edu.doudou.NanqiangTakenout.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {
}
