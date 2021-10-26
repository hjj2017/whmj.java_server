package org.mj.proxyserver.base;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.base.MsgRecognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端消息编码器
 */
public class ClientMsgEncoder extends ChannelOutboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ClientMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msgObj, ChannelPromise promise) {
        try {
            if (!(msgObj instanceof GeneratedMessageV3) &&
                !(msgObj instanceof InternalServerMsg)) {
                super.write(ctx, msgObj, promise);
                return;
            }

            // 定义消息编码和消息体
            int msgCode;
            byte[] msgBody;

            if (msgObj instanceof InternalServerMsg) {
                // 如果是内部服务器消息
                msgCode = ((InternalServerMsg) msgObj).getMsgCode();
                msgBody = ((InternalServerMsg) msgObj).getMsgBody();
                // 释放资源
                ((InternalServerMsg) msgObj).free();
            } else /*if (msgObj instanceof GeneratedMessageV3)*/ {
                // 如果是协议消息
                msgCode = MsgRecognizer.getMsgCodeByMsgClazz(msgObj.getClass());
                msgBody = ((GeneratedMessageV3) msgObj).toByteArray();
            }

            ByteBuf byteBuf = ctx.alloc().buffer();

            // 先写出消息长度, 避免粘包情况!
            // XXX 注意: 2 = sizeof(short)
            byteBuf.writeShort(2 + msgBody.length);
            byteBuf.writeShort(msgCode);
            byteBuf.writeBytes(msgBody);

            // 写出消息
            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuf);
            ctx.write(outputFrame, promise);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
