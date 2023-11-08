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

    /**
     * 员工登录请求
     * @param httpServletRequest
     * @param employee
     * 1. 判断是否为空,
     * 2. 登录
     * @return
     */
    @PostMapping("/login")
    public Res<Employee> login(
            HttpServletRequest httpServletRequest,
            @RequestBody Employee employee){
        log.info("正在请求尝试登录 :");
        if(employee==null){
            return Res.error("账号密码为空");
        }
        return Res.success(employeeService.login(employee,httpServletRequest));
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

    /**
     * 员工注册请求
     * @param httpServletRequest
     * @param employee
     * 1. 判断是否为空,
     * 2. 注册
     * @return
     */
    @PostMapping
    public Res<String> save(HttpServletRequest httpServletRequest,@RequestBody Employee employee){
        log.info("正在请求添加员工");
        if(employee==null){
            return Res.error("员工信息为空");
        }
        return Res.success(employeeService.toSave(employee,httpServletRequest));

    }

    /**
     * 员工分页展示请求
     * @param page 当前页面
     * @param pageSize  页面大小
     * @param name  员工名字(可选地筛选条件)
     * @return
     */
    @GetMapping("/page")
    public Res<Page> page(int page,int pageSize,String name){
        log.info("正在请求分页展示员工...");

        Page resPage = employeeService.toPageWhereName(page,pageSize,name);
        return Res.success(resPage);
    }

    /**
     * 员工更新请求
     * @param request
     * @param employee
     * 1. 判断是否为空,
     * 2. 更新
     * @return
     */
    @PutMapping
    public Res<String> update(
            HttpServletRequest request,
            @RequestBody Employee employee){
        log.info("正在请求更新员工信息");
        if(employee==null){
            return Res.error("员工信息为空");
        }
        return Res.success(employeeService.toUpdate(request,employee));
    }

    /**
     * 员工根据id请求
     * @param id 员工id
     * @return 员工信息
     */
    @GetMapping("/{id}")
    public Res<Employee> getById(@PathVariable Long id){
        log.info("正在根据员工id请求员工数据");
        return Res.success(employeeService.toGetById(id));
    }

}
