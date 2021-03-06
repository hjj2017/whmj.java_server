package org.mj.bizserver.cmdhandler.record;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.allmsg.RecordServerProtocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.record.RecordBizLogic;
import org.mj.bizserver.mod.record.bizdata.ARound;
import org.mj.bizserver.mod.record.bizdata.Player;
import org.mj.bizserver.mod.record.bizdata.RecordDetailz;
import org.mj.comm.cmdhandler.ICmdHandler;

import java.util.List;

/**
 * 获取战绩详情指令处理器
 */
public class GetRecordDetailzCmdHandler implements ICmdHandler<RecordServerProtocol.GetRecordDetailzCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        RecordServerProtocol.GetRecordDetailzCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        RecordBizLogic.getInstance().getRecordDetailz_async(
            cmdObj.getRoomUUId(),
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
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, BizResultWrapper<RecordDetailz> resultX) {
        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
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
        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
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
