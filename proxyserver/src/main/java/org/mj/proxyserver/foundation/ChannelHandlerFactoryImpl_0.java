package org.mj.proxyserver.foundation;

import io.netty.channel.ChannelHandler;
import org.mj.comm.NettyClient;

/**
 * 内部服务器信道处理器工厂
 */
public class ChannelHandlerFactoryImpl_0 extends NettyClient.Config.AbstractChannelHandlerFactory {
    @Override
    public ChannelHandler createMsgHandler() {
        return new InternalMsgHandler();
    }
}
