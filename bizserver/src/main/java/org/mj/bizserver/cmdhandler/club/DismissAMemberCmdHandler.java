package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.club.adminctrl.AdminCtrlBizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 开除一个成员指令处理器
 */
public class DismissAMemberCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.DismissAMemberCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.DismissAMemberCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        AdminCtrlBizLogic.getInstance().dismissAMember_async(
            ctx.getFromUserId(),
            cmdObj.getUserId(),
            cmdObj.getClubId(),
            (resultX) -> buildResultMsgAndSend(
                ctx, cmdObj.getUserId(), cmdObj.getClubId(), resultX
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx      客户端信道处理器上下文
     * @param memberId 亲友圈成员 Id
     * @param clubId   亲友圈 Id
     * @param resultX  业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx, int memberId, int clubId,
        BizResultWrapper<Boolean> resultX) {
        if (null == ctx ||
            null == resultX) {
            return;
        }

        if (0 != resultX.getErrorCode()) {
            ctx.sendError(
                resultX.getErrorCode(), resultX.getErrorMsg()
            );
            return;
        }

        ClubServerProtocol.DismissAMemberResult r = ClubServerProtocol.DismissAMemberResult.newBuilder()
            .setUserId(memberId)
            .setClubId(clubId)
            .setSuccezz(resultX.getFinalResult())
            .build();

        ctx.writeAndFlush(r);
    }
}
