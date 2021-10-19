package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.club.adminctrl.AdminCtrlBizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 创建亲友圈命令处理器
 */
public class CreateClubCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.CreateClubCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.CreateClubCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        // 创建亲友圈
        AdminCtrlBizLogic.getInstance().createClub_async(
            ctx.getFromUserId(),
            cmdObj.getClubName(),
            (resultX) -> buildResultMsgAndSend(ctx, cmdObj.getClubName(), resultX)
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx      客户端信道处理器上下文
     * @param clubName 亲友圈名称
     * @param resultX  业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx, String clubName, BizResultWrapper<Integer> resultX) {
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

        ClubServerProtocol.CreateClubResult r = ClubServerProtocol.CreateClubResult.newBuilder()
            .setClubId(resultX.getFinalResult())
            .setClubName(clubName)
            .build();

        ctx.writeAndFlush(r);
    }
}
