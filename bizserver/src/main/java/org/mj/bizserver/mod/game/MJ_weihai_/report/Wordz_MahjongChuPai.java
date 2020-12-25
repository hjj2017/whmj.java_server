package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

/**
 * 麻将出牌
 */
public class Wordz_MahjongChuPai implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 麻将出牌
     */
    private final MahjongTileDef _t;

    /**
     * 类参数构造器
     *
     * @param userId 用户 Id
     * @param t      麻将出牌
     */
    public Wordz_MahjongChuPai(int userId, MahjongTileDef t) {
        _userId = userId;
        _t = t;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取麻将出牌
     *
     * @return 麻将出牌
     */
    public MahjongTileDef getT() {
        return _t;
    }

    /**
     * 获取麻将出牌整数值
     *
     * @return 整数值
     */
    public int getTIntVal() {
        return (null == _t) ? -1 : _t.getIntVal();
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.MahjongChuPaiResult.newBuilder()
            .setT(getTIntVal())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongChuPaiBroadcast.newBuilder()
            .setUserId(_userId)
            .setT(getTIntVal())
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
