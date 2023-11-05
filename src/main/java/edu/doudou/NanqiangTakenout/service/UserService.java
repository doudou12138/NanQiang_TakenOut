package edu.doudou.NanqiangTakenout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.doudou.NanqiangTakenout.Entity.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService extends IService<User> {
    String sendVerificationMsg(User user, HttpSession session);

    User login(Map map, HttpSession session);
}
