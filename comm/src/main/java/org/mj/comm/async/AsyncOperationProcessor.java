package org.mj.comm.async;

import org.mj.comm.MainThreadProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
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
     * 主线程处理器
     */
    private final MainThreadProcessor _mainTP;

    /**
     * 随机对象
     */
    private Random _rand;

    /**
     * 类参数构造器
     *
     * @param processorName 处理器名称
     * @param threadNum     线程数量
     * @param mainTP        主线程处理器
     * @throws IllegalArgumentException if null == mainTP
     */
    public AsyncOperationProcessor(String processorName, int threadNum, MainThreadProcessor mainTP) {
        if (null == mainTP) {
            throw new IllegalArgumentException("mainTP is null");
        }

        if (null == processorName) {
            processorName = "AsyncOperationThread";
        }

        if (threadNum <= 0) {
            threadNum = 8;
        }

        _esArray = new ExecutorService[threadNum];

        for (int i = 0; i < threadNum; i++) {
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

        _mainTP = mainTP;
    }

    /**
     * 处理异步操作
     *
     * @param bindId 绑定 ( 线程 ) Id
     * @param op     异步操作对象
     * @param con    同步继续执行
     */
    public void process(int bindId, IAsyncOperation op, IContinueWith con) {
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

                if (null != con) {
                    // 回到主消息线程继续执行完成逻辑
                    _mainTP.process(con::doContinue);
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
        process(bindId, op, null);
    }

    /**
     * 处理异步操作
     *
     * @param op  异步操作
     * @param con 同步继续执行
     */
    public void process(IAsyncOperation op, IContinueWith con) {
        if (null == op) {
            return;
        }

        if (null == _rand) {
            synchronized (this) {
                if (null == _rand) {
                    _rand = new Random();
                }
            }
        }

        int bindId = _rand.nextInt(_esArray.length);

        process(
            bindId, op, con
        );
    }

    /**
     * 处理异步操作
     *
     * @param op 异步操作
     */
    public void process(IAsyncOperation op) {
        process(op, null);
    }
}
