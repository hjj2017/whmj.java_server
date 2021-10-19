package org.mj.bizserver.cmdhandler.record;

import org.mj.bizserver.allmsg.RecordServerProtocol;
import org.mj.bizserver.def.GameType0Enum;
import org.mj.bizserver.def.GameType1Enum;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.record.RecordBizLogic;
import org.mj.bizserver.mod.record.bizdata.Player;
import org.mj.bizserver.mod.record.bizdata.RecordSummary;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.mj.comm.util.OutParam;

import java.util.List;

/**
 * 获取战绩列表指令处理器
 */
public class GetRecordListCmdHandler implements ICmdHandler<MyCmdHandlerContext, RecordServerProtocol.GetRecordListCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        RecordServerProtocol.GetRecordListCmd cmdObj) {

        if (null == ctx) {
            return;
        }

        final int userId = cmdObj.getUserId();
        final int clubId = cmdObj.getClubId();
        final GameType0Enum gameType0 = GameType0Enum.valueOf(cmdObj.getGameType0());
        final GameType1Enum gameType1 = GameType1Enum.valueOf(cmdObj.getGameType1());
        final int pageIndex = cmdObj.getPageIndex();
        final OutParam<Integer> out_totalCount = new OutParam<>();

        // 获取战绩列表
        RecordBizLogic.getInstance().getRecordList_async(
            cmdObj.getUserId(),
            cmdObj.getClubId(),
            gameType0,
            gameType1,
            pageIndex,
            cmdObj.getPageSize(),
            out_totalCount,
            (resultX) -> buildResultMsgAndSend(
                ctx, userId, clubId, gameType0, gameType1, pageIndex,
                OutParam.optVal(out_totalCount, 0),
                resultX
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx        客户端信道处理器上下文
     * @param userId     用户 Id
     * @param clubId     亲友圈 Id
     * @param gameType0  游戏类型 0
     * @param gameType1  游戏类型 1
     * @param pageIndex  页面索引
     * @param totalCount 记录总数
     * @param resultX    业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx,
        int userId,
        int clubId,
        GameType0Enum gameType0,
        GameType1Enum gameType1,
        int pageIndex,
        int totalCount,
        BizResultWrapper<List<RecordSummary>> resultX) {

        if (0 != resultX.getErrorCode()) {
            ctx.sendError(
                resultX.getErrorCode(), resultX.getErrorMsg()
            );
            return;
        }

        RecordServerProtocol.GetRecordListResult.Builder b0 = RecordServerProtocol.GetRecordListResult.newBuilder()
            .setUserId(userId)
            .setClubId(clubId)
            .setGameType0(null == gameType0 ? -1 : gameType0.getIntVal())
            .setGameType1(null == gameType1 ? -1 : gameType1.getIntVal())
            .setPageIndex(pageIndex)
            .setTotalCount(totalCount);

        // 填充记录列表
        fillRecordList(b0, resultX.getFinalResult());

        RecordServerProtocol.GetRecordListResult r = b0.build();

        ctx.writeAndFlush(r);
    }

    /**
     * 填充战绩列表
     *
     * @param rootBuilder       根构建器
     * @param recordSummaryList 战绩摘要列表
     */
    static private void fillRecordList(
        RecordServerProtocol.GetRecordListResult.Builder rootBuilder,
        List<RecordSummary> recordSummaryList) {
        if (null == rootBuilder ||
            null == recordSummaryList ||
            recordSummaryList.isEmpty()) {
            return;
        }

        for (RecordSummary recordSummary : recordSummaryList) {
            if (null == recordSummary) {
                continue;
            }

            RecordServerProtocol.GetRecordListResult.ARecord.Builder
                b1 = RecordServerProtocol.GetRecordListResult.ARecord.newBuilder()
                .setGameType1(recordSummary.getGameType1IntVal())
                .setRoomId(recordSummary.getRoomId())
                .setRoomUUId(recordSummary.getRoomUUId())
                .setCostRoomCard(recordSummary.getCostRoomCard())
                .setActualRoundCount(recordSummary.getActualRoundCount())
                .setCreateTime(recordSummary.getCreateTime())
                .setOverTime(recordSummary.getOverTime());

            // 填充玩家列表
            fillPlayerList(b1, recordSummary.getPlayerList());

            rootBuilder.addRecordz(b1);
        }
    }

    /**
     * 填充玩家列表
     *
     * @param rootBuilder 根构建器
     * @param playerList  玩家列表
     */
    static private void fillPlayerList(
        RecordServerProtocol.GetRecordListResult.ARecord.Builder rootBuilder,
        List<Player> playerList) {
        if (null == rootBuilder ||
            null == playerList ||
            playerList.isEmpty()) {
            return;
        }

        for (Player currPlayer : playerList) {
            if (null == currPlayer) {
                continue;
            }

            rootBuilder.addPlayer(
                RecordServerProtocol.Player.newBuilder()
                    .setUserId(currPlayer.getUserId())
                    .setUserName(currPlayer.getUserName())
                    .setHeadImg(currPlayer.getHeadImg())
                    .setSex(currPlayer.getSex())
                    .setCurrScore(currPlayer.getCurrScore())
                    .setTotalScore(currPlayer.getTotalScore())
                    .setSeatIndex(currPlayer.getSeatIndex())
                    .setZhuangFlag(currPlayer.isZhuangFlag())
                    .setZiMo(currPlayer.isZiMo())
                    .setHu(currPlayer.isHu())
                    .setDianPao(currPlayer.isDianPao())
                    .setDiZhu(currPlayer.isDiZhu())
                    .setNongMin(currPlayer.isNongMin())
                    .setWinner(currPlayer.isWinner())
            );
        }
    }
}
