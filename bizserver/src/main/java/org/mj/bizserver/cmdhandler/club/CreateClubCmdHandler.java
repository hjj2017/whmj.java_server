package org.mj.bizserver.cmdhandler.club;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.club.adminctrl.AdminCtrlBizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 创建亲友圈命令处理器
 */
public class CreateClubCmdHandler implements ICmdHandler<ClubServerProtocol.CreateClubCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ClubServerProtocol.CreateClubCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        // 创建亲友圈
        AdminCtrlBizLogic.getInstance().createClub_async(
            fromUserId,
            cmdObj.getClubName(),
            (resultX) -> buildResultMsgAndSend(ctx, remoteSessionId, fromUserId, cmdObj.getClubName(), resultX)
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param clubName        亲友圈名称
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, String clubName,
        BizResultWrapper<Integer> resultX) {
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

        ClubServerProtocol.CreateClubResult r = ClubServerProtocol.CreateClubResult.newBuilder()
            .setClubId(resultX.getFinalResult())
            .setClubName(clubName)
            .build();

        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
    }
}
