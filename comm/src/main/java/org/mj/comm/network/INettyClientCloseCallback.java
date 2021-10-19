package org.mj.comm.network;

/**
 * Netty 客户端关闭回调接口
 */
@FunctionalInterface
public interface INettyClientCloseCallback {
    /**
     * 执行回调
     *
     * @param closeClient 关闭客户端
     */
    void apply(NettyClient closeClient);
}
