package org.mj.bizserver.cmdhandler.club;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 退出亲友圈指令处理器
 */
public class QuitClubCmdHandler implements ICmdHandler<ClubServerProtocol.QuitClubCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ClubServerProtocol.QuitClubCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        MemberCenterBizLogic.getInstance().quitClub_async(
            fromUserId,
            cmdObj.getClubId(),
            (resultX) -> buildResultMsgAndSend(
                ctx, remoteSessionId, fromUserId, cmdObj.getClubId(), resultX
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
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, int clubId, BizResultWrapper<Boolean> resultX) {
        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            clubId <= 0 ||
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

        ClubServerProtocol.QuitClubResult r = ClubServerProtocol.QuitClubResult.newBuilder()
            .setClubId(clubId)
            .setSuccezz(resultX.getFinalResult())
            .build();

        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);

        // 根据用户 Id 移除听众
        ClubBroadcaster.removeAudienceByUserId(fromUserId);
    }
}
