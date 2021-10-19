package org.mj.bizserver.foundation;

import org.mj.comm.async.AsyncOperationProcessor;
import org.mj.comm.async.IAsyncOperation;
import org.mj.comm.async.IContinueWith;

import java.util.Random;
import java.util.concurrent.Executor;

/**
 * 异步操作处理器单例类
 */
public final class AsyncOperationProcessorSingleton {
    /**
     * 单例对象
     */
    static private final AsyncOperationProcessorSingleton INSTANCE = new AsyncOperationProcessorSingleton();

    /**
     * 随机对象
     */
    private volatile Random _rand;

    /**
     * 内置处理器
     */
    private final AsyncOperationProcessor _innerProcessor = new AsyncOperationProcessor(
        "bizServer_asyncOperationProcessor",
        -1 // 使用默认的线程数量
    );

    /**
     * 主线程执行器
     */
    private final Executor _mainThreadExecutor = MainThreadProcessorSingleton.getInstance()::process;

    /**
     * 私有化类默认构造器
     */
    private AsyncOperationProcessorSingleton() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public AsyncOperationProcessorSingleton getInstance() {
        return INSTANCE;
    }

    /**
     * 处理异步操作
     *
     * @param bindId 绑定 ( 线程 ) Id
     * @param op     异步操作对象
     * @param exec   同步执行线程
     * @param co     同步继续执行
     */
    public void process(int bindId, IAsyncOperation op, Executor exec, IContinueWith co) {
        _innerProcessor.process(bindId, op, exec, co);
    }

    /**
     * 处理异步操作,
     * 异步操作执行完成之后会回到主线程处理器继续执行
     *
     * @param bindId 绑定 ( 线程 ) Id
     * @param op     异步操作对象
     * @param co     ( 回到主线程 ) 同步继续执行
     */
    public void process_0(int bindId, IAsyncOperation op, IContinueWith co) {
        _innerProcessor.process(
            bindId, op, _mainThreadExecutor, co
        );
    }

    /**
     * 处理异步操作
     *
     * @param bindId 绑定 ( 线程 ) Id
     * @param op     异步操作对象
     */
    public void process(int bindId, IAsyncOperation op) {
        _innerProcessor.process(bindId, op);
    }

    /**
     * 执行一步操作
     *
     * @param op 异步操作对象
     */
    public void process(IAsyncOperation op) {
        if (null == _rand) {
            synchronized (this) {
                if (null == _rand) {
                    _rand = new Random();
                }
            }
        }

        // 随机一个 Id
        int bindId = _rand.nextInt(1024);
        // 执行异步操作
        process(bindId, op, null, null);
    }
}
