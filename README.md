# spring_clould分布式事务框架-v0.1
## 项目介绍
基于spring cloud 研发的分布式事务架构，用来解决多数据库下数据不一致的问题。

注意:springboot 1.x已不再支持,公司内部的框架还保留在gitlab中,即将升级成springboot.2.7.10,无论怎么升级springboot与springcloud都避免不了CVE漏洞王的称号.

别问为什么直接看证据:
```
Spring Boot Starter Parent » 2.7.5
Parent pom providing dependency and plugin management for applications built with Maven

License	Apache 2.0
Tags	springstarter
HomePage	https://spring.io/projects/spring-boot
Date	Oct 20, 2022
Files	pom (8 KB)  View All
Repositories	Central
Ranking	#11622 in MvnRepository (See Top Artifacts)
Used By	31 artifacts
Vulnerabilities	Vulnerabilities from dependencies:
CVE-2023-28867
CVE-2023-25194
CVE-2023-1370
CVE-2022-45868
CVE-2022-4492
CVE-2022-41946
CVE-2022-41854
CVE-2022-41853
CVE-2022-38752
CVE-2022-38751
CVE-2022-38750
CVE-2022-38749
CVE-2022-35278
CVE-2022-25857
CVE-2022-1471
```
官方最新版3.x也是有很多漏洞,所以在线上的项目技术选型还是要谨慎。
## 项目结构
 ### 架构源码
 dawdler-distributed-trasaction 分布式事务核心架构源码  
 dawdler-distributed-compensator 补偿处理架构源码  
 dawdler-distributed-transaction-api 分布式事务api

### 使用实例

 dt_demo_order 订单模块(含下单页面)  
 dt_demo_user 用户模块  
 dt_demo-product 产品模块  
 dt_demo_compensator 补偿器模块    
 eureka-server eureka服务器  
 test.sql  需要导入到mysql中的数据库脚本 会生成3个库  
 
## 使用说明
1、通过maven安装dawdler-distributed-trasaction 和 dawdler-distributed-compensator  
 2、准备activemq或artemis，也可以用其他mq  
 3、准备redis (架构中可以扩展 mysql，本地文件等其他存储方式)用于存储发起者的记录用于做极端情况下的补偿  
 4、导入test.sql到mysql  
 5、更改项目中的mysql配置及redis，mq等配置  
 6、启动  eureka-server， dt_demo-product，dt_demo_user ，dt_demo_order ，dt_demo_compensator 。(先启动eureka 其他顺序无要求)  
 7、访问dt_demo_order 的index.html 测试  
