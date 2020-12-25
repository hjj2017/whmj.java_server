package org.mj.comm.async;

/**
 * 异步操作之后回到主线程继续执行
 */
@FunctionalInterface
public interface IContinueWith {
    /**
     * 继续执行
     */
    void doContinue();
}
