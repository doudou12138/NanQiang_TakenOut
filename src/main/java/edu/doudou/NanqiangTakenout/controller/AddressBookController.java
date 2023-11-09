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

    @PutMapping("/default")
    public Res<AddressBook> setDefault (@RequestBody AddressBook addressBook){
        log.info("正在请求设置默认地址....");
        addressBook.setUserId(BaseContext.getCurrentId());
        return Res.success(addressBookService.setDefault(addressBook));
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
        return Res.success(addressBookService.toList(addressBook));
    }

}
