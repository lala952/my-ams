# 07 · MySQL 与 MyBatis

> 数据库是后端面试必考，索引、事务隔离、锁、SQL 优化四件套必须滚瓜烂熟。

---

## 1. 索引（最核心，必考）

### 1.1 索引是什么？为什么快？
索引是一种**有序的数据结构**，帮助 MySQL 高效获取数据。InnoDB 默认用 **B+ 树**。

### 1.2 B+ 树 vs B 树 vs 红黑树 vs Hash
- **B+ 树**：非叶子节点只存 key（不存数据），叶子节点存所有数据且**有序链表连接**，支持范围查询，磁盘 IO 少（树矮）。
- **B 树**：非叶子也存数据，IO 次数相对多，范围查询不如 B+。
- **红黑树**：二叉，树太高，磁盘 IO 多，不适合磁盘。
- **Hash**：等值查询 O(1)，但不支持范围、排序，有碰撞。

**为什么 InnoDB 用 B+ 树**：树矮（减少磁盘 IO）、叶子链表（范围查询快）、非叶子不存数据（一个节点能存更多 key）。

### 1.3 聚簇索引 vs 非聚簇索引（二级索引）
- **聚簇索引**：叶子节点存**整行数据**。每表一个，通常是主键。
- **二级索引**：叶子节点存**主键值**，回表查询整行。
- **回表**：二级索引找到主键 → 再到聚簇索引查整行。
- **覆盖索引**：查询列都在二级索引里，无需回表，`Extra` 显示 `Using index`。

### 1.4 最左前缀原则
联合索引 `(a, b, c)`，查询必须从最左列 a 开始，且不能跳过中间列。以下能命中：`a`、`a,b`、`a,b,c`；`b`、`b,c` 不能命中（除非覆盖索引）。

### 1.5 索引失效的场景（必背）
1. 对索引列使用**函数或运算**（`WHERE YEAR(create_time)=2026`）。
2. 隐式类型转换（`varchar` 列用数字查）。
3. `LIKE '%xxx'` 前导模糊。
4. 违反**最左前缀**。
5. `OR` 连接非索引列。
6. `!=`、`<>`、`NOT IN`、`IS NULL` 等在某些情况下不走。
7. 联合索引范围查询后，后续列失效（`a = 1 AND b > 2 AND c = 3`，c 失效）。

### 1.6 结合项目
资产变动列表查询 `selectChangeList` 支持按变动类型、业务状态、申请人、时间范围多条件查询，这些字段应该建索引。你可以说：
> 资产变动单表数据量大，我在 `change_no`（单号）、`business_status`（业务状态）、`create_by`（申请人）、`create_time`（时间）上建了联合/单列索引，查询走最左前缀。用 `EXPLAIN` 看执行计划，确认 `type` 是 `ref` 或 `range`，避免全表扫描。

---

## 2. 事务与 ACID

- **原子性 A**：undo log 回滚。
- **一致性 C**：由其他三者保证。
- **隔离性 I**：MVCC + 锁。
- **持久性 D**：redo log。

---

## 3. 事务隔离级别与并发问题

| 并发问题 | 说明 |
|---------|------|
| 脏读 | 读到别的事务未提交的数据 |
| 不可重复读 | 同事务两次读结果不同（被别的事务 update） |
| 幻读 | 同事务两次读的行数不同（被别的事务 insert/delete） |

| 隔离级别 | 脏读 | 不可重复读 | 幻读 |
|---------|------|-----------|------|
| 读未提交 | × | × | × |
| 读已提交 | ✓ | × | × |
| 可重复读（MySQL默认） | ✓ | ✓ | 基本✓（MVCC+间隙锁） |
| 串行化 | ✓ | ✓ | ✓ |

**追问：MySQL 默认隔离级别为什么是可重复读？**
> 历史原因：早期 MySQL binlog 是 statement 格式，读已提交会导致主从数据不一致，所以默认可重复读。

---

## 4. MVCC 多版本并发控制

### 4.1 原理
- 每条记录有两个隐藏列：`trx_id`（最近修改事务ID）、`roll_pointer`（指向 undo log 版本链）。
- **ReadView**：读时生成快照，判断版本可见性，实现不加锁的一致性读（快照读）。

### 4.2 快照读 vs 当前读
- **快照读**：普通 `SELECT`，基于 MVCC，读历史版本。
- **当前读**：`SELECT ... FOR UPDATE`、`UPDATE`、`DELETE`、`INSERT`，读最新版本并加锁。

### 4.3 结合项目
> 资产变动审批时，查询资产状态用快照读（MVCC），而更新资产状态是当前读（加锁），避免并发审批导致资产状态被覆盖。同时用 `business_status` 状态机（草稿→审批中→完成/驳回）做乐观控制。

---

## 5. 锁

### 5.1 锁分类
- **按粒度**：全局锁、表锁、行锁、间隙锁。
- **按模式**：共享锁（S，读）、排他锁（X，写）。
- **行锁算法**：记录锁（锁单行）、间隙锁（Gap，锁区间，防幻读）、临键锁（Next-Key，记录+间隙）。

### 5.2 乐观锁 vs 悲观锁
- **乐观锁**：`version` 字段或时间戳，`UPDATE ... WHERE version = ?`，失败重试。适合冲突少。
- **悲观锁**：`SELECT ... FOR UPDATE`，加排他锁。适合冲突多。
- **结合项目**：资产库存扣减、资产状态流转可用乐观锁（版本号）防并发覆盖。

### 5.3 死锁排查
- 原因：交叉加锁、锁升级。
- 排查：`SHOW ENGINE INNODB STATUS` 看 `LATEST DETECTED DEADLOCK`。

---

## 6. SQL 优化（高频）

### 6.1 优化步骤
1. 用 `EXPLAIN` 看执行计划，关注 `type`（system > const > eq_ref > ref > range > index > ALL）。
2. 关注 `key`（用了哪个索引）、`rows`（扫描行数）、`Extra`（`Using filesort`、`Using temporary` 要优化）。
3. 避免 `SELECT *`，只查需要的列（利于覆盖索引）。
4. 大数据量分页优化：`LIMIT 100000, 10` 慢，用**延迟关联**或**记录上次主键**（`WHERE id > 上次id LIMIT 10`）。
5. 大表加索引、分库分表。

### 6.2 结合项目
> 资产列表大数据量分页，我用 PageHelper 分页 + 合理索引；统计接口用 `CompletableFuture` 并行查询，避免多次串行 DB 往返；批更新用 Guava 分段（每批 100 条）减少单次 SQL 过大。

---

## 7. MyBatis 与 MyBatis-Plus

### 7.1 MyBatis 工作原理
1. 加载配置文件/Mapper XML。
2. `SqlSessionFactory` 创建 `SqlSession`。
3. Mapper 接口通过**动态代理**绑定 SQL。
4. `Executor` 执行 SQL，`ResultHandler` 映射结果。

### 7.2 `#{}` 和 `${}` 的区别（必考）
| 对比 | `#{}` | `${}` |
|------|-------|-------|
| 处理 | 预编译占位符 `?` | 字符串直接拼接 |
| SQL 注入 | 安全 | 有风险 |
| 用途 | 值 | 表名、列名、排序等动态标识 |

### 7.3 一级缓存 / 二级缓存
- **一级缓存**：`SqlSession` 级别，默认开启。
- **二级缓存**：`Mapper` 命名空间级别，需配置，多表关联可能脏读。

### 7.4 MyBatis-Plus 特点
- 项目 `ChangeServiceImpl extends ServiceImpl<ChangeMapper, Change>`，用 MyBatis-Plus 的 `ServiceImpl` + `BaseMapper` 实现免写 XML 的基础 CRUD。
- 支持条件构造器 `LambdaQueryWrapper`、分页插件、逻辑删除、自动填充。

### 7.5 结合项目的讲法
> 项目用 **MyBatis-Plus** 做基础 CRUD（`ServiceImpl`/`BaseMapper`），复杂多表查询（如资产变动视图 `selectChangeList`）用自定义 XML 或注解 SQL，分页用 PageHelper。这样既享受了 MP 的开发效率，又保留了复杂 SQL 的灵活性。

---

## 附：高频面试题速答

1. **InnoDB 和 MyISAM 区别？** — InnoDB 支持事务、行锁、崩溃恢复（redo log），是默认引擎；MyISAM 不支持事务、表锁、查询快但并发差。
2. **主键自增和 UUID 哪个好？** — 自增好，聚簇索引顺序插入、无页分裂；UUID 无序导致页分裂、索引大。
3. **为什么用覆盖索引？** — 避免回表，减少磁盘 IO。
4. **一条 SQL 很慢怎么排查？** — EXPLAIN 看执行计划 → 判断是否走索引 → 加索引/改 SQL → 慢查询日志。
5. **count(*) 和 count(1)、count(字段) 区别？** — count(*) 和 count(1) 一样，统计行数；count(字段) 忽略 NULL。
6. **分库分表怎么分？** — 按业务分库，按主键哈希/范围分表，中间件 ShardingSphere。
7. **如何避免重复插入？** — 唯一索引 + `INSERT IGNORE` / `ON DUPLICATE KEY UPDATE`。
8. **MySQL 执行顺序？** — FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT。
9. **redo log 和 undo log 区别？** — redo 保证持久性（崩溃恢复），undo 保证原子性（回滚）+ MVCC。
10. **binlog 的三种格式？** — statement、row、mixed。
