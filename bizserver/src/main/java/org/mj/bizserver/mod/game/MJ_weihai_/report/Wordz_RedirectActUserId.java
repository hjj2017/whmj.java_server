package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;

/**
 * 重定向活动用户 Id
 */
public class Wordz_RedirectActUserId implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 座位索引
     */
    private final int _seatIndex;

    /**
     * 当前牌局索引
     */
    private final int _currRoundIndex;

    /**
     * 剩余时间
     */
    private final int _remainTime;

    /**
     * 剩余卡牌数量
     */
    private final int _remainCardNum;

    /**
     * 类参数构造器
     *
     * @param userId         用户 Id
     * @param seatIndex      座位索引
     * @param currRoundIndex 当前牌局索引
     * @param remainCardNum  剩余卡牌数量
     * @param remainTime     剩余时间
     */
    public Wordz_RedirectActUserId(int userId, int seatIndex, int currRoundIndex, int remainCardNum, int remainTime) {
        _userId = userId;
        _seatIndex = seatIndex;
        _currRoundIndex = currRoundIndex;
        _remainCardNum = remainCardNum;
        _remainTime = remainTime;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取座位索引
     *
     * @return 座位索引
     */
    public int getSeatIndex() {
        return _seatIndex;
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
     * 获取剩余卡牌数量
     *
     * @return 剩余卡牌数量
     */
    public int getRemainCardNum() {
        return _remainCardNum;
    }

    /**
     * 获取剩余时间 ( 单位 = 秒 )
     *
     * @return 剩余时间
     */
    public int getRemainTime() {
        return _remainTime;
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return null;
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.RedirectActUserIdBroadcast.newBuilder()
            .setUserId(_userId)
            .setSeatIndex(_seatIndex)
            .setCurrRoundIndex(_currRoundIndex)
            .setRemainCardNum(_remainCardNum)
            .setRemainTime(_remainTime)
            .build();
    }

    @Override
    public JSONObject buildJSONObj() {
        final JSONObject jsonObj = new JSONObject(true);
        jsonObj.put("clazzName", this.getClass().getSimpleName());
        jsonObj.put("userId", _userId);
        jsonObj.put("seatIndex", _seatIndex);
        jsonObj.put("currRoundIndex", _currRoundIndex);
        jsonObj.put("remainCardNum", _remainCardNum);
        jsonObj.put("remainTime", _remainTime);

        return jsonObj;
    }
}
