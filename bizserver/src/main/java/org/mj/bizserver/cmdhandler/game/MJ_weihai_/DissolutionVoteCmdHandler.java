package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.RoomOverDetermine;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.DissolveRoomSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解散投票指令处理器
 */
public class DissolutionVoteCmdHandler implements ICmdHandler<MyCmdHandlerContext, MJ_weihai_Protocol.DissolutionVoteCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(DissolutionVoteCmdHandler.class);

    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        MJ_weihai_Protocol.DissolutionVoteCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        int fromUserId = ctx.getFromUserId();

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
            // 直接移除...
            RoomGroup.removeByRoomId(currRoom.getRoomId());
            return;
        }

        // 获取解散房间会议
        DissolveRoomSession sessionObj = currRoom.getDissolveRoomSession();

        if (null == sessionObj) {
            LOGGER.error(
                "当前房间没有发起解散, userId = {}, roomId = {}",
                fromUserId,
                currRoom.getRoomId()
            );
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

        if (-1 != sessionObj.getYesByUserId(execPlayer.getUserId())) {
            LOGGER.warn(
                "玩家已经同意或拒绝解散房间, 不能再次投票, userId = {}, roomId = {}",
                execPlayer.getUserId(),
                currRoom.getRoomId()
            );
            return;
        }

        // 执行投票
        sessionObj.doVote(fromUserId, cmdObj.getYes());

        // 构建广播消息并发送
        final GeneratedMessageV3 msgObj = MJ_weihai_Protocol.DissolutionVoteBroadcast.newBuilder()
            .setUserId(execPlayer.getUserId())
            .setYes(cmdObj.getYes())
            .build();

        GameBroadcaster.broadcast(currRoom, msgObj);

        if (0 == cmdObj.getYes()) {
            LOGGER.info(
                "玩家拒绝解散房间, userId = {}, roomId = {}",
                execPlayer.getUserId(),
                currRoom.getRoomId()
            );

            currRoom.setDissolveRoomSession(null);
            return;
        }

        // 测试是否全票通过并处理
        testUnanimouzApprovalAndDispose(currRoom);
    }

    /**
     * 测试是否全票通过并处理
     *
     * @param currRoom 当前房间
     */
    static private void testUnanimouzApprovalAndDispose(final Room currRoom) {
        if (null == currRoom) {
            return;
        }

        // 获取解散房间会议
        DissolveRoomSession sessionObj = currRoom.getDissolveRoomSession();

        if (null == sessionObj) {
            return;
        }

        for (Player currPlayer : currRoom.getPlayerListCopy()) {
            if (null == currPlayer) {
                continue;
            }

            if (1 != sessionObj.getYesByUserId(currPlayer.getUserId())) {
                // 如果不是全票通过,
                return;
            }
        }

        LOGGER.info(
            "全票通过, 执行房间解散逻辑! roomId = {}",
            currRoom.getRoomId()
        );

        if (null != currRoom.getCurrRound()) {
            // 当前牌局强制结束
            currRoom.getCurrRound().setEnded(true);
        }

        // 当前房间强制结束并清理
        currRoom.setForcedEnd(true);
        currRoom.setDissolveRoomSession(null);
        currRoom.setTimerTask(null);

        // 移除当前房间并清理 Redis
        RoomGroup.removeByRoomId(currRoom.getRoomId());
        MJ_weihai_BizLogic.getInstance().cleanUpRedisAndFree_async(currRoom, () -> {
            // 发送解散成功消息广播
            GameBroadcaster.broadcast(
                currRoom,
                MJ_weihai_Protocol.DissolveSuccezzBroadcast.newBuilder().build()
            );

            // 发送当前房间结算消息
            RoomSettlementPostman.post(currRoom);
        });
    }
}
