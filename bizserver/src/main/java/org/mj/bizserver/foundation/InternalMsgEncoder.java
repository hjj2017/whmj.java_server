package org.mj.bizserver.foundation;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 内部服务器消息编码器
 */
public class InternalMsgEncoder extends ChannelOutboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(InternalMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msgObj, ChannelPromise promise) {
        try {
            if (!(msgObj instanceof InternalServerMsg)) {
                super.write(ctx, msgObj, promise);
                return;
            }

            // 转型为内部服务器消息
            InternalServerMsg realMsg = (InternalServerMsg) msgObj;

            if (null == realMsg.getMsgBody()) {
                // 确保消息体不为空
                realMsg.setMsgBody(new byte[0]);
            }

            ByteBuf byteBuf = ctx.alloc().buffer();

            // 先写出消息长度, 避免粘包情况!
            // XXX 注意: 12 = sizeof(short) + sizeof(int) + sizeof(int) + sizeof(short)
            byteBuf.writeShort(12 + realMsg.getMsgBody().length);

            byteBuf.writeShort(realMsg.getProxyServerId());
            byteBuf.writeInt(realMsg.getRemoteSessionId());
            byteBuf.writeInt(realMsg.getFromUserId());
            byteBuf.writeShort(realMsg.getMsgCode());
            byteBuf.writeBytes(realMsg.getMsgBody());

            // 写出消息
            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuf);
            ctx.write(outputFrame, promise);
            // 释放资源
            realMsg.free();
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
