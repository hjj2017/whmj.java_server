package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 解散房间会议
 */
public final class DissolveRoomSession {
    /**
     * 解散房间最大等待秒数
     */
    static public final int MAX_WAITING_TIME = 300 * 1000;

    /**
     * 发起人用户 Id
     */
    private final int _fromUserId;

    /**
     * 解散原因
     */
    private final int _reasonOfDissolveRoom;

    /**
     * 等待结束时间戳
     */
    private final long _waitingOverTime;

    /**
     * 投票字典
     */
    private final Map<Integer, Integer> _voteMap = new ConcurrentHashMap<>();

    /**
     * 类参数构造器
     *
     * @param fromUserId           发起人用户 Id
     * @param reasonOfDissolveRoom 解散房间的原因
     * @param waitingOverTime      等待结束时间戳
     */
    public DissolveRoomSession(int fromUserId, int reasonOfDissolveRoom, long waitingOverTime) {
        _fromUserId = fromUserId;
        _reasonOfDissolveRoom = reasonOfDissolveRoom;
        _waitingOverTime = waitingOverTime;
    }

    /**
     * 获取发起人用户 Id
     *
     * @return 发起人用户 Id
     */
    public int getFromUserId() {
        return _fromUserId;
    }

    /**
     * 获取解散房间的原因
     *
     * @return 解散房间的原因, 0 = 有人掉线, 1 = 有急事
     */
    public int getReasonOfDissolveRoom() {
        return _reasonOfDissolveRoom;
    }

    /**
     * 执行投票
     *
     * @param userId 用户 Id
     * @param yes    是否同意, -1 = 等待, 0 = 拒绝, 1 = 已同意
     */
    public void doVote(int userId, int yes) {
        _voteMap.put(userId, yes);
    }

    /**
     * 获取是否同意
     *
     * @param userId 用户 Id
     * @return 是否同意, -1 = 等待, 0 = 拒绝, 1 = 已同意
     */
    public int getYesByUserId(int userId) {
        return _voteMap.getOrDefault(userId, -1);
    }

    /**
     * 获取等待结束时间戳
     *
     * @return 等待结束时间戳
     */
    public long getWaitingOverTime() {
        return _waitingOverTime;
    }
}
