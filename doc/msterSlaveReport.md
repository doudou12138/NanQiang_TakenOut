# 背景(解决问题):
- 项目中的数据读操作和写操作都在同一个数据库中进行,我们希望为这个数据库分担压力.
- 如果这个数据库完了,则所有数据都完了,我们希望留有"备份".

# 简介:
mysql提供了主从数据库的功能:  
程序在主数据库中写数据,在从数据库中读数据.如此既使单个数据库的压力得到分摊,又让一个数据库挂了的时候又有其他数据库顶上.
同时主库写操作锁表锁行时,从库上的读操作也不会受影响.

# mysql主从复制
>新的问题:主数据库和从数据库的数据同步.  

mysql提供了主从复制的功能,无需第三方工具.其复制过程是基于mysql自带的二进制日志功能.
复制过程可以分为三步:  
1. master将改变记录到二进制文件
2. slave将master的binary log拷贝到它的中继日志
3. slave重做中继日志中的文件.

![mysql主从复制](https://github.com/doudou12138/img/blob/3524448454d0c2ae3d473c9485fd6fed8bafe73b/projectStudy/db/mysql_master_slave.PNG?raw=true)

## mysql主从复制配置
下面具体命令均已ubuntu20,mysql8为背景:
1. 准备多个mysql服务器.
2. 在mysql的配置文件中加入如下配置: (主从都做)  
   配置文件默认应该是位于/etc/mysql/mysql.conf.d/mysqld.cnf.  
   ```
   log-bin=mysql-bin  # 启用二进制日志  
   server-id=100   # 服务id,可以自己写,只要参与主从结构的各个mysql的server-id不等即可  
   ```
3. 重启mysql服务  (主从都做)  
   systemctl restart mysql  
   1. 创建用户并设置权限(其实就是为从库提供身份来拿binary log) (主库做)  
      1. 进入mysqld服务  
      2. 输入以下命令:  
         创建用户,用户名为username,密码为password  
         为该用户授予"从库的权限",复制权限  
      ```
         CREATE USER '${user_name}'@'%' IDENTIFIED WITH 'mysql_native_password' BY '${password}';
         GRANT REPLICATION SLAVE ON *.* TO '${user_name}'@'%';
      ```
      
4. 拿到日志文件位置  (主库做)  
   输入命令`show master status`  
   就可以得到下一步的log_path和log_pos  
5. 与主库建立连接 (从库做)  
   ```
   Change master to master_host='${ip地址}',
   master_port=${db_port},
   master_user='${username}',
   master_password='${password}',
   master_log_file='${log_path}',
   master_log_pos=${pos};
   ```
   
在Navicate中连接远程数据库
1. 先配置连接远程服务器
2. 配置连接从远程服务器到数据库

验证发现,这两个没有同步起来(应该是哪里输错了),如何重设从库.  
一些相关的验证指令:  
/var/log/mysql中会有一些mysql的日志:  
在我的error.log中就有着这样的记录:  
`Error connecting to source 'slave@{-------}:3306'. This was attempt 10/86400, with a delay of 60 seconds between attempts. Message: Can't connect to MySQL server on '{------}:3306`  
意为连接(主库)失败.   
```
show variables like 'log_bin';  
# 查看log_bin是否已经打开  
select user,host from mysql.user;  
# 查看用户和它的主机
show grants for '${username}'@'${host}'
# 查看对于某用户的授权
ping ${maste_ip}
# 查看主从数据库所在服务器之间的网络状况
telnet ${master_ip} ${mysql_port}
# 查看能否访问主数据库的端口号
```
这里报错了,检查阿里云服务器安全组,没毛病.防火墙没开.  
okfine.mysql只监听本地的.没有去修改mysql的配置文件.    
![](https://github.com/doudou12138/img/blob/3524448454d0c2ae3d473c9485fd6fed8bafe73b/projectStudy/db/mysql_listen_host.png?raw=true)

## 重设从库
### 重设从库的场景:
1. 修复数据一致性问题
2. 清除从库中冗余数据
3. 重新配置从库以适应新的主库结构.
   过程:
   1. `stop slave`:暂停从主库到从库的复制
   2. `reset slave`:重制从库设置并清除从库中的数据
   3. 在已有数据的情况下,这时候可能还需要将主库中数据先迁移进从库
   4. (可选)从库重新配置
   5. (主从数据库还没连接上)重设同步数据库
   6. `start slave`:开启从库

成功咯.

## 为南墙外卖添加读写分离:
### Sharding-JDBC介绍:
在java的jdbc上提供额外服务.可以轻松地实现数据库读写分离,完全兼容JDBC和各种ORM框架.
maven依赖  
```xml
<dependency>
<groupId>org.apache.shardingsphere</groupId>
<artifactId>sharding-jdbc-spring-boot-starter</artifactId>
<version>4.0.0-RC1</version>
</dependency>
```
调整配置文件:  
```yml
spring:
   shardingsphere:
      datasource:
         names:
            master, slave
         # 主数据源
         master:
            type: com.alibaba.druid.pool.DruidDataSource
            driver-class-name: com.mysql.cj.jdbc.Driver
            url: jdbc:mysql://${master_ip}:3306/nanqiang_takenout?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true # 你的数据库地址
            username: ${username}  # 你的数据库用户名
            password: ${pwd}  # 你的数据库密码
         # 从数据源
         slave:
            type: com.alibaba.druid.pool.DruidDataSource
            driver-class-name: com.mysql.cj.jdbc.Driver
            url: jdbc:mysql://${slave_ip}:3306/nanqiang_takenout?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allow
            username: ${username}  # 你的数据库用户名
            password: ${pwd}  # 你的数据库密码
      masterslave:
      # 读写分离配置
         load-balance-algorithm-type: round_robin # 轮询
         name: dataSource
         master-data-source-name: master
         slave-data-source-names: slave
   main:
      # 因为我们涉及到多个数据库,所以会有多个数据源的bean
      allow-bean-definition-overriding: true  # 允许声明多个bean
```
运行,失败..连接有问题..是远程连接的问题.  
### 数据库远程连接 
okfine.刚刚我们将监听3306端口调整为不仅仅是本地可以访问,其他ip也能访问.但仅仅是将3306端口开放了.  
但是对于数据库的访问,也并没有完全打开.默认的root用户连接数据库,这样的连接请求只能来自localhost.可以使用命令:  
```sql
use mysql;
select host,user from user;
```
会得到这样的输出:  
![](https://github.com/doudou12138/img/blob/3524448454d0c2ae3d473c9485fd6fed8bafe73b/projectStudy/db/mysql_user_host.png?raw=true)
> 可以看到root对应的host是localhost,即只能在本地登录连接.  
slave的host是%,即使用slave用户的登录连接可以来自任意ip.  
这也是为什么我们在别的机器上无法用root用户登录该数据库的原因,因此我们需要调整用户的host.

鉴于数据库中有多个database,我们应该将用户的权限加以控制,不应该直接将root的host改为%.
1. 新建用户:
   ```sql
   CREATE USER 'taken_admin'@'%' identified by '${password}'
   ```
2. 为用户授予权限:
   ```sql
   GRANT ${privileges} ON ${database}.${table} to '${username}'@'${host}';
   ```
   好的.现在你就可以远程以taken_admin用户连接数据库进行操作啦~

啊.又报错了.最后发现是yml文件的层级弄错了,masterslave是在shardingsphere下一级,与datasource同级.我写成了masterslave在datasource下(好像突然发现了.properties的优点)
调整结束,成功啦.
