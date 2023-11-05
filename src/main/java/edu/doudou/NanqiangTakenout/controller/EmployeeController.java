package edu.doudou.NanqiangTakenout.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.doudou.NanqiangTakenout.Entity.Employee;
import edu.doudou.NanqiangTakenout.common.Res;
import edu.doudou.NanqiangTakenout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public Res<Employee> login(
            HttpServletRequest httpServletRequest,
            @RequestBody Employee employee){
        log.info("尝试登录 with username:"+employee.getUsername());
        return employeeService.login(employee,httpServletRequest);
    }

    /**
     * 员工退出请求
     * 清除session中的员工id
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Res<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return Res.success("成功退出");
    }

    @PostMapping
    public Res<String> save(HttpServletRequest httpServletRequest,@RequestBody Employee employee){
        log.info("正在请求添加员工");
        return employeeService.toSave(employee,httpServletRequest);

    }

    @GetMapping("/page")
    public Res<Page> page(int page,int pageSize,String name){
        log.info("正在请求分页展示员工...");

        Page resPage = employeeService.toPageWhereName(page,pageSize,name);
        return Res.success(resPage);
    }

    @PutMapping
    public Res<String> update(
            HttpServletRequest request,
            @RequestBody Employee employee){
        log.info("正在请求更新员工信息");

        return employeeService.toUpdate(request,employee);
    }

    @GetMapping("/{id}")
    public Res<Employee> getById(@PathVariable Long id){
        log.info("正在根据员工id请求员工数据");
        return employeeService.toGetById(id);
    }

}
