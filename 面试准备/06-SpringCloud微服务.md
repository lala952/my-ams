# 06 · Spring Cloud 微服务

> 你的项目是微服务架构，这块是重点中的重点，面试官一定会问服务治理、调用链、分布式事务。

---

## 1. 微服务架构概述

### 1.1 什么是微服务？优缺点？

- **定义**：把单体应用拆分成一组小而自治的服务，每个服务独立部署、独立数据库、通过轻量级通信（HTTP/RPC）协作。
- **优点**：独立部署、技术栈灵活、易扩展、故障隔离、团队自治。
- **缺点**：分布式复杂度高、网络调用不可靠、数据一致性难、运维监控成本高。

### 1.2 结合项目
本项目拆分为：`ruoyi-gateway`（网关）、`ruoyi-auth`（认证）、`ruoyi-system`（系统）、`ruoyi-asset`（资产核心业务）、`ruoyi-workflow`（工作流）、`ruoyi-job`（定时任务）、`ruoyi-file`（文件）、`ruoyi-gen`（代码生成）。

---

## 2. 服务注册与发现 —— Nacos

### 2.1 Nacos 是什么？
阿里巴巴开源的**注册中心 + 配置中心**，支持服务注册发现、健康检查、动态配置。

### 2.2 服务注册发现的流程
1. 服务启动时向 Nacos 注册自己的 IP:端口。
2. 服务消费者从 Nacos 拉取服务列表并缓存本地。
3. 服务通过**心跳**维持注册，Nacos 剔除不健康实例。
4. 配置变更时 Nacos 推送更新（动态刷新）。

### 2.3 Nacos 和 Eureka 的区别
| 对比 | Nacos | Eureka |
|------|-------|--------|
| 一致性 | AP + CP 可选 | AP |
| 健康检查 | 心跳 + 主动探测 | 心跳 |
| 配置中心 | 支持 | 不支持（需 Config） |
| 项目 | 阿里，活跃 | Netflix 停止维护 |

### 2.4 结合项目
项目启动类有 `@EnableDiscoveryClient`，`bootstrap.yml` 配置 Nacos 地址，服务间调用通过 Nacos 做服务发现。

---

## 3. 服务调用 —— OpenFeign（项目里有 `RemoteWorkflowService`）

### 3.1 Feign 原理
1. 声明式 HTTP 客户端，`@FeignClient(name = "ruoyi-workflow")` 指定服务名。
2. 底层通过**动态代理**生成接口实现，把接口方法转成 HTTP 请求。
3. 结合 Ribbon/LoadBalancer 做负载均衡，通过 Nacos 解析服务地址。

### 3.2 Feign 调用流程（背）
接口调用 → 动态代理 → 组装请求 → LoadBalancer 选实例 → HTTP 调用 → 解析响应。

### 3.3 结合项目
`ChangeServiceImpl` 注入 `RemoteWorkflowService`（`@FeignClient` 远程调用工作流服务做审批），`ChangeFallbackFactory` 是它的**降级工厂**：
> 当工作流服务不可用时，Feign 会调用 `ChangeFallbackFactory` 返回的降级实现，返回统一错误或兜底数据，避免雪崩。这就是 **Sentinel/Feign 的熔断降级**。

---

## 4. 服务网关 —— Spring Cloud Gateway

### 4.1 网关的作用
统一入口、**路由转发**、**鉴权过滤**、**限流**、跨域、日志。

### 4.2 三大核心概念
- **Route（路由）**：匹配规则 + 目标地址。
- **Predicate（断言）**：路由匹配条件（路径、方法、Header）。
- **Filter（过滤器）**：请求前后处理（鉴权、限流、改写）。

### 4.3 结合项目
`ruoyi-gateway` 是网关，统一处理 JWT 鉴权、请求转发到各微服务、日志记录。

**追问：Gateway 和 Zuul 的区别？**
> Gateway 基于 **WebFlux（Reactor 响应式）**，非阻塞、性能好；Zuul 1.x 基于 Servlet 阻塞模型。Gateway 是 Spring Cloud 官方主推。

---

## 5. 熔断降级 —— Sentinel

### 5.1 三个核心能力
- **流量控制（限流）**：QPS 限制，防流量洪峰。
- **熔断降级**：慢调用/异常比例超阈值时熔断，快速失败。
- **系统保护**：负载、CPU 保护。

### 5.2 结合项目
依赖里有 `ruoyi-common-seata` 和 Sentinel。Feign 的降级（`ChangeFallbackFactory`）就是熔断降级的落地：
> 当远程服务调用超时或异常达到阈值，Sentinel 触发熔断，走 Fallback 降级逻辑，保证主流程不被打垮。

---

## 6. 分布式事务 —— Seata

### 6.1 为什么需要分布式事务？
微服务下，一个业务跨多个服务、多个数据库，本地事务无法保证全局一致性。

### 6.2 CAP 理论 / BASE 理论
- **CAP**：一致性（C）、可用性（A）、分区容错性（P），三者最多同时满足两个。分布式必须保证 P，所以在 C 和 A 间取舍。
- **BASE**：基本可用（Basically Available）、软状态（Soft state）、最终一致（Eventually consistent）。

### 6.3 分布式事务解决方案
| 方案 | 说明 | 适用 |
|------|------|------|
| 2PC / XA | 两阶段提交，强一致 | 性能差，少用 |
| TCC | Try-Confirm-Cancel，补偿 | 对一致性要求高 |
| **AT（Seata）** | 自动补偿，基于 undo log | 项目用的这个 |
| 本地消息表 | 最终一致 | 异步场景 |
| 可靠消息（RocketMQ） | 事务消息 | 异步场景 |

### 6.4 Seata AT 模式原理（项目重点）
1. 全局事务开启，生成全局事务 XID。
2. 每个分支事务执行本地 SQL 时，Seata 拦截并记录 **undo log**（反向 SQL）。
3. 分支提交：先提交本地事务，但数据处于"全局未提交"状态（写隔离）。
4. 全局提交：删除 undo log；全局回滚：用 undo log 反向补偿。

**一句话**：Seata AT 模式 = 全局事务协调 + 每个分支的 undo log 自动补偿，实现最终一致。

### 6.5 结合项目的讲法
> 项目引入 `ruoyi-common-seata`，配置 Seata 服务，用 `@GlobalTransactional` 注解标在跨服务的业务方法上（如资产变动审批同时更新资产模块和工作流模块的数据），Seata 用 AT 模式的 undo log 保证两边的数据要么都成功、要么都回滚。

---

## 7. 认证授权 —— JWT + Spring Security

### 7.1 JWT 是什么？
JSON Web Token，无状态令牌，三段式：`Header.Payload.Signature`（Base64 编码）。
- Header：算法；Payload：用户信息 + 过期时间；Signature：用密钥签名防篡改。

### 7.2 登录流程（项目 auth 模块）
1. 用户登录 → `SysLoginService` 校验账号密码 → 生成 JWT → 返回 Token。
2. 前端请求携带 Token（Authorization 头）。
3. 网关/过滤器解析校验 Token → 通过则放行。

### 7.3 JWT 和 Session 的区别
| 对比 | JWT | Session |
|------|-----|---------|
| 存储 | 客户端 | 服务端 |
| 状态 | 无状态 | 有状态 |
| 扩展 | 天然适合分布式 | 需共享 Session（Redis） |
| 注销 | 难（需黑名单） | 简单 |

### 7.4 结合项目
`ruoyi-auth` 的 `TokenController`、`SysLoginService` 负责登录发 Token，网关/过滤器负责校验。

---

## 附：高频面试题速答

1. **服务雪崩是什么？怎么解决？** — 一个服务不可用导致依赖它的服务连锁不可用。解决：熔断（Sentinel）、降级（Fallback）、限流、超时。
2. **Feign 调用超时怎么配？** — `ribbon.ReadTimeout` / `ConnectTimeout` 或 `feign.client.config`。
3. **Nacos 服务注册和 Eureka 区别？** — 见上表，Nacos 多了配置中心、健康检查更强。
4. **网关路由转发原理？** — 断言匹配请求 → 过滤器链处理 → 转发到下游服务。
5. **如何保证微服务间调用安全？** — Token 校验、内部服务走内网、签名认证。
6. **分布式事务有哪些方案？** — 2PC、TCC、Seata AT、消息最终一致。
7. **限流算法有哪些？** — 计数器、滑动窗口、漏桶、令牌桶（Sentinel 默认）。
8. **接口幂等怎么做？** — 唯一约束、Token、状态机、分布式锁。
9. **配置中心动态刷新原理？** — Nacos 监听配置变更，`@RefreshScope` 重新创建 Bean。
10. **负载均衡策略？** — 轮询、随机、权重、最少连接、一致性哈希。
