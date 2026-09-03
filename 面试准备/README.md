# 资产管理系统（AMS）Java 面试备考手册

> 基于本项目（RuoYi-Cloud 微服务改造的资产管理系统）整理的一套完整 Java 后端面试题、知识点详解与笔试真题。
> 所有知识点都尽量**结合本项目真实代码**讲解，方便你在面试时"讲项目 + 讲原理"一箭双雕。

---

## 一、项目一句话介绍（背下来）

> 本项目是基于 **Spring Cloud Alibaba 微服务架构**改造的**企业资产全生命周期管理系统**，
> 覆盖资产的**采购 → 入库 → 领用 → 变动 → 折旧 → 维保 → 盘点 → 处置/报废**全流程，
> 集成 **Activiti 工作流**做审批、**Redis** 做缓存与分布式 ID、**Nacos/Sentinel/Seata** 做服务治理与分布式事务。

---

## 二、技术栈速览（面试自我介绍要能脱口而出）

| 层次 | 技术 | 版本 | 在本项目中的作用 |
|------|------|------|------------------|
| 基础框架 | Spring Boot | 2.7.18 | 单体服务脚手架、自动配置 |
| 微服务 | Spring Cloud | 2021.0.9 | 微服务治理体系 |
| 微服务 | Spring Cloud Alibaba | 2021.0.6.1 | Nacos / Sentinel / Seata |
| 注册/配置中心 | Nacos | — | 服务注册发现 + 配置中心 |
| 网关 | Spring Cloud Gateway | — | 统一入口、路由、鉴权、限流 |
| 认证授权 | Spring Security + JWT | jjwt 0.9.1 | 无状态登录、Token 校验 |
| 服务调用 | OpenFeign | — | 服务间远程调用（`RemoteWorkflowService`） |
| 熔断降级 | Sentinel | — | 流量控制、熔断 |
| 分布式事务 | Seata | — | 跨服务数据一致性 |
| ORM | MyBatis-Plus + PageHelper | — | CRUD、分页 |
| 缓存 | Redis | — | 缓存、分布式 ID、分布式锁 |
| 工作流 | Activiti | — | 审批流程引擎（`ruoyi-workflow`） |
| 文件存储 | FastDFS / MinIO | — | 附件、PDF、二维码存储 |
| 其他 | POI / Velocity / Guava Retry / fastjson2 / Hutool / Lombok | — | Excel、代码生成、重试、工具 |

---

## 三、目录索引

| 文档 | 内容 | 面试权重 |
|------|------|----------|
| [01-Java基础与集合](01-Java基础与集合.md) | HashMap、ArrayList、String、并发集合、equals/hashCode | ⭐⭐⭐⭐ |
| [02-JVM](02-JVM.md) | 内存模型、GC、类加载、OOM 排查 | ⭐⭐⭐⭐ |
| [03-多线程与并发](03-多线程与并发.md) | 线程池、锁、volatile/CAS/AQS、ThreadLocal、CompletableFuture | ⭐⭐⭐⭐⭐ |
| [04-Spring框架](04-Spring框架.md) | IOC/AOP、Bean 生命周期、循环依赖、事务失效 | ⭐⭐⭐⭐⭐ |
| [05-SpringBoot](05-SpringBoot.md) | 自动配置、启动原理、Runner 接口 | ⭐⭐⭐⭐ |
| [06-SpringCloud微服务](06-SpringCloud微服务.md) | Nacos、Gateway、Feign、Sentinel、Seata、服务降级 | ⭐⭐⭐⭐⭐ |
| [07-MySQL与MyBatis](07-MySQL与MyBatis.md) | 索引、事务隔离、MVCC、锁、SQL 优化、MyBatis-Plus | ⭐⭐⭐⭐⭐ |
| [08-Redis缓存](08-Redis缓存.md) | 数据结构、缓存穿透/击穿/雪崩、分布式锁、分布式 ID | ⭐⭐⭐⭐⭐ |
| [09-设计模式](09-设计模式.md) | 策略、工厂、单例、模板方法、代理、观察者 | ⭐⭐⭐⭐ |
| [10-项目场景与笔试真题](10-项目场景与笔试真题.md) | 业务设计题、项目难点 Q&A、算法笔试 | ⭐⭐⭐⭐⭐ |

---

## 四、项目里可以直接引用的"黄金代码"（面试加分项）

面试时主动提到这些，能立刻证明你不是"背八股"，而是真的做过：

| 类名 | 对应知识点 | 面试可讲的话题 |
|------|-----------|---------------|
| `CacheClient` | 缓存穿透/击穿/雪崩 | `queryWithPassThrough`（空值缓存防穿透）、`queryWithMutex`（互斥锁防击穿）、`queryWithLogicalExpire`（逻辑过期+线程重建） |
| `RedisIdWorker` | 分布式 ID | `时间戳 << 32 | 序列号`，Redis `increment` 自增 |
| `ThreadPoolConfig` | 线程池 | 业务线程池 / IO 线程池**隔离**，`CallerRunsPolicy` 拒绝策略 |
| `ChangeServiceImpl` | 事务、异步编排、降级 | `CompletableFuture` 并行统计、Guava 分段批量更新、Redis 降级到 DB |
| `RetryUtils` | Guava Retry | 数据库 3 次重试、Redis 2 次重试 |
| `designPatterns/strategy` | 策略模式 | 折扣策略、支付策略（支付宝/微信/信用卡） |
| `ExceptionUtils` | 全局异常 | `@ControllerAdvice` + `@ExceptionHandler` |
| `CommandLineRunnerUtils` / `ApplicationRunnerUtils` | 启动钩子 | 两者区别、执行顺序 |

---

## 五、三个月备考路线建议

### 阶段一：基础夯实（第 1–2 周）
- 通读 `01`、`02`，把 HashMap、GC 这些高频题吃透。
- 每天 5 道 Java 基础题，能口述原理。

### 阶段二：并发与框架（第 3–6 周）
- `03` 多线程是重中之重，结合项目 `ThreadPoolConfig` 讲线程池。
- `04`、`05` 深入 Spring 源码：Bean 生命周期、循环依赖、事务失效。

### 阶段三：微服务与中间件（第 7–10 周）
- `06`、`07`、`08`：微服务调用链、MySQL 索引、Redis 缓存三兄弟。
- 结合项目 `CacheClient`、`ChangeFallbackFactory`、Seata 配置讲透。

### 阶段四：项目复盘与刷题（第 11–12 周）
- `09`、`10`：把项目亮点、难点、业务设计题全部练到能脱稿。
- 刷 LeetCode 高频题 + 每天模拟一次自我介绍。

---

## 六、面试开场白模板（1–2 分钟）

> 面试官好，我叫 XX。我最近完整参与/独立开发了一个**基于 Spring Cloud Alibaba 微服务架构的资产管理系统**。
> 系统负责企业固定资产从**采购、入库、领用、变动、折旧、维保、盘点，到处置报废**的**全生命周期管理**。
> 技术上，我用 **Nacos** 做注册和配置中心，**Gateway** 做统一网关，**Spring Security + JWT** 做无状态认证，
> 集成 **Activiti 工作流**做多级审批，用 **Redis** 解决缓存穿透、击穿、雪崩问题，并实现了基于 Redis 自增的**分布式 ID 生成**。
> 在资产变动审批这个核心模块里，我用**线程池隔离 + CompletableFuture 并行统计 + Guava 重试 + Redis 降级**优化了性能和稳定性。
> 这个项目让我对微服务、缓存、并发和事务都有了比较扎实的实践理解。
