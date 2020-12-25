package org.mj.bizserver.cmdhandler.club;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.ClubDetailz;
import org.mj.bizserver.mod.club.membercenter.bizdata.FixGameX;
import org.mj.comm.cmdhandler.ICmdHandler;

import java.util.List;
import java.util.Map;

/**
 * 获取亲友圈详情指令处理器
 */
public class GetClubDetailzCmdHandler implements ICmdHandler<ClubServerProtocol.GetClubDetailzCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ClubServerProtocol.GetClubDetailzCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        // 获取亲友圈详情
        MemberCenterBizLogic.getInstance().getClubDetailz_async(
            fromUserId, cmdObj.getClubId(),
            (resultX) -> buildResultMsgAndSend(ctx, remoteSessionId, fromUserId, resultX)
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
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, BizResultWrapper<ClubDetailz> resultX) {
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

        // 获取亲友圈详情
        ClubDetailz clubDetailz = resultX.getFinalResult();

        if (null == clubDetailz) {
            newMsg.free();
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

        ClubServerProtocol.GetClubDetailzResult r = b0.build();
        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
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
