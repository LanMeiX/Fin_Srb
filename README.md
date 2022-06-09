# 互联网金融项目--尚融宝

## 1、项目简介

尚融宝是一个网络借贷信息中介服务平台，为个人投资者、个人融资用户和小微企业提供专业的线上信贷及出借撮合服务，与人人贷https://renrendai.com和拍拍贷https://www.ppdai.com/  此类平台有异曲同工之妙，默认本地访问端口：3000

![1653316077575](https://user-images.githubusercontent.com/82700457/172781649-3d631897-317b-41b2-bb3f-8e44952bfd3c.png)


## 2、技术栈

**项目环境：**

|   环境   |  版本号  |
| :------: | :------: |
| Node.js  | v14.16.1 |
|  MySQL   |  5.7.34  |
|   JDK    |   1.8    |
|  Maven   |  3.6.3   |
|  Nacos   |  1.4.0   |
|  Redis   |  6.2.3   |
| RabbitMQ |  3.8.1   |

**后端**

- SpringBoot 2.6.7.RELEASE

- SpringCloud Hoxton.2021：微服务基础设施 - 服务注册、服务发现、服务熔断、微服务网关、配置中心等

- SpringCloud Alibaba 2.2.2.RELEASE

- MyBatis Plus：持久层框架和代码生成器

- Lombok：简化实体类开发

- Swagger2：Api接口文档生成工具

- Logback：日志系统

- alibaba-easyexcel：Excel读写

- Spring Data Redis：Spring项目中访问Redis缓存

- HTTPClient: 基于Http协议的客户端，用来实现远程调用

- Spring Task：定时任务


**数据库和中间件**

- MySQL 8.0：关系型数据库，管理工具：Navicat
- Redis6：缓存技术，管理工具：RedisDesktopManager
- RabbitMQ 3.8：消息中间件

**第三方接口整合**

- 阿里云短信：短信网关
- 阿里云OSS：分布式文件存储
- 资金托管平台API对接：汇付宝

**前端**

- Node.js： JavaScript 运行环境
- ES6：JavaScript的模块化版本
- axios：一个发送Ajax请求的工具
- Vue.js：web 界面的渐进式框架
- Element-UI：前端组件库
- 模块化开发：解决javascript变量全局空间污染的问题
- NPM：模块资源管理器
- vue-element-admin：基于Vue.js的后台管理系统UI集成方案
- NuxtJS：基于Vue.js构建的服务器端渲染应用的轻量级框架

## 3、项目架构

![1653316023003](https://user-images.githubusercontent.com/82700457/172781813-23881599-5de9-4b57-a04e-932a1bff179e.png)


## 4、业务开发流程

**核心功能介绍**

![1653317115862](https://user-images.githubusercontent.com/82700457/172781853-35d0da47-109d-48c4-9a1c-4884bfe2d6c8.png)


**详细流程**

![输入图片说明](https://images.gitee.com/uploads/images/2021/0331/092043_c2e31f08_7758392.png "屏幕截图.png")

## 5、启动项目

一切准备就绪后，我们就可以启动项目了，本系统共分为四个项目：

1. 后台接口系统：srb
2. 后台管理系统：srb-admin
3. 用户前端系统：srb-site
4. 支付系统：hfb

其中后台接口和支付系统都是使用SpringBoot开发的项目，后台管理页面和用户前端页面是使用Vue + ElementUI进行开发的，我们先来启动一下前端项目，将前端项目克隆到本地后，执行指令：

```shell
npm install
npm run dev
```

这就是后台管理页面：

![1654673657110](https://user-images.githubusercontent.com/82700457/172781893-23c81aea-c6ac-487c-8d9f-214238df7e44.png)


它将管理本系统中的所有数据，然后以同样的方式启动用户前端项目：

![1654691192774](https://user-images.githubusercontent.com/82700457/172781917-41464260-95ec-4209-8b16-2a05acbc1509.png)
