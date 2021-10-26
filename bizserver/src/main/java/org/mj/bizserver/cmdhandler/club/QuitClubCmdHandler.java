package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 退出亲友圈指令处理器
 */
public class QuitClubCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.QuitClubCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.QuitClubCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        MemberCenterBizLogic.getInstance().quitClub_async(
            ctx.getFromUserId(),
            cmdObj.getClubId(),
            (resultX) -> buildResultMsgAndSend(
                ctx, cmdObj.getClubId(), resultX
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx     客户端信道处理器上下文
     * @param clubId  亲友圈 Id
     * @param resultX 业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx, int clubId, BizResultWrapper<Boolean> resultX) {
        if (null == ctx ||
            clubId <= 0 ||
            null == resultX) {
            return;
        }

        if (0 != resultX.getErrorCode()) {
            ctx.sendError(
                resultX.getErrorCode(), resultX.getErrorMsg()
            );
            return;
        }

        ClubServerProtocol.QuitClubResult r = ClubServerProtocol.QuitClubResult.newBuilder()
            .setClubId(clubId)
            .setSuccezz(resultX.getFinalResult())
            .build();

        ctx.writeAndFlush(r);

        // 根据用户 Id 移除听众
        ClubBroadcaster.removeAudienceByUserId(ctx.getFromUserId());
    }
}
