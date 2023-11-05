package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.User;
import edu.doudou.NanqiangTakenout.mapper.UserMapper;
import edu.doudou.NanqiangTakenout.service.UserService;
import edu.doudou.NanqiangTakenout.utils.ValidatorCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    //


    @Override
    public String sendVerificationMsg(User user, HttpSession session) {

        String phoneNumber = user.getPhone();
        if(phoneNumber==null){
            return "手机号为空.";
        }
        String veriCode = ValidatorCodeUtil.generateValidateCode(6).toString();

        //通过阿里云发送验证码.测试已经成功
//        AliyunSmsUtil.sendMessage(MediaMsgType.LOG_IN,phoneNumber,veriCode);

        log.info("该用户的验证码: " +phoneNumber + ":" +veriCode);
        //将验证码保存到Session
        session.setAttribute(phoneNumber,veriCode);

        return "成功发送验证码";
    }

    @Override
    public User login(Map map, HttpSession session) {
        User user = null;

        //获取手机号/验证码
        String phoneNumber = (String) map.get("phone");
        String code = (String) map.get("code");

        String codeInSession = (String) session.getAttribute(phoneNumber);

        if(code!=null&&code.equals(codeInSession)){
            //如果是新用户,直接注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phoneNumber);
            user = this.getOne(queryWrapper);
            if(user==null){
                user = new User();
                user.setPhone(phoneNumber);
                user.setStatus(1);
                this.save(user);
            }

            session.setAttribute("user",user.getId());
        }else{
            System.err.println("验证码 : "+codeInSession );
        }

        return user;
    }

}
