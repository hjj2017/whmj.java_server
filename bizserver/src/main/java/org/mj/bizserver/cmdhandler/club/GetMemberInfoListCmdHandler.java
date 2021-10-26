package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberInfo;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.mj.comm.util.OutParam;

import java.util.Collections;
import java.util.List;

/**
 * 获取 ( 亲友圈 ) 成员信息列表指令处理器
 */
public class GetMemberInfoListCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.GetMemberInfoListCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.GetMemberInfoListCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        final OutParam<Integer> out_totalCount = new OutParam<>();

        MemberCenterBizLogic.getInstance().getMemberInfoList_async(
            ctx.getFromUserId(),
            cmdObj.getClubId(),
            cmdObj.getPageIndex(),
            cmdObj.getPageSize(),
            out_totalCount,
            (resultX) -> buildResultMsgAndSend(
                ctx, cmdObj.getClubId(), cmdObj.getPageIndex(), out_totalCount, resultX
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx            客户端信道处理器上下文
     * @param clubId         亲友圈 Id
     * @param pageIndex      页面索引
     * @param out_totalCount 亲友圈总人数
     * @param resultX        业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx,
        int clubId,
        int pageIndex,
        OutParam<Integer> out_totalCount,
        BizResultWrapper<List<MemberInfo>> resultX) {
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

        ctx.writeAndFlush(b.build());
    }
}
