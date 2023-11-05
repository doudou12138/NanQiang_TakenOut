# 功能说明
本项目名为南墙外卖.其主要分为员工端和用户端两个部分.两个部分的功能分别有:  
1. 员工端:
    - 员工登录
    - 员工添加,禁用,删除,信息修改,搜索
    - 菜品添加,修改,删除,显示,搜索
    - 套餐添加,修改,删除,显示,搜索
    - 分类管理
    - 查看订单
2. 用户端:
   - 用户登录(短信登录)
   - 用户点单,菜品查看
   - 购物车
   - 下单 
   
技术栈:
SpringBoot + mybatispls + mysql + sms 


# 运行说明
本项目后端采用springboot,mybatis-plus等技术,使用mysql数据库.使用maven构建项目

1. 使用maven加入依赖
2. 准备好mysql数据库,redis数据库
3. 在配置文件中修改填上你的数据库账号/密码(如果设置了的话),如果非本机数据库还要填上对应的url  
至此你已经可以有效的运行起项目了.  
4. 文件存储位置修改: 你可以在配置文件中修改nanqiang.path,已调整文件存放位置  
> 上传的图片默认会在D:/image/ 中
> 你的用户登录所需验证码会显示在终端中


第三方功能开启:
1. 阿里云短信服务:
在用户登录时,采用短信验证码,如果你希望可以成功发送验证码.你需要首先开通你的阿里云短信服务,并且在AliyunSmsUtil中填上你自己的
- AccessKeyId
- AccessKeySecret
- 签名sighName
- 模版号TemplateCode
- ENDPOINT(这个大陆使用者都无需修改)