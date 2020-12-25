package org.mj.bizserver.cmdhandler.club;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberInfo;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.mj.comm.util.OutParam;

import java.util.Collections;
import java.util.List;

/**
 * 获取 ( 亲友圈 ) 成员信息列表指令处理器
 */
public class GetMemberInfoListCmdHandler implements ICmdHandler<ClubServerProtocol.GetMemberInfoListCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ClubServerProtocol.GetMemberInfoListCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        final OutParam<Integer> out_totalCount = new OutParam<>();

        MemberCenterBizLogic.getInstance().getMemberInfoList_async(
            fromUserId,
            cmdObj.getClubId(),
            cmdObj.getPageIndex(),
            cmdObj.getPageSize(),
            out_totalCount,
            (resultX) -> buildResultMsgAndSend(
                ctx, remoteSessionId, fromUserId, cmdObj.getClubId(), cmdObj.getPageIndex(), out_totalCount, resultX
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
     * @param pageIndex       页面索引
     * @param out_totalCount  亲友圈总人数
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        int clubId,
        int pageIndex,
        OutParam<Integer> out_totalCount,
        BizResultWrapper<List<MemberInfo>> resultX) {
        if (null == ctx ||
            null == resultX) {
            return;
        }

        InternalServerMsg newMsg = new InternalServerMsg();
        newMsg.setRemoteSessionId(remoteSessionId);
        newMsg.setFromUserId(fromUserId);

        if (0 != newMsg.admitError(resultX)) {
            ctx.writeAndFlush(newMsg);
            return;
        }

        // 获取亲友圈成员列表
        List<MemberInfo> memberInfoList = resultX.getFinalResult();

        if (null == memberInfoList) {
            memberInfoList = Collections.emptyList();
        }

        if (null == out_totalCount.getVal()) {
            out_totalCount.setVal(0);
        }

        ClubServerProtocol.GetMemberInfoListResult.Builder b = ClubServerProtocol.GetMemberInfoListResult.newBuilder()
            .setClubId(clubId)
            .setPageIndex(pageIndex)
            .setTotalCount(out_totalCount.getVal());

        for (MemberInfo currMember : memberInfoList) {
            if (null == currMember) {
                continue;
            }

            b.addMemberInfo(
                ClubServerProtocol.GetMemberInfoListResult.MemberInfo.newBuilder()
                    .setUserId(currMember.getUserId())
                    .setUserName(currMember.getUserName())
                    .setHeadImg(currMember.getHeadImg())
                    .setSex(currMember.getSex())
                    .setRole(currMember.getRoleIntVal())
                    .setJoinTime(currMember.getJoinTime())
                    .setLastLoginTime(currMember.getLastLoginTime())
                    .setCurrState(currMember.getCurrStateIntVal())
            );
        }

        ClubServerProtocol.GetMemberInfoListResult r = b.build();

        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
    }
}
