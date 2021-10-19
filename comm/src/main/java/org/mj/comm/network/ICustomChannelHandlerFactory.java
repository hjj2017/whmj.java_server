package org.mj.comm.network;

import io.netty.channel.ChannelHandler;

/**
 * 自定义信道处理器工厂类
 */
@FunctionalInterface
public interface ICustomChannelHandlerFactory {
    /**
     * 创建自定义信道处理器
     *
     * @return 自定义信道处理器
     */
    ChannelHandler create();
}
