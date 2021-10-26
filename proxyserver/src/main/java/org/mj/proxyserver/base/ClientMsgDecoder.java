package org.mj.proxyserver.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端消息解码器
 * XXX 注意: 这里只将消息解码成半成品,
 * 不会完全解析成命令对象 XxxCmd!
 */
public class ClientMsgDecoder extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ClientMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObj) {
        try {
            if (null == ctx ||
                !(msgObj instanceof BinaryWebSocketFrame)) {
                super.channelRead(ctx, msgObj);
                return;
            }

            BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msgObj;
            ByteBuf byteBuf = inputFrame.content();

            // 读掉消息长度
            byteBuf.readShort();

            // 读取消息编码
            int msgCode = byteBuf.readShort();
            // 读取消息体
            byte[] msgBody = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgBody);

            // 创建客户端消息半成品
            ClientMsgSemiFinished clientMsg = new ClientMsgSemiFinished();
            clientMsg.setMsgCode(msgCode);
            clientMsg.setMsgBody(msgBody);

            // 继续派发消息
            ctx.fireChannelRead(clientMsg);
            // 释放资源
            ReferenceCountUtil.safeRelease(inputFrame);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
