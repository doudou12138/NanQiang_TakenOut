package edu.doudou.NanqiangTakenout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.doudou.NanqiangTakenout.Entity.AddressBook;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    AddressBook setDefault(AddressBook addressBook);

    AddressBook getDefault(Long userId);

    List<AddressBook> toList(AddressBook addressBook);
}
