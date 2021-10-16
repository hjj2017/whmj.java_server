package org.mj.bizserver.foundation;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.comm.cmdhandler.AbstractCmdHandlerContext;

/**
 * 自定义命令处理器上下文
 */
public class MyCmdHandlerContext extends AbstractCmdHandlerContext {
    /**
     * 客户端信道
     */
    private final Channel _proxyServerCh;

    /**
     * 类参数构造器
     *
     * @param gatewayServerCh 网关服务器信道
     */
    MyCmdHandlerContext(Channel gatewayServerCh) {
        super();
        _proxyServerCh = gatewayServerCh;
    }

    @Override
    public ChannelFuture writeAndFlush(Object msgObj) {
        if (!(msgObj instanceof GeneratedMessageV3) ||
            null == _proxyServerCh ||
            !_proxyServerCh.isWritable()) {
            return null;
        }

        // 获取协议消息
        GeneratedMessageV3 protobufMsg = (GeneratedMessageV3) msgObj;

        InternalServerMsg innerMsg = new InternalServerMsg()
            .setProxyServerId(getProxyServerId())
            .setRemoteSessionId(getRemoteSessionId())
            .setClientIP(getClientIP())
            .setFromUserId(getFromUserId())
            .setMsgCode(MsgRecognizer.getMsgCodeByMsgClazz(protobufMsg.getClass()))
            .setMsgBody(protobufMsg.toByteArray());

        return _proxyServerCh.writeAndFlush(innerMsg);
    }
}
