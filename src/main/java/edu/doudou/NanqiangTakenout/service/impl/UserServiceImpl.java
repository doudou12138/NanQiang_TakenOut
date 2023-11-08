package edu.doudou.NanqiangTakenout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.doudou.NanqiangTakenout.Entity.User;
import edu.doudou.NanqiangTakenout.mapper.UserMapper;
import edu.doudou.NanqiangTakenout.service.UserCacheService;
import edu.doudou.NanqiangTakenout.service.UserService;
import edu.doudou.NanqiangTakenout.utils.ValidatorCodeUtil;
import edu.doudou.NanqiangTakenout.utils.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private static final int MAX_SMS_REQUEST_PER_HOUR = 3;

    @Autowired
    private UserCacheService userCacheService;


    @Override
    public String sendVerificationMsg(User user, HttpSession session) {

        String phoneNumber = user.getPhone();
        if(phoneNumber==null){
            return "手机号为空.";
        }

        long count = belowLimitSmsRequest(phoneNumber);
        if(count==-1){
            return "请求次数过多，请下一个小时再尝试";
        }
        userCacheService.setBindRequestCount(phoneNumber,count);


        String veriCode = ValidatorCodeUtil.generateValidateCode(6).toString();
        //通过阿里云发送验证码.测试已经成功
//        AliyunSmsUtil.sendMessage(MediaMsgType.LOG_IN,phoneNumber,veriCode);

        log.info("该用户的验证码: " +phoneNumber + ":" +veriCode);
        //将验证码保存到Session
        userCacheService.setBindConfirmCode(phoneNumber,veriCode);

        return "成功发送验证码";
    }

    private int belowLimitSmsRequest(String phone) {
        int count = userCacheService.getBindRequestCount(phone);
        return count<MAX_SMS_REQUEST_PER_HOUR?count:-1;
    }

    @Override
    public User login(Map map, HttpSession session) {
        User user = null;

        //获取手机号/验证码
        String phoneNumber = (String) map.get("phone");
        String code = (String) map.get("code");

        String codeInRedis = userCacheService.getBindConfirmCode(phoneNumber);

        if(code!=null&&code.equals(codeInRedis)){
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
            System.err.println("验证码 : "+codeInRedis );
        }

        return user;
    }

}
