package org.mj.bizserver.foundation;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.mj.bizserver.allmsg.CommProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.comm.cmdhandler.AbstractCmdHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 内部服务器消息处理器
 */
public class InternalServerMsgHandler_BizServer extends ChannelDuplexHandler {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(InternalServerMsgHandler_BizServer.class);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        if (null == ctx) {
            return;
        }

        ChannelHandler[] hArray = {
            new LengthFieldBasedFrameDecoder(4096, 0, 2, 0, 2),
            new LengthFieldPrepender(2),
            new InternalServerMsgCodec(),
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
    public void channelRead(ChannelHandlerContext nettyCtx, Object msgObj) {
        if (null == nettyCtx ||
            !(msgObj instanceof InternalServerMsg)) {
            return;
        }

        // 获取内部服务器消息
        InternalServerMsg realMsg = (InternalServerMsg) msgObj;
        // 获取协议消息
        GeneratedMessageV3 protoMsg = realMsg.getProtoMsg();

        if (CommProtocol.CommMsgCodeDef._PingCmd_VALUE != realMsg.getMsgCode()) {
            LOGGER.info(
                "收到内部服务器消息, proxyServerId = {}, remoteSessionId = {}, fromUserId = {}, msgCode = {}, msgClazz = {}",
                realMsg.getProxyServerId(),
                realMsg.getRemoteSessionId(),
                realMsg.getFromUserId(),
                realMsg.getMsgCode(),
                null == protoMsg ? "NULL" : protoMsg.getClass().getSimpleName()
            );
        }

        AbstractCmdHandlerContext myCtx = new MyCmdHandlerContext(nettyCtx.channel())
            .setProxyServerId(realMsg.getProxyServerId())
            .setRemoteSessionId(realMsg.getRemoteSessionId())
            .setFromUserId(realMsg.getFromUserId())
            .setClientIP(realMsg.getClientIP());

        // 处理命令对象
        // XXX 注意: 命令对象具体是由哪个 CmdHandler 处理器的,
        // 可以参考 CmdHandlerFactory 类!
        // CmdHandlerFactory 类是 MainThreadProcessor 构造器中的一个参数,
        // 它会自动扫描指定包中所有实现了 ICmdHandler 接口的类...
        MainThreadProcessorSingleton.getInstance().process(
            (MyCmdHandlerContext) myCtx, protoMsg
        );

        realMsg.free();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (null == ctx) {
            return;
        }

        LOGGER.warn("代理服务器已断开, proxyServerId = {}", IdSetterGetter.getProxyServerId(ctx));

        ProxyServerChannelGroup.remove(ctx);
    }

    @Override
    public void userEventTriggered(
        ChannelHandlerContext ctx, Object objEvent) {
        if (null == ctx ||
            !(objEvent instanceof WebSocketServerProtocolHandler.HandshakeComplete)) {
            return;
        }

        WebSocketServerProtocolHandler.HandshakeComplete
            handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) objEvent;

        // 获取代理服务器 Id
        String strServerId = handshakeComplete.requestHeaders().get("proxyServerId");

        if (null == strServerId ||
            strServerId.isEmpty()) {
            return;
        }

        LOGGER.info(
            "代理服务器已接入, proxyServerId = {}",
            strServerId
        );

        IdSetterGetter.putProxyServerId(
            ctx, Integer.parseInt(strServerId)
        );

        // 添加到服务器分组
        ProxyServerChannelGroup.add(ctx);
    }
}
