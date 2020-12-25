package org.mj.bizserver.cmdhandler.club;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.club.adminctrl.AdminCtrlBizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 亲友圈充值房卡指令处理器
 */
public class ExchangeRoomCardCmdHandler implements ICmdHandler<ClubServerProtocol.ExchangeRoomCardCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ClubServerProtocol.ExchangeRoomCardCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
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
            fromUserId,
            clubId,
            roomCard,
            (resultX) -> buildResultMsgAndSend(
                ctx, remoteSessionId, fromUserId, clubId, roomCard, resultX
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param clubId          亲友圈 Id
     * @param roomCard        房卡数量
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, int clubId, int roomCard, BizResultWrapper<Boolean> resultX) {
        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
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

        ClubServerProtocol.ExchangeRoomCardResult r = ClubServerProtocol.ExchangeRoomCardResult.newBuilder()
            .setClubId(clubId)
            .setRoomCard(roomCard)
            .setSuccezz(resultX.getFinalResult())
            .build();

        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
    }
}
