package edu.doudou.NanqiangTakenout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.doudou.NanqiangTakenout.Entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService extends IService<Category> {

    Page toPage(int page, int pageSize);

    String remove(Long id);

    List<Category> toListByType(Category category);

    Map<Long,String> idAndNames(List<Long> ids);

}
