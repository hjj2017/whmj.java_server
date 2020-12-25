package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

/**
 * 麻将补杠词条
 */
public class Wordz_MahjongBuGang implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 杠的是哪一张牌
     */
    private final MahjongTileDef _t;

    /**
     * 类参数构造器
     *
     * @param userId 用户 Id
     * @param t      杠的是哪一张拍
     */
    public Wordz_MahjongBuGang(int userId, MahjongTileDef t) {
        _userId = userId;
        _t = t;
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
     * 构建麻将暗杠消息
     *
     * @return 麻将暗杠消息对象
     */
    private MJ_weihai_Protocol.MahjongChiPengGang buildMahjongBuGangMsg() {
        return MJ_weihai_Protocol.MahjongChiPengGang.newBuilder()
            .setKind(MahjongChiPengGang.KindDef.BU_GANG.getIntVal())
            .setTX(getTIntVal())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.MahjongBuGangResult.newBuilder()
            .setMahjongBuGang(buildMahjongBuGangMsg())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongBuGangBroadcast.newBuilder()
            .setUserId(_userId)
            .setMahjongBuGang(buildMahjongBuGangMsg())
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
}
