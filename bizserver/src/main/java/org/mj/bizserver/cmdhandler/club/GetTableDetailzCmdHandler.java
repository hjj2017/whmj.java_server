package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.Player;
import org.mj.bizserver.mod.club.membercenter.bizdata.Table;
import org.mj.comm.cmdhandler.ICmdHandler;

import java.util.List;
import java.util.Map;

/**
 * 获取牌桌详情指令处理器
 */
public class GetTableDetailzCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.GetTableDetailzCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.GetTableDetailzCmd cmdObj) {
        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        MemberCenterBizLogic.getInstance().getTableDetailz_async(
            ctx.getFromUserId(),
            cmdObj.getClubId(),
            cmdObj.getSeqNum(),
            (resultX) -> buildResultMsgAndSend(
                ctx, cmdObj.getClubId(), cmdObj.getSeqNum(), resultX
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param clubId          亲友圈 Id
     * @param tableSeqNum     亲友圈牌桌序号
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx,
        int clubId,
        int tableSeqNum,
        BizResultWrapper<Table> resultX) {
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

        ClubServerProtocol.GetTableDetailzResult.Builder
            b0 = ClubServerProtocol.GetTableDetailzResult.newBuilder();

        b0.setClubId(clubId);
        b0.setSeqNum(tableSeqNum);

        // 获取当前牌桌
        final Table currTable = resultX.getFinalResult();

        if (null != currTable) {
            ClubServerProtocol.Table.Builder b1 = ClubServerProtocol.Table.newBuilder();
            b1.setSeqNum(currTable.getSeqNum())
                .setRoomId(currTable.getRoomId())
                .setGameType0(currTable.getGameType0IntVal())
                .setGameType1(currTable.getGameType1IntVal())
                .setMaxRound(currTable.getMaxRound())
                .setCurrRound(currTable.getCurrRound())
                .setMaxPlayer(currTable.getMaxPlayer());

            // 获取规则字典
            final Map<Integer, Integer> ruleMap = currTable.getRuleMap();

            for (Map.Entry<Integer, Integer> keyAndVal : ruleMap.entrySet()) {
                b1.addRuleItem(
                    ClubServerProtocol.KeyAndVal.newBuilder()
                        .setKey(keyAndVal.getKey())
                        .setVal(keyAndVal.getValue())
                );
            }

            // 填充所有玩家
            fillAllPlayer(b1, currTable.getPlayerList());

            b0.setTable(b1);
        }

        ctx.writeAndFlush(b0.build());
    }

    /**
     * 填充所有玩家
     *
     * @param b1         消息构建器
     * @param playerList 玩家列表
     */
    static private void fillAllPlayer(
        final ClubServerProtocol.Table.Builder b1,
        final List<Player> playerList) {
        if (null == b1 ||
            null == playerList ||
            playerList.isEmpty()) {
            return;
        }

        for (Player currPlayer : playerList) {
            if (null == currPlayer) {
                continue;
            }

            ClubServerProtocol.Player.Builder b2 = ClubServerProtocol.Player.newBuilder();
            b2.setUserId(currPlayer.getUserId())
                .setAtSeatIndex(currPlayer.getSeatIndex())
                .setUserName(currPlayer.getUserName())
                .setSex(currPlayer.getSex())
                .setHeadImg(currPlayer.getHeadImg());

            b1.addPlayer(b2);
        }
    }
}
