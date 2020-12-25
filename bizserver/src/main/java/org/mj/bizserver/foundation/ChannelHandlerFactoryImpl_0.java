package org.mj.bizserver.foundation;

import io.netty.channel.ChannelHandler;
import org.mj.comm.NettyServer;

/**
 * 内部服务器信道处理器工厂
 */
public final class ChannelHandlerFactoryImpl_0 extends NettyServer.Config.AbstractChannelHandlerFactory {
    @Override
    public ChannelHandler createMsgHandler() {
        return new InternalMsgHandler();
    }
}
