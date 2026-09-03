# 04 · Spring 框架（IOC / AOP / 事务）

> Spring 是 Java 后端面试的灵魂。重点：IOC 与 AOP、Bean 生命周期、循环依赖、事务失效。

---

## 1. 什么是 IOC？什么是 DI？

- **IOC（控制反转）**：把对象的创建、装配、生命周期管理交给 Spring 容器，而不是程序员手动 new。
- **DI（依赖注入）**：IOC 的具体实现方式，容器把依赖自动注入到对象里。
- **好处**：解耦、易测试、易扩展、统一管理。

**结合项目**：`ChangeServiceImpl` 通过 `@Autowired` 注入 `ChangeMapper`、`IAssetsService`、`RemoteWorkflowService` 等，全部由 Spring 容器管理，不用自己 new。

**依赖注入方式**：构造器注入（推荐，`CacheClient` 就用的构造器注入）、Setter 注入、字段注入（`@Autowired`）。

---

## 2. Spring 容器与 Bean 生命周期（必考）

### 2.1 生命周期完整流程

1. **实例化**：通过反射调用构造器创建对象。
2. **属性填充**：`populateBean` 注入依赖、`@Autowired`、`@Value`。
3. **Aware 接口回调**：`BeanNameAware`、`BeanFactoryAware`、`ApplicationContextAware`。
4. **BeanPostProcessor 前置处理**：`postProcessBeforeInitialization`。
5. **初始化**：`@PostConstruct` → `InitializingBean.afterPropertiesSet` → 自定义 `init-method`。
6. **BeanPostProcessor 后置处理**：`postProcessAfterInitialization`（AOP 动态代理在这里生成）。
7. **使用**。
8. **销毁**：`@PreDestroy` → `DisposableBean.destroy` → `destroy-method`。

**高频追问：AOP 代理在哪一步生成？**
> 在 `postProcessAfterInitialization`，由 `AnnotationAwareAspectJAutoProxyCreator` 生成动态代理。

### 2.2 Bean 的作用域

| 作用域 | 说明 |
|--------|------|
| singleton | 默认，单例 |
| prototype | 每次获取新建 |
| request / session | Web 环境，请求/会话级别 |

**追问：singleton 和 prototype 哪个线程安全？**
> 都不天然安全。singleton 多线程共享，成员变量需谨慎；prototype 每次新建但共享成员变量仍需注意。Spring 本身不保证线程安全。

---

## 3. 什么是 AOP？应用场景？

- **AOP（面向切面编程）**：把横切关注点（日志、事务、权限）从业务逻辑里抽离，通过动态代理织入。
- **核心概念**：切面（Aspect）、切入点（Pointcut）、通知（Advice）、连接点（JoinPoint）。
- **通知类型**：`@Before`、`@AfterReturning`、`@AfterThrowing`、`@After`、`@Around`。

**结合项目（必讲）**：
1. **日志**：若依框架用 `@Log` 注解 + AOP 切面做操作日志记录（`ApplicationRunnerUtils` 里就 import 了 `@Log`）。
2. **事务**：`@Transactional` 本质就是 AOP。
3. **数据权限**：若依的 `@DataScope` 数据范围注解，用 AOP 拦截拼 SQL。

**动态代理区别**：JDK 动态代理（接口）vs CGLIB（继承）。Spring 默认：有接口 JDK，无接口 CGLIB。

---

## 4. Spring 事务（面试必考，结合项目 ChangeServiceImpl）

### 4.1 事务的传播行为（7 种）

| 传播行为 | 说明 |
|---------|------|
| `REQUIRED`（默认） | 有事务就加入，无则新建 |
| `REQUIRES_NEW` | 总是新建事务，挂起当前 |
| `SUPPORTS` | 有就加入，无则非事务运行 |
| `NOT_SUPPORTED` | 非事务运行，挂起当前 |
| `MANDATORY` | 必须已有事务，否则抛异常 |
| `NEVER` | 必须非事务，否则抛异常 |
| `NESTED` | 嵌套事务（保存点） |

### 4.2 事务失效的 8 大场景（必考，项目里踩过坑）

你的提交记录里有"**fix: 修复审批事务失效问题**"，这就是面试素材，直接背这 8 条：

1. **方法不是 public**：`@Transactional` 只能作用于 public 方法。
2. **同类内部调用（自调用）**：A 类方法调用 B 类的 `@Transactional` 方法，走的是 `this` 调用，**绕过了代理**，事务失效。解决：注入自身代理、拆到别的类、或用 `AopContext.currentProxy()`。
3. **异常被 catch 吞掉**：`try-catch` 捕获异常没抛出，Spring 感知不到，不会回滚。
4. **抛的不是 RuntimeException/Error**：默认只对 `RuntimeException` 和 `Error` 回滚，受检异常需 `@Transactional(rollbackFor = Exception.class)`。
5. **数据库引擎不支持事务**：如 MyISAM 不支持。
6. **事务方法被 final/static 修饰**：代理无法生效。
7. **传播行为设置不当**：如 `NOT_SUPPORTED`、`REQUIRES_NEW` 用错。
8. **多线程调用**：子线程里的事务和主线程不是同一个事务上下文。

**结合项目的讲法**：
> 我在做资产变动审批时，遇到"审批通过后更新资产状态没有回滚"的问题，排查发现是**同类方法内部自调用**导致事务失效——`this.approve()` 绕过了 Spring 代理。后来把事务方法拆到独立 Service 或注入自身代理解决。另外还有**异常被 catch 吞掉**导致不回滚的情况，统一改为抛 `ServiceException` 让事务回滚。

### 4.3 事务隔离级别

| 级别 | 解决 | 未解决 |
|------|------|--------|
| READ_UNCOMMITTED | — | 脏读、不可重复读、幻读 |
| READ_COMMITTED | 脏读 | 不可重复读、幻读 |
| REPEATABLE_READ（MySQL默认） | 脏读、不可重复读 | 幻读（InnoDB 用 MVCC+间隙锁基本解决） |
| SERIALIZABLE | 全部 | — |

---

## 5. 循环依赖

### 5.1 什么是循环依赖？
A 依赖 B，B 依赖 A，注入时形成闭环。

### 5.2 Spring 如何解决（三级缓存）

| 缓存 | 名称 | 存放内容 |
|------|------|---------|
| 一级缓存 | `singletonObjects` | 完整的单例 Bean |
| 二级缓存 | `earlySingletonObjects` | 早期暴露的 Bean（未注入完） |
| 三级缓存 | `singletonFactories` | Bean 的工厂（`ObjectFactory`），可生成代理对象 |

**流程**：A 实例化 → 把 A 的工厂放入三级缓存 → 发现依赖 B → B 实例化 → B 依赖 A → 从三级缓存拿 A 的工厂生成早期 A → B 注入完成 → A 注入 B 完成。

**为什么需要三级缓存？**
> 三级缓存存的是 `ObjectFactory`，目的是在有 AOP 代理时，能提前返回**代理对象**而非原始对象。没有 AOP 时二级缓存就够，但为了统一处理代理，用三级。

**追问：构造器注入能解决循环依赖吗？**
> 不能。三级缓存解决的是 **Setter/字段注入**的循环依赖（先实例化后注入）；构造器注入在实例化阶段就需要依赖，无法提前暴露。

---

## 6. Spring 用了哪些设计模式？（高频）

| 模式 | Spring 中的体现 |
|------|----------------|
| 工厂模式 | `BeanFactory`、`ApplicationContext` |
| 单例模式 | 默认 Bean 作用域 singleton |
| 代理模式 | AOP |
| 模板方法 | `JdbcTemplate`、`RestTemplate` |
| 观察者模式 | 事件监听 `ApplicationListener` |
| 策略模式 | `InstantiationStrategy` |
| 适配器模式 | `HandlerAdapter`、`MethodArgumentResolver` |

---

## 7. @Autowired 和 @Resource 的区别

| 对比 | @Autowired | @Resource |
|------|-----------|-----------|
| 来源 | Spring | JSR-250（JDK） |
| 装配方式 | 默认 byType | 默认 byName，再 byType |
| 配合 | 需 `@Qualifier` 指定名 | 有 `name` 属性 |
| 可填 | `required=false` | 无 |

---

## 附：高频面试题速答

1. **Spring 的 Bean 是线程安全的吗？** — 默认单例，不保证线程安全，取决于是否有共享可变状态。
2. **BeanFactory 和 ApplicationContext 区别？** — ApplicationContext 是 BeanFactory 子接口，多了国际化、事件、AOP、Web 支持。
3. **@Component 和 @Bean 区别？** — @Component 标注类自动扫描；@Bean 标注方法手动注册，适合注册第三方类。
4. **@Configuration 和 @Component 区别？** — @Configuration 是 CGLIB 代理，保证单例（@Bean 方法调用返回同一实例）。
5. **事务的隔离级别默认是什么？** — 使用数据库默认隔离级别。
6. **Spring 事务回滚规则？** — 默认 RuntimeException 和 Error 回滚，受检异常不回滚。
7. **如何保证接口幂等？** — 唯一索引、Token、分布式锁、状态机校验。
8. **@Lazy 作用？** — 延迟初始化 Bean。
9. **Spring MVC 请求流程？** — DispatcherServlet → HandlerMapping 找处理器 → HandlerAdapter 执行 → 返回 ModelAndView → 视图解析。
10. **为什么要用构造器注入而不是字段注入？** — 不可变、依赖明确、便于测试、避免 NPE。
