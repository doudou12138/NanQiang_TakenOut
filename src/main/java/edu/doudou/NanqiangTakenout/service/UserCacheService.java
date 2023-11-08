package edu.doudou.NanqiangTakenout.service;

public interface UserCacheService {

    //验证码的过期时间 3分钟
    int CONFIRM_CODE_CACHE_SECONDS = 3*60;

    //某小时的请求次数缓存时间
    int REQUEST_COUNT_CACHE_SECONDS = 60*60;

    /**
     * 获取 绑定请求的次数
     * @return
     */

    int getBindRequestCount(String phone);

    /**
     * 缓存绑定请求的次数
     */
    void setBindRequestCount(String phone, long count);

    /**
     * 获取 绑定请求的验证码
     * @return
     */
    String getBindConfirmCode(String bindInfo);

    /**
     * 缓存 绑定请求的验证码
     */
    void setBindConfirmCode(String bindInfo,String code);

}
