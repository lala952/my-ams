<p align="center">
	<img alt="logo" src="https://oscimg.oschina.net/oscnet/up-b99b286755aef70355a7084753f89cdb7c9.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">资产管理系统，基于开源RuoYi v3.6.7进行二次开发项目</h1>
<h4 align="center">基于 Vue/Element UI 和 Spring Boot/Spring Cloud & Alibaba 前后端分离的分布式微服务架构</h4>
<p align="center">
	<a href="https://gitee.com/y_project/RuoYi-Cloud/stargazers"><img src="https://gitee.com/y_project/RuoYi-Cloud/badge/star.svg?theme=dark"></a>
	<a href="https://gitee.com/y_project/RuoYi-Cloud"><img src="https://img.shields.io/badge/RuoYi-v3.6.7-brightgreen.svg"></a>
	<a href="https://gitee.com/y_project/RuoYi-Cloud/blob/master/LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg"></a>
</p>

## 平台简介

若依是一套全部开源的快速开发平台，毫无保留给个人及企业免费使用。

* 本仓库为RuoYi-Cloud的Spring Boot 2 的版本，保持同步更新。

* 后端采用Spring Boot2、Spring Cloud & Alibaba。

* 注册中心、配置中心选型Nacos，权限认证使用Redis。

* 流量控制框架选型Sentinel，分布式事务选型Seata。

* 阿里云优惠券：[点我进入](http://aly.ruoyi.vip)，腾讯云优惠券：[点我进入](http://txy.ruoyi.vip)

# 版本分支

RuoYi-Cloud 后端项目提供 Spring Boot 2.x / 3.x / 4.x 多版本分支的并行维护。

| 名称             | 说明                                  | 地址                                                       |
|:---------------|:------------------------------------|:---------------------------------------------------------|
| master 默认分支    | Spring Boot 4.x (JDK 17+、Nacos 3.x) | https://gitee.com/y_project/RuoYi-Cloud                  |
| springboot3 分支 | Spring Boot 3.x (JDK 17+、Nacos 3.x) | https://gitee.com/y_project/RuoYi-Cloud/tree/springboot3 |
| springboot2 分支 | Spring Boot 2.x (JDK 8+、 Nacos 2.x) | https://gitee.com/y_project/RuoYi-Cloud/tree/springboot2 |

RuoYi-Cloud 前端项目提供 Vue 2.x / 3.x / JavaScript TypeScript 版本均可混用搭配

| 项目名称       | **RuoYi-Cloud**                                        | **RuoYi-Cloud-Vue3**                                                   | **RuoYi-Cloud-Vue3-TypeScript**                                                                   |
|:-----------|:-------------------------------------------------------|:-----------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------|
| **前端框架**   | Vue 2                                                  | Vue 3                                                                  | Vue 3                                                                                             |
| **脚本语言**   | JavaScript                                             | JavaScript                                                             | TypeScript                                                                                        |
| **构建工具**   | Vue CLI                                                | Vite                                                                   | Vite                                                                                              |
| **UI 组件库** | Element UI                                             | Element Plus                                                           | Element Plus                                                                                      |
| **状态管理**   | Vuex                                                   | Pinia                                                                  | Pinia                                                                                             |
| **路由管理**   | Vue Router 3                                           | Vue Router 4                                                           | Vue Router 4                                                                                      |
| **核心特点**   | 1. 技术栈经典稳定<br>2. 社区资料丰富<br>3. 当前维护重心已转移                | 1. 现代前端技术栈<br>2. 开发体验与性能更优<br>3. 官方主推的活跃版本                             | 1. 类型加持，减少沟通成本<br>2. 开发时有提示，效率更高<br>3. 多人协作企业级开发项目                                                |
| **仓库地址**   | [RuoYi-Cloud](https://gitee.com/y_project/RuoYi-Cloud) | [RuoYi-Cloud-Vue3](https://gitcode.com/yangzongzhuan/RuoYi-Cloud-Vue3) | [RuoYi-Cloud-Vue3-TypeScript](https://gitcode.com/yangzongzhuan/RuoYi-Cloud-Vue3/tree/typescript) |

## 系统模块

~~~
com.ruoyi     
├── ruoyi-ui              // 前端框架 [80]
├── ruoyi-gateway         // 网关模块 [8080]
├── ruoyi-auth            // 认证中心 [9200]
├── ruoyi-api             // 接口模块
│       └── ruoyi-api-system                          // 系统接口
├── ruoyi-common          // 通用模块
│       └── ruoyi-common-core                         // 核心模块
│       └── ruoyi-common-datascope                    // 权限范围
│       └── ruoyi-common-datasource                   // 多数据源
│       └── ruoyi-common-log                          // 日志记录
│       └── ruoyi-common-redis                        // 缓存服务
│       └── ruoyi-common-seata                        // 分布式事务
│       └── ruoyi-common-security                     // 安全模块
│       └── ruoyi-common-sensitive                    // 数据脱敏
│       └── ruoyi-common-swagger                      // 系统接口
├── ruoyi-modules         // 业务模块
│       └── ruoyi-system                              // 系统模块 [9201]
│       └── ruoyi-gen                                 // 代码生成 [9202]
│       └── ruoyi-job                                 // 定时任务 [9203]
│       └── ruoyi-file                                // 文件服务 [9300]
├── ruoyi-visual          // 图形化管理模块
│       └── ruoyi-visual-monitor                      // 监控中心 [9100]
├──pom.xml                // 公共依赖
~~~

## 架构图

<img src="https://oscimg.oschina.net/oscnet/up-82e9722ecb846786405a904bafcf19f73f3.png"/>

## 内置功能

1. 用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2. 部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3. 岗位管理：配置系统用户所属担任职务。
4. 菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5. 角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6. 字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7. 参数管理：对系统动态配置常用参数。
8. 通知公告：系统通知公告信息发布维护。
9. 操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 在线构建器：拖动表单元素生成相应的HTML代码。
17. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

11111111111111111111111111