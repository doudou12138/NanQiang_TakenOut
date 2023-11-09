package edu.doudou.NanqiangTakenout.controller;

import edu.doudou.NanqiangTakenout.Entity.User;
import edu.doudou.NanqiangTakenout.common.Res;
import edu.doudou.NanqiangTakenout.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public Res<String> sendVerificationMsg(@RequestBody User user, HttpSession session){
        log.info("正在请求发送短信验证码....");
        String str = userService.sendVerificationMsg(user,session);
        if(str.charAt(0)=='0'){
            return Res.error(str.substring(1));
        }
        return Res.success("发送验证码成功");
    }

    @PostMapping("/login")
    public Res<User> login(@RequestBody Map map, HttpSession session){
        log.info("正在请求登录....");


        User user = userService.login(map,session);
        if(user==null){
            return Res.error("登录失败");
        }
        return Res.success(user);
    }

}
