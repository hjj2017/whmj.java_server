package org.mj.proxyserver.nobody;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mj.bizserver.allmsg.PassportServerProtocol;
import org.mj.proxyserver.foundation.ClientMsgSemiFinished;
import org.mj.proxyserver.foundation.IdSetterGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户 Id 验证器
 */
public class UserIdValidator extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(UserIdValidator.class);

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

            boolean izSafeMsg;

            switch (clientMsg.getMsgCode()) {
                case PassportServerProtocol.PassportServerMsgCodeDef._GetSMSAuthCodeCmd_VALUE:
                case PassportServerProtocol.PassportServerMsgCodeDef._UserLoginCmd_VALUE:
                    izSafeMsg = true;
                    break;

                default:
                    izSafeMsg = false;
            }

            if (izSafeMsg) {
                // 如果是安全消息,
                super.channelRead(ctx, msgObj);
                return;
            }

            if (IdSetterGetter.getUserId(ctx) <= 0) {
                LOGGER.error(
                    "没有设置用户 Id, 不能处理消息!, msgCode = {}",
                    clientMsg.getMsgCode()
                );

                // 如果不是安全消息,
                // 并且还没有设置用户 Id,
                ctx.disconnect();
                return;
            }

            super.channelRead(ctx, msgObj);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
