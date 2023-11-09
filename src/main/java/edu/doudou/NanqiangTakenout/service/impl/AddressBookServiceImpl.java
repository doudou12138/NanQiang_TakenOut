package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.AddressBook;
import edu.doudou.NanqiangTakenout.common.BaseContext;
import edu.doudou.NanqiangTakenout.mapper.AddressBookMapper;
import edu.doudou.NanqiangTakenout.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 设置默认地址.并调整缓存
     * @param addressBook
     * @return
     */
    @CachePut(value = "addressBook",key = "'default: '+ #addressBook.userId")
    @Override
    public AddressBook setDefault(AddressBook addressBook) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook1 = this.getOne(queryWrapper);

        if(addressBook1==null){
            addressBook.setIsDefault(1);
            this.updateById(addressBook);
        }else if(!Objects.equals(addressBook1.getId(), addressBook.getId())){
            transactionTemplate.executeWithoutResult((status)->{
                try {
                    addressBook1.setIsDefault(0);
                    this.updateById(addressBook1);
                    addressBook.setIsDefault(1);
                    this.updateById(addressBook);
                }catch (Exception e) {
                    status.setRollbackOnly();
                    throw e;
                }
            });
        }

        return addressBook;
    }

    @Cacheable(value = "addressBook", key = "'default: '+ #userId")
    @Override
    public AddressBook getDefault(Long userId) {
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
