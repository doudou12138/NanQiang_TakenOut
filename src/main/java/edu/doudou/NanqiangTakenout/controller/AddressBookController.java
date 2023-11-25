package edu.doudou.NanqiangTakenout.controller;

import edu.doudou.NanqiangTakenout.Entity.AddressBook;
import edu.doudou.NanqiangTakenout.common.BaseContext;
import edu.doudou.NanqiangTakenout.common.Res;
import edu.doudou.NanqiangTakenout.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    public Res<AddressBook> save(@RequestBody AddressBook addressBook){
        log.info("正在请求添加地址...");
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return Res.success(addressBook);
    }

    @PutMapping
    public Res<AddressBook> update(@RequestBody AddressBook addressBook){
        log.info("正在请求更新地址....");
        boolean success = addressBookService.update(addressBook);
        if(!success){
            return Res.error("更新失败,请稍后再试");
        }
        return Res.success(addressBook);
    }

    @PutMapping("/default")
    public Res<AddressBook> setDefault (@RequestBody AddressBook addressBook){
        log.info("正在请求设置默认地址....");
        addressBook.setUserId(BaseContext.getCurrentId());
        AddressBook defaultAddressBook = addressBookService.setDefault(addressBook);
        return Res.success(defaultAddressBook);
    }

    /**
     * 查询默认地址并缓存
     */
    @GetMapping("/default")
    public Res<AddressBook> getDefault(){
        log.info("正在请求查询默认地址....");

        AddressBook addressBook = addressBookService.getDefault(BaseContext.getCurrentId());
        if(addressBook==null){
            return Res.error("没有默认地址");
        }
        return Res.success(addressBook);
    }

    @GetMapping("/list")
    public Res<List<AddressBook>> list(AddressBook addressBook){
        log.info("正在展示地址簿列表");
        return Res.success(addressBookService.toList(addressBook));
    }

    @GetMapping("/{id}")
    public Res<AddressBook> get(@PathVariable("id") Long id){
        log.info("正在根据地址id查询地址信息");
        AddressBook addressBook = addressBookService.getById(id);
        return Res.success(addressBook);
    }

}
