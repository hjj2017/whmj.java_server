package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

/**
 * 麻将碰牌词条
 */
public class Wordz_MahjongPeng implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 碰牌
     */
    private final MahjongTileDef _t;

    /**
     * 来自用户 Id
     */
    private final int _fromUserId;

    /**
     * 类参数构造器
     *
     * @param userId     用户 Id
     * @param t          碰牌
     * @param fromUserId 来自用户 Id, 从谁那里碰的
     */
    public Wordz_MahjongPeng(int userId, MahjongTileDef t, int fromUserId) {
        _userId = userId;
        _t = t;
        _fromUserId = fromUserId;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取碰牌
     *
     * @return 碰牌
     */
    public MahjongTileDef getT() {
        return _t;
    }

    /**
     * 获取碰牌整数值
     *
     * @return 整数值
     */
    public int getTIntVal() {
        return (null == _t) ? -1 : _t.getIntVal();
    }

    /**
     * 获取来自用户 Id
     *
     * @return 来自用户 Id
     */
    public int getFromUserId() {
        return _fromUserId;
    }

    /**
     * 构建麻将碰牌消息
     *
     * @return 麻将碰牌消息
     */
    private MJ_weihai_Protocol.MahjongChiPengGang buildMahjongPengMsg() {
        return MJ_weihai_Protocol.MahjongChiPengGang.newBuilder()
            .setKind(MahjongChiPengGang.KindDef.PENG.getIntVal())
            .setTX(getTIntVal())
            .setFromUserId(_fromUserId)
            .build();
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.MahjongPengResult.newBuilder()
            .setMahjongPeng(buildMahjongPengMsg())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongPengBroadcast.newBuilder()
            .setUserId(_userId)
            .setMahjongPeng(buildMahjongPengMsg())
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
