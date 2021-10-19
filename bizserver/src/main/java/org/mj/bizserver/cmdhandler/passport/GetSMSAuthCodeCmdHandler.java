package org.mj.bizserver.cmdhandler.passport;

import org.mj.bizserver.allmsg.PassportServerProtocol;
import org.mj.bizserver.foundation.AliSMSAuthZervice;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 获取短信验证码指令处理器
 */
public class GetSMSAuthCodeCmdHandler implements ICmdHandler<MyCmdHandlerContext, PassportServerProtocol.GetSMSAuthCodeCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        PassportServerProtocol.GetSMSAuthCodeCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        AliSMSAuthZervice.getInstance().sendAuthCode_async(
            cmdObj.getPhoneNumber(),
            (resultX) -> buildResultMsgAndSend(ctx, cmdObj.getPhoneNumber(), resultX)
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx     客户端信道处理器上下文
     * @param resultX 业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx,
        String phoneNumber,
        BizResultWrapper<Boolean> resultX) {
        if (null == ctx ||
            null == phoneNumber ||
            null == resultX) {
            return;
        }

        if (0 != resultX.getErrorCode()) {
            ctx.sendError(
                resultX.getErrorCode(), resultX.getErrorMsg()
            );
            return;
        }

        PassportServerProtocol.GetSMSAuthCodeResult r = PassportServerProtocol.GetSMSAuthCodeResult.newBuilder()
            .setPhoneNumber(phoneNumber)
            .setSuccezz(Boolean.TRUE == resultX.getFinalResult())
            .build();

        ctx.writeAndFlush(r);
    }
}
