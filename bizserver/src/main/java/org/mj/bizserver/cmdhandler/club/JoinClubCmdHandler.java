package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 加入亲友圈指令处理器
 */
public class JoinClubCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.JoinClubCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.JoinClubCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        // ( 异步方式 ) 加入亲友圈
        MemberCenterBizLogic.getInstance().joinClub_async(
            ctx.getFromUserId(),
            cmdObj.getClubId(),
            (resultX) -> buildResultMsgAndSend(ctx, cmdObj.getClubId(), resultX)
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

        // 构建结果消息
        ClubServerProtocol.JoinClubResult r = ClubServerProtocol.JoinClubResult.newBuilder()
            .setClubId(clubId)
            .setSuccezz(resultX.getFinalResult())
            .build();

        ctx.writeAndFlush(r);
    }
}
