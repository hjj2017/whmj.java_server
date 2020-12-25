package org.mj.bizserver.foundation;

import org.mj.comm.async.AsyncOperationProcessor;
import org.mj.comm.async.IAsyncOperation;
import org.mj.comm.async.IContinueWith;

/**
 * 异步操作处理器单例类
 */
public final class AsyncOperationProcessorSingleton {
    /**
     * 单例对象
     */
    static private final AsyncOperationProcessorSingleton _instance = new AsyncOperationProcessorSingleton();

    /**
     * 异步操作处理器
     */
    private final AsyncOperationProcessor _asyncOP;

    /**
     * 类默认构造器
     */
    private AsyncOperationProcessorSingleton() {
        _asyncOP = new AsyncOperationProcessor(
            "mjAsync",
            8, // 线程数
            MainThreadProcessorSingleton.getInstance().getActualMainTP()
        );
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public AsyncOperationProcessorSingleton getInstance() {
        return _instance;
    }

    /**
     * 处理异步操作
     *
     * @param bindId 绑定 ( 线程 ) Id
     * @param op     异步操作对象
     * @param con    同步继续执行
     */
    public void process(int bindId, IAsyncOperation op, IContinueWith con) {
        _asyncOP.process(bindId, op, con);
    }

    /**
     * 处理异步操作
     *
     * @param bindId 绑定 ( 线程 ) Id
     * @param op     异步操作对象
     */
    public void process(int bindId, IAsyncOperation op) {
        _asyncOP.process(bindId, op);
    }

    /**
     * 处理异步操作
     *
     * @param op  异步操作对象
     * @param con 同步继续执行
     */
    public void process(IAsyncOperation op, IContinueWith con) {
        _asyncOP.process(op, con);
    }

    /**
     * 处理异步操作
     *
     * @param op 异步操作对象
     */
    public void process(IAsyncOperation op) {
        _asyncOP.process(op);
    }
}
