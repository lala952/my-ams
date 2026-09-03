# 08 · Redis 缓存

> 你的项目里有 `CacheClient`、`RedisIdWorker` 两个"黄金代码"，缓存三兄弟、分布式锁、分布式 ID 全都能结合项目讲，这是你最大的加分项。

---

## 1. Redis 是什么？为什么快？

- 基于**内存**存储的 key-value 数据库。
- 快的原因：① 纯内存操作；② 单线程 + IO 多路复用（避免上下文切换和锁竞争）；③ 高效的数据结构。

**追问：Redis 6.0 为什么引入多线程？**
> 只是**网络 IO 读写**多线程，命令执行仍是单线程（避免数据竞争）。

---

## 2. 常用数据结构及应用场景

| 类型 | 底层结构 | 应用场景 |
|------|---------|---------|
| String | SDS | 缓存、计数、分布式锁、分布式 ID |
| Hash | 哈希表/压缩列表 | 对象缓存、购物车 |
| List | 双向链表/quicklist | 消息队列、最新列表 |
| Set | 哈希表/intset | 去重、共同好友、抽奖 |
| ZSet | 跳表 | 排行榜、延迟队列 |

**结合项目**：`CacheClient` 用 String 存 JSON 缓存；`RedisIdWorker` 用 String 的 `increment` 做自增序列号。

---

## 3. 缓存穿透 / 击穿 / 雪崩（必考，项目有完整实现）

### 3.1 缓存穿透
- **定义**：查询**根本不存在**的数据，缓存和 DB 都没有，每次都打到 DB。
- **解决**：
  1. **缓存空对象**：DB 查到 null 也缓存一个空值（短 TTL）。
  2. **布隆过滤器**：先判断 key 是否可能存在。
- **结合项目**：`CacheClient.queryWithPassThrough` 就是**空值缓存**实现——DB 查不到时 `set(key, "", CACHE_NULL_TTL, MINUTES)` 缓存空串，下次命中空值直接返回"数据不存在"，不再打 DB。

### 3.2 缓存击穿
- **定义**：某个**热点 key 过期**，大量请求同时打到 DB。
- **解决**：
  1. **互斥锁**：只让一个线程查 DB 重建缓存，其他线程等待/重试。
  2. **逻辑过期**：不设置 TTL，值里存逻辑过期时间，过期后异步重建，返回旧值。
- **结合项目**：
  - `queryWithMutex` 用 `setIfAbsent`（`SETNX`）加**互斥锁**，获取锁失败的线程 sleep 50ms 后重试，保证只有一个线程回源 DB。
  - `queryWithLogicalExpire` 用**逻辑过期**：值包装成 `RedisData{data, expireTime}`，过期后开独立线程重建缓存，当前请求先返回旧值，实现"非阻塞"重建。

### 3.3 缓存雪崩
- **定义**：大量 key **同时过期**或 Redis **宕机**，请求全部打到 DB。
- **解决**：
  1. 过期时间加**随机值**，避免同时过期。
  2. **Redis 高可用**：主从 + 哨兵 / 集群。
  3. **降级/限流**：Redis 挂了走降级。
- **结合项目**：`ChangeServiceImpl` 里有"**Redis 异常或无数据时自动降级到数据库直查**"的逻辑，这就是雪崩/故障时的兜底方案。

### 3.4 面试万能话术
> 我的缓存方案是参考经典做法实现的：**穿透用空值缓存，击穿用互斥锁（保证一致性）或逻辑过期（保证可用性）双方案，雪崩用随机 TTL + 高可用 + 降级**。核心代码在 `CacheClient`，三个方法分别对应三种场景。

---

## 4. 缓存一致性（缓存与数据库双写）

### 4.1 双写方案对比
| 方案 | 说明 | 问题 |
|------|------|------|
| 先删缓存再更新 DB | 删缓存 → 更新 DB | 更新期间可能读到旧值写回缓存 |
| 先更新 DB 再删缓存 | 更新 DB → 删缓存 | 删除失败导致不一致（需重试） |
| **延迟双删** | 删缓存 → 更新 DB → 延迟再删 | 推荐，降低脏数据窗口 |

### 4.2 最终一致性方案
- **Canal 监听 binlog**：DB 变更 → Canal 解析 → 异步删缓存。
- **消息队列**：更新 DB 后发 MQ 异步删缓存。

**面试建议**：说"读多写少用先更新 DB 再删缓存 + 失败重试/延迟双删，追求强一致用 Canal + binlog"。

---

## 5. 分布式锁（项目 `tryLock` 用 SETNX）

### 5.1 实现方式
```java
// 加锁：SET key value NX EX 10
boolean lock = redisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
// 解锁：删除 key（需判断 value 是否是自己的，防误删）
redisTemplate.delete(key);
```

### 5.2 三个核心问题
1. **误删别人的锁**：value 用唯一标识（UUID+线程ID），解锁前判断 `value == 自己`，用 **Lua 脚本**保证"判断+删除"原子性。
2. **锁过期导致并发**：设置合理 TTL，或用 **Redisson 看门狗**自动续期。
3. **Redisson vs 自己用 SETNX**：Redisson 封装了可重入锁、看门狗续期、红锁，生产推荐。

### 5.3 结合项目
`CacheClient.tryLock` 用 `setIfAbsent(key, "1", 10s)` 加锁，`unlock` 删 key。你可以诚实指出：
> 项目里 `CacheClient` 的互斥锁是最简版 SETNX 实现，生产更严谨的做法是用 Redisson 的可重入锁 + Lua 解锁 + 看门狗续期，避免锁误删和过期问题。

---

## 6. 分布式 ID 生成（项目 `RedisIdWorker`）

### 6.1 常见方案
| 方案 | 优点 | 缺点 |
|------|------|------|
| UUID | 简单 | 无序、长、字符串 |
| 数据库自增 | 有序 | 单点、性能瓶颈 |
| 雪花算法 | 有序、高性能 | 依赖时钟 |
| **Redis 自增** | 简单、有序 | 依赖 Redis |

### 6.2 项目实现解析
```java
long timestamp = nowSecond - BEGIN_TIMESTAMP;   // 相对时间戳，减小数值
long count = redisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date); // 按天自增
return timestamp << 32 | count;                  // 时间戳左移32位 + 序列号
```
- 思想：**时间戳（高 32 位） + Redis 自增序列号（低 32 位）**，拼接成全局唯一 ID。
- 按天加 key 前缀 `icr:业务:yyyy:MM:dd`，序列号每天重置，避免单 key 无限增长。
- Redis `increment` 是原子命令，天然线程安全。

### 6.3 面试讲法
> 我实现了一个基于 Redis 自增的分布式 ID 生成器 `RedisIdWorker`：用相对时间戳左移 32 位，再拼接 Redis `increment` 生成的按天序列号，得到全局唯一、趋势递增的 ID。相比雪花算法更简单，且能保证单号有序。

---

## 7. Redis 持久化

| 方式 | 原理 | 优点 | 缺点 |
|------|------|------|------|
| RDB | 定时快照（fork 子进程写二进制文件） | 恢复快、文件小 | 可能丢最后一次快照后的数据 |
| AOF | 追加写命令日志 | 丢数据少（可每秒刷） | 文件大、恢复慢 |
| 混合 | RDB + AOF | 兼顾 | — |

**RDB 的触发**：`save`（阻塞）、`bgsave`（fork 子进程）、`save 900 1` 等自动触发。

---

## 8. Redis 过期删除策略与内存淘汰

- **过期删除**：惰性删除（访问时检查）+ 定期删除（周期抽样）。
- **内存淘汰策略**：
  - `noeviction`（默认，不淘汰报错）
  - `allkeys-lru`（推荐，LRU 淘汰）
  - `volatile-lru`（只淘汰设了 TTL 的）
  - `allkeys-lfu` / `volatile-lfu`（LFU）

---

## 9. 缓存预热与降级

- **预热**：启动时把热点数据加载进 Redis（可用 `CommandLineRunner`/`ApplicationRunner` 实现）。
- **降级**：Redis 不可用时直接查 DB，避免缓存层拖垮业务（项目 `ChangeServiceImpl` 的 Redis 降级就是）。

---

## 附：高频面试题速答

1. **Redis 是单线程吗？** — 命令执行单线程，6.0 后网络 IO 多线程。
2. **Redis 的 value 能多大？** — 单个 value 最大 512MB。
3. **怎么用 Redis 做排行榜？** — ZSet，`ZADD` 排序、`ZREVRANGE` 取 TopN。
4. **怎么实现延迟队列？** — ZSet 用时间戳做 score，或 List + 阻塞，或用 Redis 5.0 Stream。
5. **Redis 和 MySQL 一致性怎么保证？** — 延迟双删 / Canal 监听 binlog / MQ。
6. **布隆过滤器原理？** — 多个哈希函数映射到位数组，判断"可能存在"（有误判，不会漏判）。
7. **Redisson 看门狗是什么？** — 自动给锁续期，防止业务没执行完锁就过期。
8. **Redis 主从复制原理？** — 全量同步（RDB）+ 增量同步（offset），异步复制可能丢数据。
9. **哨兵和集群区别？** — 哨兵解决高可用（主从切换），集群解决扩展（数据分片）。
10. **缓存击穿和穿透的区别？** — 击穿是热点 key 过期被集中打；穿透是查不存在的数据。
