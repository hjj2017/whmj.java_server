package org.mj.bizserver.cmdhandler.passport;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.allmsg.PassportServerProtocol;
import org.mj.bizserver.foundation.AliSMSAuthZervice;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 获取短信验证码指令处理器
 */
public class GetSMSAuthCodeCmdHandler implements ICmdHandler<PassportServerProtocol.GetSMSAuthCodeCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        PassportServerProtocol.GetSMSAuthCodeCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            null == cmdObj) {
            return;
        }

        AliSMSAuthZervice.getInstance().sendAuthCode_async(
            cmdObj.getPhoneNumber(),
            (resultX) -> buildResultMsgAndSend(ctx, remoteSessionId, fromUserId, cmdObj.getPhoneNumber(), resultX)
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        String phoneNumber,
        BizResultWrapper<Boolean> resultX) {
        if (null == ctx ||
            remoteSessionId <= 0 ||
            null == phoneNumber ||
            null == resultX) {
            return;
        }

        final InternalServerMsg newMsg = new InternalServerMsg();
        newMsg.setRemoteSessionId(remoteSessionId);
        newMsg.setFromUserId(fromUserId);

        if (0 != newMsg.admitError(resultX)) {
            ctx.writeAndFlush(newMsg);
            return;
        }

        PassportServerProtocol.GetSMSAuthCodeResult r = PassportServerProtocol.GetSMSAuthCodeResult.newBuilder()
            .setPhoneNumber(phoneNumber)
            .setSuccezz(Boolean.TRUE == resultX.getFinalResult())
            .build();

        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
    }
}
