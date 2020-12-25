package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

/**
 * 麻将暗杠词条
 */
public class Wordz_MahjongAnGang implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 杠的是哪一张牌
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
     * @param t      杠的是哪一张拍
     */
    public Wordz_MahjongAnGang(int userId, MahjongTileDef t) {
        this(userId, t, false);
    }

    /**
     * 类参数构造器
     *
     * @param userId 用户 Id
     * @param t      杠的是哪一张牌
     * @param mask   是否遮挡
     */
    public Wordz_MahjongAnGang(int userId, MahjongTileDef t, boolean mask) {
        _userId = userId;
        _t = t;
        _mask = mask;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取杠的是哪一张牌
     *
     * @return 麻将牌定义
     */
    public MahjongTileDef getT() {
        return _t;
    }

    /**
     * 获取杠的是哪一张牌整数值
     *
     * @return 整数值
     */
    public int getTIntVal() {
        return (null == _t) ? -1 : _t.getIntVal();
    }

    /**
     * 是否遮挡
     *
     * @return true = 遮挡, false = 不遮挡
     */
    public boolean isMask() {
        return _mask;
    }

    /**
     * 构建麻将暗杠消息
     *
     * @return 麻将暗杠消息对象
     */
    private MJ_weihai_Protocol.MahjongChiPengGang buildMahjongAnGangMsg() {
        return MJ_weihai_Protocol.MahjongChiPengGang.newBuilder()
            .setKind(MahjongChiPengGang.KindDef.AN_GANG.getIntVal())
            .setTX(_mask ? MahjongTileDef.MASK_VAL : getTIntVal())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.MahjongAnGangResult.newBuilder()
            .setMahjongAnGang(buildMahjongAnGangMsg())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongAnGangBroadcast.newBuilder()
            .setUserId(_userId)
            .setMahjongAnGang(buildMahjongAnGangMsg())
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
    public Wordz_MahjongAnGang createMaskCopy() {
        return new Wordz_MahjongAnGang(_userId, _t, true);
    }
}
