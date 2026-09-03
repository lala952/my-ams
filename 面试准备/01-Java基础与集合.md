# 01 · Java 基础与集合

> 集合和 String 是面试第一关，答不好直接凉。这里把高频题按"原理 → 结合项目 → 追问"展开。

---

## 1. HashMap 底层原理（必考，务必讲全）

### 知识点详解

**JDK 1.8 之后 HashMap = 数组 + 链表 + 红黑树。**

- **结构**：底层是 `Node<K,V>[] table` 数组，每个桶位存链表头节点。
- **put 流程**：
  1. 对 key 求 `hash = (key.hashCode()) ^ (h >>> 16)`（高 16 位与低 16 位异或，让高位参与运算，减少哈希碰撞）。
  2. 计算桶下标 `index = (n - 1) & hash`（`n` 是数组长度，`n` 必须是 2 的幂，这样 `&` 等价于取模且分布均匀）。
  3. 桶为空直接放；桶有值则先比较 hash 和 key（`==` 或 `equals`），相同则覆盖，不同则尾插。
  4. 链表长度 ≥ 8 且数组长度 ≥ 64 时，链表**树化**为红黑树；红黑树节点 ≤ 6 时**退化**回链表。
- **扩容**：默认容量 16，负载因子 0.75。当 `size > capacity * loadFactor` 时扩容为 2 倍，并 rehash 重排。JDK1.8 用 `(e.hash & oldCap)` 判断节点留原位还是去新位，避免重新计算 hash。

### 高频追问（必须会）

**Q1：为什么数组长度要是 2 的幂？**
> 因为 `(n-1) & hash` 只有 `n` 是 2 的幂时，`n-1` 的二进制才是全 1，等价于 `hash % n` 且每个桶位都有机会被命中，分布最均匀。

**Q2：为什么负载因子是 0.75？**
> 是时间与空间的折中。太小 → 扩容频繁、浪费空间；太大 → 碰撞多、查询慢。0.75 是泊松分布下链表长度超过 8 概率极小的经验值。

**Q3：为什么链表转红黑树阈值是 8，退化是 6？**
> 8 是因为哈希碰撞导致链表长度达到 8 的概率极低（约千万分之一，符合泊松分布），此时树化能保证查询从 O(n) 降到 O(logn)。退化设 6 而非 8，是加了缓冲区间，避免在 8 附近频繁树化/退化的抖动。

**Q4：HashMap 是线程安全的吗？有什么并发问题？**
> 不安全。JDK1.7 在并发扩容时可能形成**环形链表导致死循环**；JDK1.8 改为尾插避免了死循环，但仍有**数据覆盖、读到中间态**等问题。并发场景用 `ConcurrentHashMap`。

### 结合项目
项目里 `CommandLineRunnerUtils` 用到了 `HashMap`（单线程打印系统资源信息，安全）；`CacheClient` 里没有直接用 HashMap，但你要是被问"为什么要用 `ConcurrentHashMap`"，可以举 `CommandLineRunnerUtils` 中同时 new 了 `ConcurrentHashMap` 和 `HashMap` 做对比——单线程用 HashMap 足够，多线程必须用并发容器。

---

## 2. ConcurrentHashMap 是如何保证线程安全的？

### 知识点详解

- **JDK 1.7**：分段锁 `Segment`（继承 `ReentrantLock`），默认 16 段，锁粒度是段级别。
- **JDK 1.8**：**放弃分段锁，改用 CAS + synchronized**。
  - 数组初始化、插入空桶：用 **CAS**（`casTabAt`），无锁竞争，失败就自旋。
  - 桶内已有节点（链表/红黑树）：用 **synchronized 锁住桶的头节点**，锁粒度细化到单个桶。
  - 读取 `get` 全程无锁（`volatile` 读 `table`），效率极高。

**一句话**：JDK1.8 的 `ConcurrentHashMap` = **CAS 无锁写入 + synchronized 桶级锁 + volatile 读**。

### 追问
**Q：为什么 get 不需要加锁？**
> 因为 `table` 和 `Node.val`、`Node.next` 都是 `volatile` 修饰，读操作直接可见最新值，无需加锁。

---

## 3. ArrayList 与 LinkedList 区别

| 对比项 | ArrayList | LinkedList |
|--------|-----------|-----------|
| 底层 | 动态数组 `Object[]` | 双向链表 |
| 随机访问 | O(1)，支持下标 | O(n)，需遍历 |
| 增删（中间） | O(n)，要移动元素 | O(1)（定位后改指针） |
| 增删（尾部） | 均摊 O(1) | O(1) |
| 内存 | 连续、无额外指针 | 每节点多两个指针 |
| 适用 | 查询多 | 频繁头/中间增删 |

**追问**：ArrayList 扩容机制？
> 无参构造默认空数组，第一次 add 扩容到 10；之后每次扩容为原来的 **1.5 倍**（`oldCapacity + (oldCapacity >> 1)`），用 `Arrays.copyOf` 拷贝到新数组。

---

## 4. String / StringBuilder / StringBuffer

| 对比项 | String | StringBuilder | StringBuffer |
|--------|--------|---------------|--------------|
| 可变性 | 不可变（final char[]/byte[]） | 可变 | 可变 |
| 线程安全 | 安全（不可变天然安全） | 不安全 | 安全（方法加 synchronized） |
| 性能 | 拼接会产生大量中间对象 | 快 | 比 Builder 慢 |

**追问：String 为什么设计成不可变？**
> ① 字符串常量池可复用，节省内存；② 作为 HashMap 的 key 时 hash 值稳定；③ 安全，可用于网络、线程共享；④ 天然线程安全。

**追问：`String s = "a" + "b"` 创建了几个对象？**
> 编译期常量折叠，直接优化成 `"ab"`，常量池里一个对象。若涉及变量（如 `"a" + s`）则底层用 StringBuilder 拼接。

---

## 5. equals 与 hashCode 的关系

1. 重写 `equals` 必须重写 `hashCode`（约定：两个对象 `equals` 相等，则 `hashCode` 必相等）。
2. `hashCode` 相等，`equals` 不一定相等（哈希碰撞）。
3. 原因：Hash 集合（HashMap/HashSet）先按 `hashCode` 定位桶，再按 `equals` 判等。若只重写 equals 不重写 hashCode，会导致"逻辑相等的对象"落在不同桶，出现重复。

**结合项目**：`Change`、`Assets` 等实体类用 Lombok `@Data`，它自动生成 `equals/hashCode`，但要注意：**带 `@Data` 的实体作为 HashMap key 时要小心双向关联字段导致的无限递归**。

---

## 6. == 与 equals 的区别

- `==`：基本类型比**值**，引用类型比**地址**（是否同一对象）。
- `equals`：默认 `Object.equals` 也是比地址，需要类自己重写才能比内容。

---

## 7. final / finally / finalize

- `final`：修饰类不可继承、方法不可重写、变量不可变（引用不可变，对象内容可变）。
- `finally`：异常处理中**一定执行**（除非 `System.exit` 或 JVM 崩溃）。
- `finalize`：对象被 GC 前调用，JDK9 已废弃，不可靠。

---

## 8. 异常体系

```
Throwable
├── Error（JVM 级错误，如 OOM、StackOverflow，不处理）
└── Exception
    ├── RuntimeException（运行时异常，可处理也可不处理）
    └── 受检异常（Checked，必须 try-catch 或 throws）
```

**结合项目**：项目里 `ExceptionUtils` 用 `@ControllerAdvice` 全局捕获 `Exception`；业务里大量抛 `ServiceException`（自定义运行时异常），这是若依框架统一的业务异常体系。

---

## 9. 泛型

- **类型擦除**：编译后泛型信息被擦除，如 `List<String>` 和 `List<Integer>` 在运行时是同一个类。
- **? extends T（上界）**：只能读不能写；**? super T（下界）**：只能写不能读（PECS 原则）。
- **结合项目**：`CacheClient` 里 `public <R, ID> R queryWithPassThrough(...)` 就是泛型方法，`<R, ID>` 声明两个类型参数，`Function<ID, R>` 是函数式接口。

---

## 10. 反射与动态代理（过渡到 Spring）

- **反射**：运行时获取类信息、创建对象、调用方法，是 Spring IOC、AOP、MyBatis 映射的基础。
- **动态代理**：
  - JDK 动态代理：基于**接口**，`Proxy.newProxyInstance`。
  - CGLIB：基于**继承**，`Enhancer` 生成子类。
  - Spring 默认：有接口用 JDK，无接口用 CGLIB（Spring Boot 2.x 默认 `proxyTargetClass=true`，单例用 CGLIB）。

---

## 附：本模块高频笔试/面试题速答

1. **HashMap 的 hash 方法为什么要 `>>> 16`？** — 让高 16 位参与低位运算，减少碰撞。
2. **HashMap 扩容为什么是 2 倍？** — 保证容量始终是 2 的幂，rehash 只需判断一位。
3. **ConcurrentHashMap 1.7 和 1.8 区别？** — 1.7 分段锁 Segment；1.8 CAS + synchronized 桶级锁。
4. **ArrayList 线程不安全怎么办？** — `Collections.synchronizedList` 或 `CopyOnWriteArrayList`（读多写少）。
5. **Integer 缓存范围？** — `-128 ~ 127`，`Integer a = 127; a == b` 为 true。
6. **深拷贝和浅拷贝？** — 浅拷贝只拷贝引用，深拷贝复制对象本身；深拷贝可用序列化或手动递归。
7. **final 修饰的引用类型变量能改内容吗？** — 能，不能改的是引用指向。
8. **为什么 String 用 final？** — 不可变、安全、常量池复用、hash 稳定。
9. **List 遍历删除元素怎么不报错？** — 用 `Iterator.remove()`，不要用 `foreach` + `list.remove()`（会 `ConcurrentModificationException`）。
10. **fail-fast 和 fail-safe？** — fail-fast 迭代时检测 modCount，结构变化抛异常；fail-safe（如 CopyOnWrite）在副本上迭代不抛异常。
