package org.mj.proxyserver.nobody;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mj.bizserver.allmsg.CommProtocol;
import org.mj.bizserver.base.Ukey;
import org.mj.proxyserver.foundation.ClientMsgSemiFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 重连指令处理器
 */
public class ReconnCmdHandler extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ReconnCmdHandler.class);

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

            if (CommProtocol.CommMsgCodeDef._ReconnCmd_VALUE != clientMsg.getMsgCode()) {
                // 如果接到的不是重连命令,
                super.channelRead(ctx, msgObj);
                return;
            }

            // 重连指令
            final CommProtocol.ReconnCmd cmdObj = CommProtocol.ReconnCmd
                .parseFrom(clientMsg.getMsgBody());

            if (!Ukey.verify(
                cmdObj.getUserId(), cmdObj.getUkeyStr(), cmdObj.getUkeyExpireAt())) {
                LOGGER.error(
                    "重连失败, Ukey 错误! userId = {}",
                    cmdObj.getUserId()
                );
                ctx.disconnect();
                return;
            }

            if (CheckInTicketCmdHandler.renewConn(ctx, cmdObj.getUserId(), null)) {
                // 构建重连结果
                CommProtocol.ReconnResult r = CommProtocol.ReconnResult.newBuilder()
                    .setUserId(cmdObj.getUserId())
                    .setUkeyStr(cmdObj.getUkeyStr())
                    .setUkeyExpireAt(cmdObj.getUkeyExpireAt())
                    .build();

                // 发送重连结果
                ctx.writeAndFlush(r);
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
