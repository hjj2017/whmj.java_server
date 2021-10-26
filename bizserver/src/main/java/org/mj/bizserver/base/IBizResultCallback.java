package org.mj.bizserver.base;

/**
 * 业务结果回调接口
 *
 * @param <T> 最终结果类型
 */
@FunctionalInterface
public interface IBizResultCallback<T> {
    /**
     * 执行回调函数
     *
     * @param resultX 业务结果
     */
    void apply(BizResultWrapper<T> resultX);
}
