package org.mj.bizserver.cmdhandler.club;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.club.adminctrl.AdminCtrlBizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 同意或拒绝加入申请
 */
public class ApprovalToJoinCmdHandler implements ICmdHandler<ClubServerProtocol.ApprovalToJoinCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ClubServerProtocol.ApprovalToJoinCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        AdminCtrlBizLogic.getInstance().approvalToJoin_async(
            fromUserId,
            cmdObj.getUserId(),
            cmdObj.getClubId(),
            cmdObj.getYesOrNo(),
            (resultX) -> buildResultMsgAndSend(
                ctx, remoteSessionId, fromUserId, cmdObj.getUserId(), cmdObj.getClubId(), resultX
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param memberId        亲友圈成员 Id
     * @param clubId          亲友圈 Id
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, int memberId, int clubId,
        BizResultWrapper<Boolean> resultX) {
        if (null == ctx ||
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

        ClubServerProtocol.ApprovalToJoinResult r = ClubServerProtocol.ApprovalToJoinResult.newBuilder()
            .setUserId(memberId)
            .setClubId(clubId)
            .setSuccezz(resultX.getFinalResult())
            .build();

        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
    }
}
