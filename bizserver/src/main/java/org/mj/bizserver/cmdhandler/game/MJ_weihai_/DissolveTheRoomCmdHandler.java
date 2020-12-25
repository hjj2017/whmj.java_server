package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.RoomOverDetermine;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.DissolveRoomSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.timertask.AutoDissolutionVoteTask;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解散房间指令处理器
 */
public class DissolveTheRoomCmdHandler implements ICmdHandler<MJ_weihai_Protocol.DissolveTheRoomCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(DissolveTheRoomCmdHandler.class);

    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        MJ_weihai_Protocol.DissolveTheRoomCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        // 获取当前房间
        final Room currRoom = RoomGroup.getByUserId(fromUserId);

        if (null == currRoom) {
            LOGGER.error(
                "当前房间为空, userId = {}",
                fromUserId
            );
            return;
        }

        if (RoomOverDetermine.determine(currRoom) ||
            currRoom.isForcedEnd()) {
            // 如果已经结束,
            // 直接移除!
            // 无需处理什么解散逻辑...
            RoomGroup.removeByRoomId(currRoom.getRoomId());
            MJ_weihai_BizLogic.getInstance().cleanUpRedisAndFree_async(currRoom);
            return;
        }

        // 获取执行玩家
        final Player execPlayer = currRoom.getPlayerByUserId(fromUserId);

        if (null == execPlayer) {
            LOGGER.error(
                "玩家不在房间中, userId = {}, atRoomId = {}",
                fromUserId,
                currRoom.getRoomId()
            );
            return;
        }

        // 获取已有的解散会议,
        DissolveRoomSession sessionObj = currRoom.getDissolveRoomSession();

        if (null == sessionObj) {
            // 创建解散房间会议
            sessionObj = new DissolveRoomSession(
                execPlayer.getUserId(),
                cmdObj.getReason(),
                System.currentTimeMillis() + DissolveRoomSession.MAX_WAITING_TIME
            );
            currRoom.setDissolveRoomSession(sessionObj);
            currRoom.setTimerTask(new AutoDissolutionVoteTask(currRoom));
        }

        // 发起人肯定是同意解散房间的
        sessionObj.doVote(execPlayer.getUserId(), 1);

        // 获取第一局
        final Round round0 = currRoom.getRoundByIndex(0);

        if (null != round0 &&
            round0.isBegan()) {
            // 如果第一局已经开始,
            // 那么就需要等待其玩家同意才能解散房间...
            buildMsgAndSend(currRoom);
            return;
        }

        if (!execPlayer.isRoomOwner()) {
            LOGGER.error(
                "在第一局开始之前, 只有房主才能解散房间! userId = {}, atRoomId = {}",
                execPlayer.getUserId(),
                currRoom.getRoomId()
            );
            return;
        }

        // 当前房间强制结束并清理
        currRoom.setForcedEnd(true);
        currRoom.setDissolveRoomSession(null);
        currRoom.setTimerTask(null);

        // 移除当前房间
        RoomGroup.removeByRoomId(currRoom.getRoomId());
        MJ_weihai_BizLogic.getInstance().cleanUpRedisAndFree_async(
            currRoom, () -> GameBroadcaster.broadcast(
                currRoom, MJ_weihai_Protocol.DissolveSuccezzBroadcast.newBuilder().build()
            )
        );
    }

    /**
     * 构建消息并发送
     *
     * @param currRoom 当前房间
     */
    static void buildMsgAndSend(final Room currRoom) {
        if (null == currRoom) {
            return;
        }

        // 获取解散房间会议
        final DissolveRoomSession sessionObj = currRoom.getDissolveRoomSession();

        if (null == sessionObj) {
            return;
        }

        // 获取发起解散请求的玩家
        final Player fromPlayer = currRoom.getPlayerByUserId(
            sessionObj.getFromUserId()
        );

        if (null == fromPlayer) {
            LOGGER.error(
                "发起解散请求的玩家为空, fromUserId = {}",
                sessionObj.getFromUserId()
            );
            return;
        }

        // 计算剩余时间
        int remainTime = (int) (sessionObj.getWaitingOverTime() - System.currentTimeMillis());
        remainTime = Math.min(remainTime, DissolveRoomSession.MAX_WAITING_TIME);
        remainTime = Math.max(0, remainTime);

        MJ_weihai_Protocol.DissolveTheRoomBroadcast.Builder b = MJ_weihai_Protocol.DissolveTheRoomBroadcast.newBuilder()
            .setFromUserId(fromPlayer.getUserId())
            .setFromUserName(fromPlayer.getUserName())
            .setReason(sessionObj.getReasonOfDissolveRoom())
            .setRemainTime(remainTime);

        // 获取当前牌局和已经结束的牌局的数量
        final Round currRound = currRoom.getCurrRound();
        final int endedRoundCount = currRoom.getEndedRoundCount();

        if (null != currRound) {
            b.setCurrRoundIndex(currRound.getRoundIndex());
        } else {
            // 如果第一局还都没有开始,
            // 那么这个值将 = -1,
            // 如果第一局已经结束,
            // 那么这个值将 = 0...
            b.setCurrRoundIndex(endedRoundCount - 1);
        }

        for (Player currPlayer : currRoom.getPlayerListCopy()) {
            if (null == currPlayer) {
                continue;
            }

            b.addWaiting4User(
                MJ_weihai_Protocol.DissolveTheRoomBroadcast.Waiting4User.newBuilder()
                    .setUserId(currPlayer.getUserId())
                    .setUserName(currPlayer.getUserName())
                    .setHeadImg(currPlayer.getHeadImg())
                    .setRoomOwnerFlag(currPlayer.isRoomOwner())
                    .setSeatIndex(currPlayer.getSeatIndex())
                    .setYes(sessionObj.getYesByUserId(currPlayer.getUserId()))
            );
        }

        // 发送解散房间广播
        GameBroadcaster.broadcast(currRoom, b.build());
    }
}
