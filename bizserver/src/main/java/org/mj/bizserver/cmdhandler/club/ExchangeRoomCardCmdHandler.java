package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.club.adminctrl.AdminCtrlBizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 亲友圈充值房卡指令处理器
 */
public class ExchangeRoomCardCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.ExchangeRoomCardCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.ExchangeRoomCardCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        final int clubId = cmdObj.getClubId();
        final int roomCard = cmdObj.getRoomCard();

        if (clubId <= 0 ||
            roomCard <= 0) {
            return;
        }

        AdminCtrlBizLogic.getInstance().exchangeRoomCard_async(
            ctx.getFromUserId(),
            clubId,
            roomCard,
            (resultX) -> buildResultMsgAndSend(ctx, clubId, roomCard, resultX)
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx      客户端信道处理器上下文
     * @param clubId   亲友圈 Id
     * @param roomCard 房卡数量
     * @param resultX  业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx, int clubId, int roomCard, BizResultWrapper<Boolean> resultX) {
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

        ClubServerProtocol.ExchangeRoomCardResult r = ClubServerProtocol.ExchangeRoomCardResult.newBuilder()
            .setClubId(clubId)
            .setRoomCard(roomCard)
            .setSuccezz(resultX.getFinalResult())
            .build();

        ctx.writeAndFlush(r);
    }
}
