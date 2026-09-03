# 05 · Spring Boot

> Spring Boot 让开发变简单，核心是"约定大于配置 + 自动装配"。重点：自动配置原理、启动流程、Runner 接口。

---

## 1. Spring Boot 和 Spring 的关系 / 区别

- Spring 是核心框架（IOC/AOP），但配置繁琐（大量 XML/Java 配置）。
- Spring Boot 是 Spring 的**脚手架**，封装了 Spring，提供**自动配置、内嵌服务器、起步依赖（starter）、外部化配置、Actuator 监控**，开箱即用。

---

## 2. 什么是自动配置？（必考）

### 2.1 原理
1. 启动类 `@SpringBootApplication` 包含三个核心注解：
   - `@SpringBootConfiguration`：标记配置类。
   - `@EnableAutoConfiguration`：开启自动配置。
   - `@ComponentScan`：扫描当前包及子包。
2. `@EnableAutoConfiguration` 里的 `@Import(AutoConfigurationImportSelector.class)` 会读取 `META-INF/spring.factories`（或 2.7 后的 `AutoConfiguration.imports`）里的 `xxxAutoConfiguration` 自动配置类。
3. 每个自动配置类用 `@ConditionalOnXxx` 条件注解判断是否生效（如 `@ConditionalOnClass`、`@ConditionalOnMissingBean`、`@ConditionalOnProperty`）。
4. 按需自动装配 Bean。

**一句话**：自动配置 = 启动类触发 → 读取所有候选自动配置类 → 通过条件注解过滤 → 装配需要的 Bean。

### 2.2 结合项目
项目的 `RuoYiAssetApplication` 就是启动类，配合 `spring-boot-dependencies` 的 BOM 管理版本，`@EnableDiscoveryClient` 注册到 Nacos。你可以说：
> Spring Boot 通过 `@EnableAutoConfiguration` 扫描 classpath 里的自动配置类，比如引入了 Redis starter 就会自动装配 `RedisTemplate`，引入 MyBatis-Plus starter 就自动配置 `SqlSessionFactory`。

---

## 3. Spring Boot 启动流程

1. 创建 `SpringApplication`，推断应用类型（Servlet/Reactive）。
2. 加载 `ApplicationContextInitializer` 和 `ApplicationListener`。
3. `refreshContext`：刷新 IOC 容器（这是 Spring 的核心，加载 Bean 定义、实例化单例）。
4. 执行 `CommandLineRunner` / `ApplicationRunner` 的回调。

---

## 4. CommandLineRunner vs ApplicationRunner（项目里有现成代码）

### 4.1 相同点
- 都在 Spring 容器刷新完成后、应用真正就绪前执行。
- 都只提供一个 `run()` 方法。
- 都可以用 `@Order` 控制顺序。

### 4.2 不同点
| 对比 | CommandLineRunner | ApplicationRunner |
|------|-------------------|-------------------|
| 参数 | `run(String... args)`，原始字符串数组 | `run(ApplicationArguments args)`，封装过的参数 |
| 参数解析 | 需自己解析 | 提供 `getOptionNames()`、`getOptionValues()` 等方法 |

### 4.3 结合项目
项目 `CommandLineRunnerUtils`（打印 JVM 内存/CPU 信息做健康检查）和 `ApplicationRunnerUtils` 正好是这两个接口的例子。你可以说：
> 我用 `CommandLineRunner` 在应用启动后打印 JVM 的堆内存、CPU 核数等资源信息，做一个启动时的资源健康检查；`ApplicationRunner` 提供封装过的 `ApplicationArguments`，解析启动参数更方便。

**追问：两者的执行顺序？**
> 默认没有 `@Order` 时，先执行 `ApplicationRunner` 还是 `CommandLineRunner` 没有严格保证（通常按 Bean 注册顺序），建议用 `@Order` 明确顺序。更准确：Spring Boot 会先收集所有 Runner，按 `@Order` 排序后依次执行。

---

## 5. starter 起步依赖机制

- 一个 starter 就是一个"打包好的自动配置 + 依赖"，引入后自动生效。
- 命名规范：官方 `spring-boot-starter-xxx`，第三方 `xxx-spring-boot-starter`。
- 项目里的 `pagehelper-spring-boot-starter` 就是典型第三方 starter，引入即可自动配置分页插件。

---

## 6. Spring Boot 配置文件与外部化配置

- 支持 `application.properties` / `application.yml`。
- **优先级（从高到低）**：命令行参数 > 环境变量 > `application-{profile}.yml` > `application.yml` > `@PropertySource`。
- **多环境**：`spring.profiles.active` 切换环境。
- **结合项目**：微服务项目的配置放在 **Nacos 配置中心**，本地 `bootstrap.yml` 只配 Nacos 地址，实现配置统一管理和动态刷新（`@RefreshScope`）。

---

## 7. Spring Boot 常用注解

| 注解 | 作用 |
|------|------|
| `@SpringBootApplication` | 主启动类，聚合三注解 |
| `@ConfigurationProperties` | 读取配置绑定到 Bean |
| `@Value` | 注入单个配置值 |
| `@ConditionalOnClass` / `@ConditionalOnMissingBean` | 条件装配 |
| `@RestController` | `@Controller` + `@ResponseBody` |
| `@Configuration` | 声明配置类 |
| `@EnableScheduling` | 开启定时任务 |
| `@Async` | 异步方法（配合 `@EnableAsync`） |

---

## 8. Spring Boot 的异常处理

**三种方式**：
1. `@ControllerAdvice` + `@ExceptionHandler`（全局，项目 `ExceptionUtils` 用的就是）。
2. `@ExceptionHandler`（单 Controller 内）。
3. `ErrorController` / `HandlerExceptionResolver`（自定义）。

**结合项目**：
> 我用 `@ControllerAdvice` 做全局异常捕获，统一返回错误信息；业务里抛自定义的 `ServiceException`，由全局处理器兜底，避免异常堆栈直接暴露给前端。

---

## 附：高频面试题速答

1. **Spring Boot 如何做到快速开发？** — 自动配置、starter、内嵌容器、外部化配置。
2. **如何自定义一个 starter？** — 写 `xxxAutoConfiguration` + `spring.factories`/`AutoConfiguration.imports` + 条件注解。
3. **@SpringBootApplication 包含哪三个注解？** — `@SpringBootConfiguration`、`@EnableAutoConfiguration`、`@ComponentScan`。
4. **内嵌 Tomcat 怎么换成 Jetty？** — 排除 `spring-boot-starter-tomcat`，引入 `spring-boot-starter-jetty`。
5. **bootstrap.yml 和 application.yml 区别？** — bootstrap 优先加载，用于从配置中心拉取配置；application 是应用配置（Spring Cloud 场景）。
6. **如何实现热部署？** — devtools（开发环境），生产用配置中心动态刷新。
7. **@Value 和 @ConfigurationProperties 区别？** — @Value 单个注入、需写全路径；@ConfigurationProperties 批量绑定、支持松散绑定和校验。
8. **Spring Boot 怎么读取外部配置？** — 通过 `Environment`、`@Value`、`@ConfigurationProperties`，优先级见上。
9. **如何优雅停机？** — 配置 `server.shutdown=graceful`，等待在途请求处理完。
10. **Actuator 是什么？** — 提供健康检查、指标监控等端点（`/actuator/health`）。
