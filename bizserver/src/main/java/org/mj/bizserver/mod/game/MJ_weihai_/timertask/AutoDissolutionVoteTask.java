package org.mj.bizserver.mod.game.MJ_weihai_.timertask;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.cmdhandler.game.MJ_weihai_.GameBroadcaster;
import org.mj.bizserver.cmdhandler.game.MJ_weihai_.RoomSettlementPostman;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.DissolveRoomSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

/**
 * 自动解散房间任务
 */
public class AutoDissolutionVoteTask implements ITimerTask {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AutoDissolutionVoteTask.class);

    /**
     * 目标房间
     */
    private final WeakReference<Room> _targetRoom;

    /**
     * 类参数构造器
     *
     * @param targetRoom 目标房间
     */
    public AutoDissolutionVoteTask(
        Room targetRoom) {
        _targetRoom = new WeakReference<>(targetRoom);
    }

    @Override
    public long getRunAtTime() {
        // 获取当前房间
        final Room currRoom = _targetRoom.get();

        if (null == currRoom) {
            return -1;
        }

        // 获取解散房间会议
        final DissolveRoomSession sessionObj = currRoom.getDissolveRoomSession();

        if (null == sessionObj) {
            return -1;
        }

        return sessionObj.getWaitingOverTime();
    }

    @Override
    public void doTask() {
        // 获取要解散的房间
        final Room currRoom = _targetRoom.get();

        if (null == currRoom) {
            return;
        }

        // 获取解散会议
        final DissolveRoomSession sessionObj = currRoom.getDissolveRoomSession();

        if (null == sessionObj) {
            return;
        }

        LOGGER.error(
            "时间到, 强制解散房间! roomId = {}",
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
        this._targetRoom.clear();

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
