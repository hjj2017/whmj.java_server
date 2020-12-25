package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

/**
 * 麻将摸牌词条
 */
public class Wordz_MahjongMoPai implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 摸牌
     */
    private final MahjongTileDef _t;

    /**
     * 是否遮挡
     */
    private final boolean _mask;

    /**
     * 类参数构造器
     *
     * @param userId 用户 Id
     * @param moPai  摸牌
     */
    public Wordz_MahjongMoPai(int userId, MahjongTileDef moPai) {
        this(userId, moPai, false);
    }

    /**
     * 类参数构造器
     *
     * @param userId 用户 Id
     * @param t      摸牌
     * @param mask   是否遮挡
     */
    public Wordz_MahjongMoPai(int userId, MahjongTileDef t, boolean mask) {
        _userId = userId;
        _t = t;
        _mask = mask;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取摸牌
     *
     * @return 摸牌
     */
    public MahjongTileDef getT() {
        return _t;
    }

    /**
     * 获取摸牌整数数值
     *
     * @return 整数数值
     */
    public int getTIntVal() {
        return (null == _t) ? -1 : _t.getIntVal();
    }

    /**
     * 是否遮掩
     *
     * @return true = 遮掩, fase = 不遮掩
     */
    public boolean isMask() {
        return _mask;
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.MahjongMoPaiResult.newBuilder()
            .setT(getTIntVal())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongMoPaiBroadcast.newBuilder()
            .setUserId(_userId)
            .setT(_mask ? MahjongTileDef.MASK_VAL : getTIntVal())
            .build();
    }

    @Override
    public JSONObject buildJSONObj() {
        final JSONObject jsonObj = new JSONObject(true);
        jsonObj.put("clazzName", this.getClass().getSimpleName());
        jsonObj.put("userId", _userId);
        jsonObj.put("t", getTIntVal());

        return jsonObj;
    }

    @Override
    public Wordz_MahjongMoPai createMaskCopy() {
        return new Wordz_MahjongMoPai(_userId, _t, true);
    }
}
