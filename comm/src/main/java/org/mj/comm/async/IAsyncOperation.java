package org.mj.comm.async;

/**
 * 异步操作接口
 */
@FunctionalInterface
public interface IAsyncOperation {
    /**
     * 执行异步操作
     */
    void doAsync();
}
