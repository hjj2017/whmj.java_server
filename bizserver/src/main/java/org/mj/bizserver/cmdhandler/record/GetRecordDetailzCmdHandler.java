package org.mj.bizserver.cmdhandler.record;

import org.mj.bizserver.allmsg.RecordServerProtocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.record.RecordBizLogic;
import org.mj.bizserver.mod.record.bizdata.ARound;
import org.mj.bizserver.mod.record.bizdata.Player;
import org.mj.bizserver.mod.record.bizdata.RecordDetailz;
import org.mj.comm.cmdhandler.ICmdHandler;

import java.util.List;

/**
 * 获取战绩详情指令处理器
 */
public class GetRecordDetailzCmdHandler implements ICmdHandler<MyCmdHandlerContext, RecordServerProtocol.GetRecordDetailzCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        RecordServerProtocol.GetRecordDetailzCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        RecordBizLogic.getInstance().getRecordDetailz_async(
            cmdObj.getRoomUUId(),
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
        MyCmdHandlerContext ctx, BizResultWrapper<RecordDetailz> resultX) {
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

        final RecordDetailz recordDetailz = resultX.getFinalResult();

        RecordServerProtocol.GetRecordDetailzResult.Builder b0 = RecordServerProtocol.GetRecordDetailzResult.newBuilder()
            .setGameType0(recordDetailz.getGameType0IntVal())
            .setGameType1(recordDetailz.getGameType1IntVal())
            .setRoomId(recordDetailz.getRoomId())
            .setRoomUUId(recordDetailz.getRoomUUId())
            .setCostRoomCard(recordDetailz.getCostRoomCard())
            .setActualRoundCount(recordDetailz.getActualRoundCount())
            .setCreateTime(recordDetailz.getCreateTime());

        // 填充牌局列表
        fillRoundList(b0, recordDetailz.getRoundList());

        RecordServerProtocol.GetRecordDetailzResult r = b0.build();
        
        ctx.writeAndFlush(r);
    }

    /**
     * 填充牌局列表
     *
     * @param rootBuilder 根构建器
     * @param roundList   牌局列表
     */
    static private void fillRoundList(
        RecordServerProtocol.GetRecordDetailzResult.Builder rootBuilder,
        List<ARound> roundList) {
        if (null == rootBuilder ||
            null == roundList ||
            roundList.isEmpty()) {
            return;
        }

        for (ARound currRound : roundList) {
            if (null == currRound) {
                continue;
            }

            RecordServerProtocol.GetRecordDetailzResult.ARound.Builder
                b1 = RecordServerProtocol.GetRecordDetailzResult.ARound.newBuilder()
                .setRoundIndex(currRound.getRoundIndex())
                .setCreateTime(currRound.getCreateTime())
                .setPlaybackStub(currRound.getPlaybackStub());

            // 填充玩家列表
            fillPlayerList(b1, currRound.getPlayerList());

            rootBuilder.addRound(b1);
        }
    }

    /**
     * 填充玩家列表
     *
     * @param rootBuilder 根构建器
     * @param playerList  玩家列表
     */
    static private void fillPlayerList(
        RecordServerProtocol.GetRecordDetailzResult.ARound.Builder rootBuilder,
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
