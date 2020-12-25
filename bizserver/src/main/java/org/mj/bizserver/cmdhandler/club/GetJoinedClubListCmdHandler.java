package org.mj.bizserver.cmdhandler.club;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.JoinedClub;
import org.mj.comm.cmdhandler.ICmdHandler;

import java.util.Collections;
import java.util.List;

/**
 * 获取已经加入的亲友圈列表
 */
public class GetJoinedClubListCmdHandler implements ICmdHandler<ClubServerProtocol.GetJoinedClubListCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ClubServerProtocol.GetJoinedClubListCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        // 获取已经加入的亲友圈列表
        MemberCenterBizLogic.getInstance().getJoinedClubList_async(
            fromUserId, (resultX) -> buildResultMsgAndSend(ctx, remoteSessionId, fromUserId, resultX)
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, BizResultWrapper<List<JoinedClub>> resultX) {
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

        // 获取已经加入的亲友圈列表
        List<JoinedClub> joinedClubList = resultX.getFinalResult();

        if (null == joinedClubList) {
            joinedClubList = Collections.emptyList();
        }

        ClubServerProtocol.GetJoinedClubListResult.Builder b = ClubServerProtocol.GetJoinedClubListResult.newBuilder();

        for (JoinedClub joinedClub : joinedClubList) {
            if (null == joinedClub) {
                continue;
            }

            b.addJoinedClub(
                ClubServerProtocol.GetJoinedClubListResult.JoinedClub.newBuilder()
                    .setClubId(joinedClub.getClubId())
                    .setClubName(joinedClub.getClubName())
                    .setCreatorId(joinedClub.getCreatorId())
                    .setCreatorName(joinedClub.getCreatorName())
                    .setCreatorSex(joinedClub.getCreatorSex())
                    .setCreatorHeadImg(joinedClub.getCreatorHeadImg())
                    .setNumOfPeople(joinedClub.getNumOfPeople())
                    .setNumOfGaming(joinedClub.getNumOfGaming())
                    .setNumOfWaiting(joinedClub.getNumOfWaiting())
            );
        }

        ClubServerProtocol.GetJoinedClubListResult r = b.build();

        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
    }
}
