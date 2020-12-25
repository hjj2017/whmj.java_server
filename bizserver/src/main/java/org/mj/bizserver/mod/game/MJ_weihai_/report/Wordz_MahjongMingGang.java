package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

/**
 * 麻将明杠词条
 */
public class Wordz_MahjongMingGang implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 杠的是哪一张牌
     */
    private final MahjongTileDef _t;

    /**
     * 来自用户 Id, 从谁那里杠来的
     */
    private final int _fromUserId;

    /**
     * 类参数构造器
     *
     * @param userId     用户 Id
     * @param t          杠的是哪一张牌
     * @param fromUserId 来自用户 Id, 从谁那里杠来的
     */
    public Wordz_MahjongMingGang(int userId, MahjongTileDef t, int fromUserId) {
        _userId = userId;
        _t = t;
        _fromUserId = fromUserId;
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
     * 获取来自用户 Id, 从谁那杠来的
     *
     * @return 来自用户 Id
     */
    public int getFromUserId() {
        return _fromUserId;
    }

    /**
     * 构建麻将明杠消息
     *
     * @return 麻将明杠消息
     */
    private MJ_weihai_Protocol.MahjongChiPengGang buildMahjongMingGangMsg() {
        return MJ_weihai_Protocol.MahjongChiPengGang.newBuilder()
            .setKind(MahjongChiPengGang.KindDef.MING_GANG.getIntVal())
            .setTX(getTIntVal())
            .setFromUserId(_fromUserId)
            .build();
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.MahjongMingGangResult.newBuilder()
            .setMahjongMingGang(buildMahjongMingGangMsg())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongMingGangBroadcast.newBuilder()
            .setUserId(_userId)
            .setMahjongMingGang(buildMahjongMingGangMsg())
            .build();
    }

    @Override
    public JSONObject buildJSONObj() {
        final JSONObject jsonObj = new JSONObject(true);
        jsonObj.put("clazzName", this.getClass().getSimpleName());
        jsonObj.put("userId", _userId);
        jsonObj.put("t", getTIntVal());
        jsonObj.put("fromUserId", _fromUserId);

        return jsonObj;
    }
}
