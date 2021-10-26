package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.JoinedClub;
import org.mj.comm.cmdhandler.ICmdHandler;

import java.util.Collections;
import java.util.List;

/**
 * 获取已经加入的亲友圈列表
 */
public class GetJoinedClubListCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.GetJoinedClubListCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.GetJoinedClubListCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        // 获取已经加入的亲友圈列表
        MemberCenterBizLogic.getInstance().getJoinedClubList_async(
            ctx.getFromUserId(),
            (resultX) -> buildResultMsgAndSend(ctx, resultX)
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx     客户端信道处理器上下文
     * @param resultX 业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx, BizResultWrapper<List<JoinedClub>> resultX) {
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

        ctx.writeAndFlush(b.build());
    }
}
