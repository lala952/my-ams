# 02 · JVM（Java 虚拟机）

> JVM 是区分"背八股"和"真理解"的分水岭。重点：内存模型、GC、类加载、OOM 排查。

---

## 1. JVM 内存模型（运行时数据区）

```
JVM 运行时数据区
├── 线程私有
│   ├── 程序计数器（PC）：记录当前线程执行到哪，唯一不会 OOM 的区域
│   ├── 虚拟机栈（Java 栈）：方法调用的栈帧（局部变量表、操作数栈、返回地址），StackOverflowError
│   └── 本地方法栈：native 方法
├── 线程共享
│   ├── 堆（Heap）：对象实例、数组，GC 主战场，OOM: Java heap space
│   ├── 方法区/元空间（JDK8 起 Metaspace）：类信息、常量、静态变量，OOM: Metaspace
│   └── 运行时常量池（1.7 后移入堆）
```

**高频追问：**
- **堆和栈的区别**：堆存对象实例（共享、GC），栈存方法调用栈帧（私有、自动回收、线程结束即释放）。
- **JDK8 元空间取代永久代的原因**：永久代内存上限固定、易 OOM；元空间用**本地内存**，只受系统内存限制，且字符串常量池移入堆，GC 效率更好。

### 结合项目
`CommandLineRunnerUtils` 里打印的 `maxMemory/totalMemory/freeMemory` 就是堆内存指标：
- `maxMemory` = `-Xmx` 上限
- `totalMemory` = 已分配堆内存（`-Xms` 起步）
- `freeMemory` = 空闲堆内存
面试时可说"我在项目里用 `Runtime.getRuntime()` 打印 JVM 内存指标做健康监控"。

---

## 2. 对象创建过程（面试高频）

1. **类加载检查**：检查类是否已加载/解析/初始化。
2. **分配内存**：指针碰撞（堆规整）或空闲列表（堆不规整）。
3. **并发分配处理**：CAS + 失败重试，或 TLAB（线程本地分配缓冲）。
4. **初始化零值**：内存置 0。
5. **设置对象头**：hash、GC 分代年龄、锁标志等。
6. **执行 `<init>`**：构造方法。

**对象在内存中的布局**：对象头（Mark Word + 类型指针）+ 实例数据 + 对齐填充。

---

## 3. 判断对象是否可回收

- **引用计数法**：有循环引用问题，JVM 不用。
- **可达性分析**：从 **GC Roots** 出发，不可达的对象可回收。
  - GC Roots：栈中局部变量、静态变量、常量、JNI 引用、被 synchronized 持有的对象等。

**四种引用**：
| 引用 | 回收时机 |
|------|---------|
| 强引用 | 永不回收（除非不可达） |
| 软引用 `SoftReference` | 内存不足时回收，适合缓存 |
| 弱引用 `WeakReference` | 下次 GC 必回收，如 ThreadLocalMap 的 key |
| 虚引用 `PhantomReference` | 无法获取对象，仅用于回收通知 |

---

## 4. 垃圾回收算法

| 算法 | 思想 | 缺点 |
|------|------|------|
| 标记-清除 | 标记垃圾 → 清除 | 产生内存碎片 |
| 标记-复制 | 分两个半区，存活对象复制到另一半 | 浪费一半空间（新生代用，因存活率低） |
| 标记-整理 | 标记 → 存活对象向一端移动 | 效率低（老年代用） |

**分代收集**：新生代（Eden + 2 个 Survivor，8:1:1）用复制算法；老年代用标记-整理/清除。

**为什么新生代用复制、老年代用整理？**
> 新生代对象朝生夕死、存活率低，复制成本低；老年代存活率高、复制成本高，用整理避免碎片。

---

## 5. 垃圾收集器（重点记 CMS 和 G1）

| 收集器 | 区域 | 特点 |
|--------|------|------|
| Serial / Serial Old | 单线程 | 单线程，STW，客户端用 |
| ParNew | 新生代 | Serial 多线程版，配合 CMS |
| Parallel Scavenge / Parallel Old | 新生代/老年代 | 吞吐量优先（JDK8 默认） |
| **CMS** | 老年代 | 并发低停顿，标记-清除，有碎片，JDK9 废弃 |
| **G1** | 整堆 | 分 Region，可预测停顿，JDK9+ 默认 |
| ZGC | 整堆 | 极低停顿（<1ms），大堆 |

**G1 原理**：把堆划分为大小相等的 Region，用"可预测停顿时间模型"优先回收价值最大的 Region（Garbage First）。

**CMS 的四个阶段**：初始标记（STW）→ 并发标记 → 重新标记（STW）→ 并发清除。

---

## 6. 类加载机制

### 双亲委派模型
```
Bootstrap ClassLoader（加载 rt.jar）
   ↑
Extension/Platform ClassLoader（加载 ext 包）
   ↑
Application ClassLoader（加载 classpath）
   ↑
自定义类加载器
```
加载顺序：**自底向上询问是否已加载，自顶向下尝试加载**。好处：避免类重复加载，保证核心类安全（如自定义 `java.lang.String` 不会被加载）。

**打破双亲委派**：Tomcat 各应用独立 ClassLoader、SPI（线程上下文类加载器）、JDBC 驱动、热部署。

### 类加载的 7 个阶段
加载 → 验证 → 准备 → 解析 → 初始化 → 使用 → 卸载

### 触发初始化的 6 种情况
new 对象、反射、父类未初始化先初始化父类、main 类、MethodHandle、默认方法。

---

## 7. OOM 排查与调优（结合项目，必会）

### 常见 OOM 类型
1. `java.lang.OutOfMemoryError: Java heap space` — 堆溢出，对象太多/内存泄漏。
2. `OutOfMemoryError: Metaspace` — 类加载过多（动态代理、热部署）。
3. `StackOverflowError` — 递归过深（如实体双向关联序列化、无限递归调用）。
4. `OutOfMemoryError: unable to create new native thread` — 线程数超限。

### 排查步骤（背下来）
1. `jps` 找到进程 PID。
2. `jmap -dump:format=b,file=heap.hprof PID` 导出堆快照。
3. `jmap -heap PID` 查看堆配置与使用。
4. `jstat -gcutil PID 1000` 实时看 GC 情况。
5. `jstack PID` 看线程栈，排查死锁、阻塞。
6. 用 MAT / JProfiler 分析 dump 文件，找大对象和 GC Roots。

### 常用 JVM 参数
```
-Xms512m -Xmx512m     初始/最大堆
-XX:MetaspaceSize     元空间
-XX:+UseG1GC          使用 G1
-Xss1m                栈大小
-XX:+HeapDumpOnOutOfMemoryError   OOM 自动 dump
```

### 结合项目
项目中 `CommandLineRunnerUtils` 打印了堆内存信息，就是做**启动时资源健康检查**的思路。你可以说：
> 我在项目启动钩子里打印了 `maxMemory/totalMemory/freeMemory`，用于观察 JVM 堆内存分配和 GC 压力，方便后续配合 `-Xms/-Xmx` 调优。

---

## 附：高频面试题速答

1. **JVM 内存分哪些区域？** — 程序计数器、虚拟机栈、本地方法栈、堆、方法区（元空间）。
2. **哪些区域会 OOM？** — 除程序计数器外都可能。
3. **如何判断对象可回收？** — 可达性分析，从 GC Roots 出发。
4. **Minor GC / Major GC / Full GC？** — 新生代 / 老年代 / 整个堆+方法区。
5. **对象什么时候进入老年代？** — ① 大对象直接进；② 年龄超过阈值（默认 15）；③ Survivor 中同龄对象超一半；④ 动态年龄判定。
6. **双亲委派机制的好处？** — 避免重复加载、保证核心类安全。
7. **Full GC 频繁怎么排查？** — 查是否有大对象、内存泄漏、是否频繁创建大对象，用 jstat/jmap 分析。
8. **为什么说 Minor GC 很快？** — 新生代小、复制算法、STW 短。
9. **内存泄漏和内存溢出的区别？** — 泄漏是对象不再使用但无法回收（可达性仍存在），最终导致溢出。
10. **调优原则**：优先减少对象创建、合理设置堆大小、选合适收集器，而非盲目堆参数。
