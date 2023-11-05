package edu.doudou.NanqiangTakenout.service.impl;

import edu.doudou.NanqiangTakenout.service.UserCacheService;
import edu.doudou.NanqiangTakenout.utils.redis.RedisConstants;
import edu.doudou.NanqiangTakenout.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tianyou Wang
 */
@Service
public class UserCacheServiceImpl implements UserCacheService {

    /**
     */
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取请求次数
     * @return int
     */
    @Override
    public int getBindRequestCount(String phone) {
        String bindRequestCountKey = getBindRequestCountKey(phone);
        Object count = redisUtil.get(bindRequestCountKey);
        if (count != null) {
            return Integer.parseInt(count.toString());
        } else {
            return 0;
        }
    }

    /**
     * 缓存请求次数
     * @version 1.1
     * 效果稍高于1.0版本,从第一次get到第三次set完成可以一直请求验证.
     */
    @Override
    public void setBindRequestCount(String phone){

        String bindRequestCountKey = getBindRequestCountKey(phone);
        // 将请求次数缓存到 Redis，设置过期时间

        redisUtil.increment(bindRequestCountKey,1);
    }

    /**
     * 获取验证码
     * @param bindInfo
     * @return String 存在即验证码,否则null
     *
     * 等待增强,线程安全,防止多个线程都拿到同一验证码了
     *
     */
    @Override
    public String getBindConfirmCode(String bindInfo) {
        String bindConfirmCodeKey = getBindConfirmCodeKey(bindInfo);
        // 从 Redis 中获取验证码
        String confirmCode = (String) redisUtil.get(bindConfirmCodeKey);

        if (confirmCode != null) {
            // 验证码存在，删除缓存中的验证码
            redisUtil.delete(bindConfirmCodeKey);
        }

        return confirmCode;
    }

    /**
     * 缓存验证码
     * @param bindInfo
     * @version: 1.0
     */
    @Override
    public void setBindConfirmCode(String bindInfo,String code) {
        String bindConfirmCodeKey = getBindConfirmCodeKey(bindInfo);
        // 将验证码缓存到 Redis，设置过期时间
        redisUtil.delete(bindConfirmCodeKey);//当delete方法对应的key不存在,也会静默返回,不会出错.
        redisUtil.set(bindConfirmCodeKey, code, CONFIRM_CODE_CACHE_SECONDS);
    }



    public String getBindConfirmCodeKey(String bindInfo) {
        return RedisConstants.LOGIN_CODE+bindInfo;
    }


    /**
     * @return 请求次数存储的key
     * 生成key:
     * 用户登录: username+timeStamp
     * 用户未登录: ip+timestamp
     **/
    public String getBindRequestCountKey(String phone) {

        // 获取当前小时的时间戳
        long currentHour = System.currentTimeMillis() / (60 * 60 * 1000);

        return RedisConstants.LOGIN_REQUEST +currentHour+phone+currentHour;

    }



}
