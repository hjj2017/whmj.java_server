package org.mj.proxyserver.foundation;

import io.netty.channel.ChannelHandler;
import org.mj.comm.NettyServer;

/**
 * 外部客户端信道处理器工厂,
 * XXX 注意: 这里是面向客户端的处理器逻辑
 */
public class ChannelHandlerFactoryImpl_1 extends NettyServer.Config.AbstractChannelHandlerFactory {
    @Override
    public ChannelHandler createMsgHandler() {
        // 处理客户端的 IO 过程
        return new ClientMsgHandler();
    }
}
