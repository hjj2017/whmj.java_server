package org.mj.proxyserver.foundation;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import org.mj.bizserver.allmsg.CommProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.comm.util.MyTimer;
import org.mj.proxyserver.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler.ClientHandshakeStateEvent;

/**
 * 内部消息处理器
 */
public class InternalMsgHandler extends ChannelDuplexHandler {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(InternalMsgHandler.class);

    /**
     * Ping 间隔时间
     */
    static private final int PING_INTERVAL_TIME = 5000;

    /**
     * Ping Id
     */
    private final AtomicInteger _pingId = new AtomicInteger(0);

    /**
     * Ping 心跳
     */
    private ScheduledFuture<?> _pingHeartbeat;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        if (null == ctx) {
            return;
        }

        ChannelHandler[] hArray = {
            new InternalMsgDecoder(),
            new InternalMsgEncoder(),
        };

        // 获取信道管线
        ChannelPipeline pl = ctx.pipeline();

        for (ChannelHandler h : hArray) {
            // 获取处理器类
            Class<? extends ChannelHandler>
                hClazz = h.getClass();

            if (null == pl.get(hClazz)) {
                pl.addBefore(ctx.name(), hClazz.getSimpleName(), h);
            }
        }
    }

    @Override
    public void userEventTriggered(
        ChannelHandlerContext ctx, Object eventObj) {
        if (null == ctx ||
            !(eventObj instanceof ClientHandshakeStateEvent)) {
            return;
        }

        ClientHandshakeStateEvent
            realEvent = (ClientHandshakeStateEvent) eventObj;

        if (ClientHandshakeStateEvent.HANDSHAKE_COMPLETE != realEvent) {
            return;
        }

        // 执行 Ping 心跳
        _pingHeartbeat = MyTimer.scheduleWithFixedDelay(
            () -> doPing(ctx),
            PING_INTERVAL_TIME, PING_INTERVAL_TIME,
            TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (null == ctx) {
            return;
        }

        if (null != _pingHeartbeat) {
            LOGGER.debug("停止 Ping");
            _pingHeartbeat.cancel(true);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObj) {
        if (null == ctx ||
            !(msgObj instanceof InternalServerMsg)) {
            return;
        }

        // 获取内部服务器消息
        // 一般是由 BizServer 应答给当前服务器 ( 也就是 ProxyServer ) 的消息...
        InternalServerMsg realMsg = (InternalServerMsg) msgObj;

        if (CommProtocol.CommMsgCodeDef._PingResult_VALUE == realMsg.getMsgCode()) {
            // 如果是由服务器发回来的 Ping 结果,
            // 则直接跳过...
            return;
        }

        LOGGER.info(
            "收到内部服务器返回消息, msgCode = {}",
            realMsg.getMsgCode()
        );

        //
        // 区别于 BizServer,
        // ProxyServer 收到服务器内部消息 ( InternalServerMsg ) 时,
        // 一般做法就是:
        // 将这个消息拆包装, 把实际消息返回给客户端...
        // @see ClientMsgEncoder
        //
        // 根据会话 Id 写出消息
        ClientChannelGroup.writeAndFlushBySessionId(
            realMsg, // 该消息会经过 ClientMsgEncoder 编码
            realMsg.getRemoteSessionId()
        );
    }

    /**
     * 执行 Ping 命令
     *
     * @param ctx 信道处理器上下文
     */
    private void doPing(ChannelHandlerContext ctx) {
        if (null == ctx) {
            return;
        }

        CommProtocol.PingCmd.Builder b = CommProtocol.PingCmd.newBuilder();
        b.setPingId(_pingId.incrementAndGet());
        CommProtocol.PingCmd cmdObj = b.build();

        final InternalServerMsg innerMsg = new InternalServerMsg();
        innerMsg.setProxyServerId(ProxyServer.getId());
        innerMsg.setRemoteSessionId(-1);
        innerMsg.setFromUserId(-1);
        innerMsg.setMsgCode(CommProtocol.CommMsgCodeDef._PingCmd_VALUE);
        innerMsg.setMsgBody(cmdObj.toByteArray());

        ctx.writeAndFlush(innerMsg);
    }
}
