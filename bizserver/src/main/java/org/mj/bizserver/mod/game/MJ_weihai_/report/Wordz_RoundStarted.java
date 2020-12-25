package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;

/**
 * 开局词条
 */
public class Wordz_RoundStarted implements IWordz {
    /**
     * 当前牌局索引
     */
    private final int _currRoundIndex;

    /**
     * 庄家用户 Id
     */
    private final int _zhuangJiaUserId;

    /**
     * 类参数构造器
     *
     * @param currRoundIndex  当前牌局索引
     * @param zhuangJiaUserId 庄家用户 Id
     */
    public Wordz_RoundStarted(int currRoundIndex, int zhuangJiaUserId) {
        _currRoundIndex = currRoundIndex;
        _zhuangJiaUserId = zhuangJiaUserId;
    }

    /**
     * 获取当前牌局索引
     *
     * @return 当前牌局索引
     */
    public int getCurrRoundIndex() {
        return _currRoundIndex;
    }

    /**
     * 获取庄家用户 Id
     *
     * @return 庄家用户 Id
     */
    public int getZhuangJiaUserId() {
        return _zhuangJiaUserId;
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return null;
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.RoundStartedBroadcast.newBuilder()
            .setCurrRoundIndex(_currRoundIndex)
            .setZhuangJiaUserId(_zhuangJiaUserId)
            .build();
    }
}
