package edu.doudou.NanqiangTakenout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.doudou.NanqiangTakenout.Entity.Category;
import edu.doudou.NanqiangTakenout.common.Res;
import edu.doudou.NanqiangTakenout.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Res<String> sava(@RequestBody Category category){
        log.info("正在请求保存新种类...");
        categoryService.save(category);
        return Res.success("新增分类成功");
    }

    @GetMapping("/page")
    public Res<Page> page(int page, int pageSize){
        log.info("请求分页展示分类....");
        return Res.success(categoryService.toPage(page,pageSize));
    }

    @DeleteMapping
    public Res<String> delete(Long id){
        log.info("请求删除分类,id:{} ...",id);

        categoryService.remove(id);

        return Res.success("分类删除成功");
    }

    @PutMapping
    public Res<String> update(@RequestBody Category category){
        log.info("请求修改分类....");
        categoryService.updateById(category);
        return Res.success("修改分类信息成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Res<List<Category>> list(Category category){
        log.info("正在请求获取种类...");
        return Res.success(categoryService.toListByType(category));
    }



}
