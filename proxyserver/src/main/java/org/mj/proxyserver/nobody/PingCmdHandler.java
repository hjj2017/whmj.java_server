package org.mj.proxyserver.nobody;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mj.bizserver.allmsg.CommProtocol;
import org.mj.proxyserver.base.ClientMsgSemiFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ping 指令处理器,
 * XXX 注意: 这是由客户端发过来的
 */
public class PingCmdHandler extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(PingCmdHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObj) {
        try {
            if (null == ctx ||
                !(msgObj instanceof ClientMsgSemiFinished)) {
                // 如果接到的不是客户端半成品消息,
                super.channelRead(ctx, msgObj);
                return;
            }

            // 获取客户端消息
            ClientMsgSemiFinished clientMsg = (ClientMsgSemiFinished) msgObj;

            if (CommProtocol.CommMsgCodeDef._PingCmd_VALUE != clientMsg.getMsgCode()) {
                // 如果接到的不是检票命令,
                super.channelRead(ctx, msgObj);
                return;
            }

            // 创建 Ping 指令
            CommProtocol.PingCmd
                newCmd = CommProtocol.PingCmd.parseFrom(clientMsg.getMsgBody());
            // 获取 Ping Id
            int pingId = newCmd.getPingId();

            // 发送 Ping 结果
            ctx.writeAndFlush(
                CommProtocol.PingResult.newBuilder()
                    .setPingId(pingId)
                    .build()
            );
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
