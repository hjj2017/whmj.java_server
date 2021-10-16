package org.mj.comm.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步操作处理器
 */
public final class AsyncOperationProcessor {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    /**
     * 线程服务数组
     */
    private final ExecutorService[] _esArray;

    /**
     * 类默认构造器
     */
    public AsyncOperationProcessor() {
        this(null, 0);
    }

    /**
     * 类参数构造器
     *
     * @param processorName 处理器名称
     * @param threadCount   线程数量
     */
    public AsyncOperationProcessor(String processorName, int threadCount) {
        if (null == processorName) {
            processorName = "AsyncOperationProcessor";
        }

        if (threadCount <= 0) {
            threadCount = Runtime.getRuntime().availableProcessors() * 2;
        }

        _esArray = new ExecutorService[threadCount];

        for (int i = 0; i < threadCount; i++) {
            // 线程名称
            final String threadName = processorName + "[" + i + "]";
            // 创建单线程服务
            _esArray[i] = Executors.newSingleThreadExecutor((r) -> {
                // 创建线程并起个名字
                Thread t = new Thread(r);
                t.setName(threadName);
                return t;
            });
        }
    }

    /**
     * 处理异步操作,
     * 1、通过 bindId 来选择一个异步线程;
     * 2、执行 op 操作;
     * 3、op 执行完成之后将 co 扔给 exec 线程继续执行；
     *
     * @param bindId 绑定 ( 线程 ) Id
     * @param op     异步操作对象
     * @param exec   同步执行线程
     * @param co     同步继续执行
     */
    public void process(int bindId, IAsyncOperation op, Executor exec, IContinueWith co) {
        if (null == op) {
            return;
        }

        // 根据绑定 Id 获取线程索引
        bindId = Math.abs(bindId);
        int esIndex = bindId % _esArray.length;

        _esArray[esIndex].submit(() -> {
            try {
                // 执行异步操作
                op.doAsync();

                if (null != exec &&
                    null != co) {
                    // 回到主消息线程继续执行完成逻辑
                    exec.execute(co::doContinue);
                }
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }

    /**
     * 处理异步操作
     *
     * @param bindId 绑定 ( 线程 ) Id
     * @param op     异步操作
     */
    public void process(int bindId, IAsyncOperation op) {
        process(bindId, op, null, null);
    }
}
