package com.ruoyi.asset.utils;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * Java 面试 多线程 — 线程池详解
 * <p>
 * 一、线程池的创建方式
 * 在Java中，线程池的创建主要有两种方式：
 * 第一种，使用Executors工厂类创建
 * 第二种是使用ThreadPoolExecutor手动配置
 * <p/>
 *
 * 二、为什么不推荐使用Executors创建线程池？
 * 1、Executors.newFixedThreadPool 和 Executors.newSingleThreadExecutor 使用的任务队列是
 *    LinkedBlockingQueue，默认容量为Integer.MAX_VALUE，相当于无界队列，高并发下可能堆积
 *    大量请求导致OOM。
 * 2、Executors.newCachedThreadPool 允许的最大线程数为Integer.MAX_VALUE，可能创建大量线程
 *    导致OOM。
 * 3、Executors.newScheduledThreadPool 同样使用无界队列，存在OOM风险。
 * 因此，阿里巴巴Java开发手册强制要求：线程池不允许使用Executors去创建，而是通过
 * ThreadPoolExecutor的方式，让开发人员更加明确线程池的运行规则，规避资源耗尽的风险。
 * <p/>
 *
 * 三、ThreadPoolExecutor的核心参数：
 * 1、corePoolSize
 * （作用）核心线程数，线程池长期保持的最小线程数，即使线程空闲也不会被回收，除非设置了
 *        allowCoreThreadTimeOut(true)。
 * （配置建议）CPU密集型任务设为CPU核心数，I/O密集型任务设为CPU核心数的2倍（因线程等待I/O时
 *           释放CPU）。公式参考：N_CPU + 1 或 N_CPU * 2。
 * （典型场景）高并发I/O任务，如Web服务器、数据库中间件。
 *
 * 2、maximumPoolSize
 * （作用）最大线程数，线程池允许的最大并发线程数。当队列满了之后，会创建新线程直到达到
 *        maximumPoolSize。
 * （配置建议）根据系统资源，如内存、CPU，和任务类型调整，避免资源耗尽。一般设为
 *           corePoolSize + 预留突发量。
 * （典型场景）突发流量，如秒杀、促销活动。
 *
 * 3、keepAliveTime
 * （作用）非核心线程空闲存活时间。当线程数超过corePoolSize，多余的空闲线程在等待超过
 *        keepAliveTime后会被回收。
 * （配置建议）结合任务间隔设置，如60s，及时回收空闲线程。
 * （典型场景）低频任务，如定时任务。
 *
 * 4、unit
 * （作用）keepAliveTime的时间单位。
 * （配置建议）常用 TimeUnit.SECONDS、TimeUnit.MILLISECONDS、TimeUnit.MINUTES。
 * （典型场景）与业务需求匹配。
 *
 * 5、workQueue
 * （作用）任务队列，存放待执行的任务。当线程数达到corePoolSize后，新任务会放入队列等待。
 * （配置建议）优先使用有界队列，如LinkedBlockingQueue，避免内存溢出，需设置合理容量。
 *           同步移交队列SynchronousQueue不存储任务，直接交给线程处理，适合高响应场景但
 *           需配合较大的maximumPoolSize。优先级队列PriorityBlockingQueue支持任务排序。
 * （典型场景）任务量波动较大的场景。
 *
 * 6、threadFactory
 * （作用）线程工厂，用于创建线程。
 * （配置建议）自定义线程名称、优先级、是否为守护线程等，便于监控和调试。
 * （典型场景）需追踪线程来源的生产环境。
 *
 * 7、handler
 * （作用）拒绝策略，当队列满且线程数达到maximumPoolSize时的处理方式。
 * （配置建议）根据业务需求选择策略：
 *           AbortPolicy（默认）：丢弃任务并抛出RejectedExecutionException，适合必须成功
 *                              的场景，让调用方感知并处理。
 *           CallerRunsPolicy：由调用线程执行任务，不会丢任务但可能阻塞主线程，适合不允
 *                            许丢任务的场景。
 *           DiscardPolicy：默默丢弃任务，不抛异常，适合不重要的任务如日志上报。
 *           DiscardOldestPolicy：丢弃队列中最旧的任务，重新提交新任务，适合关注最新数据
 *                               的场景。
 * （典型场景）高负载下的容错处理。
 * <p/>
 *
 * 四、线程池执行流程（面试高频）：
 * 1、提交任务，判断核心线程数是否已满：
 *    - 未满：创建核心线程执行任务
 *    - 已满：进入步骤2
 * 2、判断任务队列是否已满：
 *    - 未满：任务加入队列等待执行
 *    - 已满：进入步骤3
 * 3、判断最大线程数是否已满：
 *    - 未满：创建非核心线程执行任务
 *    - 已满：进入步骤4
 * 4、执行拒绝策略
 * <p/>
 *
 * 五、线程池大小配置公式：
 * CPU密集型：corePoolSize = N_CPU + 1（加1是为了防止线程偶发缺页中断导致的CPU空转）
 * I/O密集型：corePoolSize = N_CPU * (1 + 平均等待时间/平均计算时间)
 *           简化公式：corePoolSize = N_CPU * 2
 * 混合型：根据具体任务的CPU和I/O占比动态调整
 * <p/>
 *
 * 六、线程池状态：
 * RUNNING（运行中）：接受新任务，处理队列中的任务
 * SHUTDOWN（关闭中）：不接受新任务，但处理队列中的任务（调用shutdown()）
 * STOP（停止）：不接受新任务，不处理队列中的任务，中断正在执行的任务（调用shutdownNow()）
 * TIDYING（整理中）：所有任务已终止，workerCount为0，准备调用terminated()
 * TERMINATED（已终止）：terminated()方法执行完毕
 * <p/>
 *
 * 七、使用线程池的注意事项：
 * 1、任务完成后需要调用shutdown()或shutdownNow()关闭线程池，否则JVM不会退出
 * 2、线程池不能使用Executors创建，必须通过ThreadPoolExecutor手动配置
 * 3、建议为线程池设置有意义的名称前缀，便于排查问题
 * 4、任务中捕获异常，否则异常会被吞掉（submit需要get获取异常，execute会直接抛出）
 * 5、合理配置队列大小，防止任务堆积导致OOM
 * 6、生产环境建议使用Spring的ThreadPoolTaskExecutor，支持@Async注解
 */
public class ThreadPoolExecutorUtils {

    /**
     * CPU核心数，用于计算线程池大小
     */
    private static final int CPU_CORES = Runtime.getRuntime().availableProcessors();

    /**
     * 核心线程数
     * CPU密集型：CPU_CORES + 1
     * I/O密集型：CPU_CORES * 2
     * 当前配置为I/O密集型场景
     */
    int corePoolSize = CPU_CORES * 2;

    /**
     * 最大线程数
     * 在核心线程数基础上预留3个突发流量处理的线程
     */
    int maximumPoolSize = CPU_CORES * 2 + 3;

    /**
     * 非核心线程空闲存活时间，单位秒
     * 超过核心线程数的空闲线程在60秒后被回收
     */
    long keepAliveTime = 60L;

    /**
     * keepAliveTime的时间单位
     */
    TimeUnit unit = TimeUnit.SECONDS;

    /**
     * 任务队列：
     * （推荐）有界队列，如LinkedBlockingQueue，防止内存溢出，但需设置合理容量。
     * 无界队列，如SynchronousQueue，使用任务直接交接的场景，但可能引发OOM。
     * 容量100：当核心线程数满后，最多可积压100个任务等待执行
     */
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(100);

    /**
     * 线程工厂
     * 自定义线程名称，格式：custom-thread-{UUID}
     * 便于在日志和jstack中快速定位线程来源
     */
    ThreadFactory threadFactory = r -> {
        Thread thread = new Thread(r);
        thread.setName("custom-thread-" + UUID.randomUUID());
        return thread;
    };

    /**
     * 拒绝策略：
     * AbortPolicy（默认）：丢弃并抛出RejectedExecutionException
     * CallerRunsPolicy：由调用线程（提交任务的主线程）直接执行该任务
     * DiscardPolicy：默默丢弃任务，不抛异常
     * DiscardOldestPolicy：丢弃队列中最旧的任务，重新提交当前任务
     */
    RejectedExecutionHandler abortPolicy = new ThreadPoolExecutor.AbortPolicy();
    RejectedExecutionHandler callerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();
    RejectedExecutionHandler discardPolicy = new ThreadPoolExecutor.DiscardPolicy();
    RejectedExecutionHandler discardOldestPolicy = new ThreadPoolExecutor.DiscardOldestPolicy();

    /**
     * 线程池实例
     * 使用手动配置的ThreadPoolExecutor，避免Executors工厂类的OOM风险
     */
    ExecutorService executorService = new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            unit,
            workQueue,
            threadFactory,
            callerRunsPolicy
    );

    /**
     * 演示：使用 execute() 提交无返回值任务
     * execute 提交的任务不会返回结果，异常会直接抛出
     */
    public void method1() {
        // 方式一：直接传入 Runnable 匿名内部类
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " -> CCTV-14 熊出没 动画片即将开始…… 倒计时：");
                for (int i = 10; i >= 0; i--) {
                    System.out.println(Thread.currentThread().getName() + " -> " + i);
                }
                System.out.println(Thread.currentThread().getName() + " -> （music）冬眠假期刚刚结束，我还有点小糊涂，鸟儿在山坡……");
            }
        });
    }

    /**
     * 演示：使用 submit() 提交有返回值任务
     * submit 返回 Future 对象，可以通过 get() 获取结果
     */
    public void method2() {
        Future<String> future = executorService.submit(() -> {
            Thread.sleep(1000);
            return "熊出没播放完毕";
        });
        try {
            // get() 会阻塞直到任务完成
            String result = future.get();
            System.out.println(Thread.currentThread().getName() + " -> 任务结果：" + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示：同时提交多个任务，观察线程复用
     * 多个任务由线程池中的线程交替执行，不会每次都创建新线程
     */
    public void method3() {
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executorService.execute(() -> {
                System.out.println(Thread.currentThread().getName() + " -> 正在播放第 " + taskId + " 集");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " -> 第 " + taskId + " 集播放完毕");
            });
        }
    }

    /**
     * 演示：使用 CountDownLatch 等待所有任务完成
     * 适用于需要等待多个异步任务全部完成后再继续的场景
     */
    public void method4() throws InterruptedException {
        int taskCount = 3;
        CountDownLatch latch = new CountDownLatch(taskCount);
        for (int i = 1; i <= taskCount; i++) {
            final int taskId = i;
            executorService.execute(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " -> 任务 " + taskId + " 开始");
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " -> 任务 " + taskId + " 完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        System.out.println("主线程等待所有子任务完成...");
        latch.await();
        System.out.println("所有任务已完成，主线程继续执行");
    }

    /**
     * 演示：使用 CompletableFuture 编排异步任务（JDK 8）
     * 支持任务串行、并行、组合等高级操作
     */
    public void method5() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getName() + " -> 第一步：查询熊出没剧集信息");
                    return "熊出没第10季";
                }, executorService)
                .thenApplyAsync(season -> {
                    System.out.println(Thread.currentThread().getName() + " -> 第二步：根据 " + season + " 加载视频流");
                    return season + " 视频流已就绪";
                }, executorService)
                .thenApplyAsync(stream -> {
                    System.out.println(Thread.currentThread().getName() + " -> 第三步：" + stream + "，开始播放");
                    return "播放成功";
                }, executorService);
        System.out.println(Thread.currentThread().getName() + " -> 最终结果：" + future.get());
    }

    /**
     * 完整演示：使用线程池执行任务并优雅关闭
     */
    public void fullDemo() throws InterruptedException {
        System.out.println("-------- 线程池完整演示开始 --------");
        System.out.println("CPU核心数：" + CPU_CORES);
        System.out.println("核心线程数：" + corePoolSize);
        System.out.println("最大线程数：" + maximumPoolSize);
        System.out.println();

        // 1. 提交多个任务
        System.out.println("--- 1. 提交5个任务，观察线程复用 ---");
        method3();
        Thread.sleep(3000);

        // 2. 使用 CountDownLatch 等待所有任务
        System.out.println("\n--- 2. CountDownLatch 等待所有任务完成 ---");
        method4();

        // 3. 使用 submit 获取返回值
        System.out.println("\n--- 3. submit 获取任务返回值 ---");
        method2();

        // 4. CompletableFuture 异步编排
        System.out.println("\n--- 4. CompletableFuture 异步任务编排 ---");
        try {
            method5();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // 5. 关闭线程池
        System.out.println("\n--- 5. 关闭线程池 ---");
        executorService.shutdown();
        if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            System.out.println("线程池已成功关闭");
        } else {
            executorService.shutdownNow();
            System.out.println("线程池超时强制关闭");
        }
        System.out.println("-------- 线程池完整演示结束 --------");
    }

    public static void main(String[] args) throws InterruptedException {
        new ThreadPoolExecutorUtils().fullDemo();
    }
}