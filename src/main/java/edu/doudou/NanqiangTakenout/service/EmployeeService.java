package edu.doudou.NanqiangTakenout.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.doudou.NanqiangTakenout.Entity.Employee;
import edu.doudou.NanqiangTakenout.common.Res;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {

    Employee login(Employee employee, HttpServletRequest httpServletRequest);
    String toSave(Employee employee,HttpServletRequest request);

    Page toPageWhereName(int page, int pageSize, String name);

    String toUpdate(HttpServletRequest request, Employee employee);

    Employee toGetById(Long id);
}
