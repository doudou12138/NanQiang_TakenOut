package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.AddressBook;
import edu.doudou.NanqiangTakenout.common.BaseContext;
import edu.doudou.NanqiangTakenout.mapper.AddressBookMapper;
import edu.doudou.NanqiangTakenout.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Transactional
    @Override
    public AddressBook setDefault(AddressBook addressBook) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook1 = this.getOne(queryWrapper);

        if(!Objects.equals(addressBook1.getId(), addressBook.getId())){
            addressBook1.setIsDefault(0);
            this.save(addressBook1);
            addressBook.setIsDefault(1);
            this.save(addressBook);
            return addressBook;
        }

        return addressBook;
    }

    @Override
    public AddressBook getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);

        return this.getOne(queryWrapper);
    }

    /**
     * 根据当前登录用户,以及筛选条件得到地址列表.
     *
     * @param addressBook:筛选条件
     * @return 地址列表
     */
    @Override
    public List<AddressBook> toList(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        return this.list(queryWrapper);
    }
}
