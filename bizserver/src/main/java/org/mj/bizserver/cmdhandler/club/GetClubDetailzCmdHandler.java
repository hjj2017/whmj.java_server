package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.ClubDetailz;
import org.mj.bizserver.mod.club.membercenter.bizdata.FixGameX;
import org.mj.comm.cmdhandler.ICmdHandler;

import java.util.List;
import java.util.Map;

/**
 * 获取亲友圈详情指令处理器
 */
public class GetClubDetailzCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.GetClubDetailzCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.GetClubDetailzCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        // 获取亲友圈详情
        MemberCenterBizLogic.getInstance().getClubDetailz_async(
            ctx.getFromUserId(),
            cmdObj.getClubId(),
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
        MyCmdHandlerContext ctx, BizResultWrapper<ClubDetailz> resultX) {
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

        // 获取亲友圈详情
        ClubDetailz clubDetailz = resultX.getFinalResult();

        if (null == clubDetailz) {
            return;
        }

        ClubServerProtocol.GetClubDetailzResult.Builder b0 = ClubServerProtocol.GetClubDetailzResult.newBuilder()
            .setClubId(clubDetailz.getClubId())
            .setClubName(clubDetailz.getClubName())
            .setCreateTime(clubDetailz.getCreateTime())
            .setCreatorId(clubDetailz.getCreatorId())
            .setCreatorName(clubDetailz.getCreatorName())
            .setCreatorHeadImg(clubDetailz.getCreatorHeadImg())
            .setCreatorSex(clubDetailz.getCreatorSex())
            .setRoomCard(clubDetailz.getRoomCard())
            .setNumOfPeople(clubDetailz.getNumOfPeople())
            .setNumOfGaming(clubDetailz.getNumOfGaming())
            .setNumOfWaiting(clubDetailz.getNumOfWaiting())
            .setMyRole(clubDetailz.getMyRoleIntVal());

        // 填充固定玩法列表
        fillFixGameXList(b0, clubDetailz);

        ctx.writeAndFlush(b0.build());
    }

    /**
     * 填充固定玩法列表
     *
     * @param rootBuilder 根构建者
     * @param clubDetailz 亲友圈详情
     */
    static private void fillFixGameXList(
        ClubServerProtocol.GetClubDetailzResult.Builder rootBuilder,
        ClubDetailz clubDetailz) {
        if (null == rootBuilder ||
            null == clubDetailz) {
            return;
        }

        // 获取固定玩法列表
        final List<FixGameX> fixGameXList = clubDetailz.getFixGameXList();

        if (null == fixGameXList ||
            fixGameXList.isEmpty()) {
            return;
        }

        for (FixGameX fixGameX : fixGameXList) {
            if (null == fixGameX ||
                null == fixGameX.getGameType0() ||
                null == fixGameX.getGameType1() ||
                null == fixGameX.getRuleMap() ||
                fixGameX.getRuleMap().isEmpty()) {
                continue;
            }

            ClubServerProtocol.GetClubDetailzResult.FixGameX.Builder
                b1 = ClubServerProtocol.GetClubDetailzResult.FixGameX.newBuilder();

            b1.setIndex(fixGameX.getIndex())
                .setGameType0(fixGameX.getGameType0IntVal())
                .setGameType1(fixGameX.getGameType1IntVal());

            for (Map.Entry<Integer, Integer> entry : fixGameX.getRuleMap().entrySet()) {
                b1.addRuleItem(
                    ClubServerProtocol.KeyAndVal.newBuilder()
                        .setKey(entry.getKey())
                        .setVal(entry.getValue())
                );
            }

            rootBuilder.addFixGameX(b1);
        }
    }
}
