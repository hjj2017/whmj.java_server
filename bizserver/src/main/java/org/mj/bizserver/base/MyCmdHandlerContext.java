package org.mj.bizserver.base;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.mj.bizserver.allmsg.CommProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.comm.cmdhandler.AbstractCmdHandlerContext;

import java.util.Objects;

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
     * @param proxyServerCh 代理服务器信道
     */
    MyCmdHandlerContext(Channel proxyServerCh) {
        super();
        _proxyServerCh = proxyServerCh;
    }

    /**
     * 获取 Netty 信道
     * <p>
     * return Netty 信道
     */
    public Channel getNettyChannel() {
        return _proxyServerCh;
    }

    @Override
    public ChannelFuture sendError(int errorCode, String errorMsg) {
        CommProtocol.ErrorHintResult.Builder b = CommProtocol.ErrorHintResult.newBuilder();
        b.setErrorCode(errorCode);
        b.setErrorMsg(Objects.requireNonNullElse(errorMsg, ""));

        return writeAndFlush(b.build());
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
