package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.Employee;
import edu.doudou.NanqiangTakenout.common.CustomException;
import edu.doudou.NanqiangTakenout.mapper.EmployeeMapper;
import edu.doudou.NanqiangTakenout.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    private final String INIT_PASSWORD = "123456";

    /**
     *
     * @param employee 不为空
     * @param request
     * 如果出现没有对应用户/密码错误/员工状态异常则抛出业务异常
     * @return employee 登录的员工信息
     */
    @Override
    public Employee login(Employee employee, HttpServletRequest request) {

        log.info("正在去数据库中查询");

        //查找对应employee
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = this.getOne(queryWrapper);

        //有无对应用户
        if(emp==null){
            log.info("未找到对应用户");
            throw new CustomException("没有对应用户");
        }

        log.info("正在比对密码");
        //对比密码
        String password = passwordEncode(employee.getPassword());
        if(!password.equals(emp.getPassword())){
            log.info("密码错误");
            throw new CustomException("密码错误");
        }

        log.info("查询员工状态");
        //查看员工状态
        if(emp.getStatus()==0){
            log.info("员工状态异常");
            throw new CustomException("账号状态异常");
        }

        log.info("登录用户没有问题,正在写入session");
        request.getSession().setAttribute("employee",employee.getId());
        return emp;
    }

    /**
     * 注册新员工
     * @param employee
     * @param request
     * @return
     */
    @Override
    public String toSave(Employee employee, HttpServletRequest request) {
        log.info("正在设置员工信息");
        employee.setPassword(passwordEncode(INIT_PASSWORD));

        log.info("即将进入数据库中添加员工信息");
        this.save(employee);

        return "添加员工成功";

    }

    /**
     * 根据页面序号,页面大小,以及筛选条件name去分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page toPageWhereName(int page, int pageSize, String name) {
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件,第一个参数是指name不为空时才使用like
        queryWrapper.like(!(name==null||name.isEmpty()),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        this.page(pageInfo,queryWrapper);
        return pageInfo;
    }

    /**
     * 更新员工信息
     * @param request
     * @param employee
     * @return
     */
    @Override
    public String toUpdate(HttpServletRequest request, Employee employee) {
        Long empId = (Long) request.getSession().getAttribute("employee");
        this.updateById(employee);
        return "用户更新成功";
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return 查找到的员工信息.
     * @throws CustomException 找不到员工时抛出
     */
    @Override
    public Employee toGetById(Long id) {
        Employee employee = this.getById(id);
        if(employee==null){
            throw  new CustomException("没有找到对应用户");
        }else{
            return employee;
        }
    }

    /**
     *使用md5进行密码加密,
     * @param: password,明文密码
     * @return: 加密后的密码
     */
    String passwordEncode(String password){
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

}
