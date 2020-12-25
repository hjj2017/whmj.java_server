package org.mj.bizserver.foundation;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 内部服务器消息解码器
 */
public class InternalMsgDecoder extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(InternalMsgDecoder.class);

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

            // 创建内部消息
            InternalServerMsg realMsg = new InternalServerMsg();
            realMsg.setProxyServerId(byteBuf.readShort());
            realMsg.setRemoteSessionId(byteBuf.readInt());
            realMsg.setFromUserId(byteBuf.readInt());
            realMsg.setMsgCode(byteBuf.readShort());

            // 消息内容
            byte[] msgBody = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgBody);
            realMsg.setMsgBody(msgBody);

            // 出发消息读取事件
            ctx.fireChannelRead(realMsg);
            // 释放资源
            ReferenceCountUtil.safeRelease(inputFrame);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
