package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

/**
 * 麻将吃牌词条
 */
public class Wordz_MahjongChi implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 吃牌
     */
    private final MahjongTileDef _tChi;

    /**
     * 第一张牌
     */
    private final MahjongTileDef _t0;

    /**
     * 第二张牌
     */
    private final MahjongTileDef _t1;

    /**
     * 第三张牌
     */
    private final MahjongTileDef _t2;

    /**
     * 来自用户 Id
     */
    private final int _fromUserId;

    /**
     * 类参数构造器
     *
     * @param userId     用户 Id
     * @param tChi       吃牌
     * @param t0         第一张牌
     * @param t1         第二张牌
     * @param t2         第三张牌
     * @param fromUserId 来自用户 Id, 从谁那里吃的
     */
    public Wordz_MahjongChi(
        int userId, MahjongTileDef tChi, MahjongTileDef t0, MahjongTileDef t1, MahjongTileDef t2, int fromUserId) {
        _userId = userId;
        _tChi = tChi;
        _t0 = t0;
        _t1 = t1;
        _t2 = t2;
        _fromUserId = fromUserId;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取吃牌
     *
     * @return 吃牌
     */
    public MahjongTileDef getTChi() {
        return _tChi;
    }

    /**
     * 获取吃牌整数值
     *
     * @return 整数值
     */
    public int getTChiIntVal() {
        return (null == _tChi) ? -1 : _tChi.getIntVal();
    }

    /**
     * 获取第一张牌
     *
     * @return 第一张牌
     */
    public MahjongTileDef getT0() {
        return _t0;
    }

    /**
     * 获取第一张牌整数值
     *
     * @return 整数值
     */
    public int getT0IntVal() {
        return (null == _t0) ? -1 : _t0.getIntVal();
    }

    /**
     * 获取第二张牌
     *
     * @return 第二张牌
     */
    public MahjongTileDef getT1() {
        return _t1;
    }

    /**
     * 获取第二张牌整数值
     *
     * @return 整数值
     */
    public int getT1IntVal() {
        return (null == _t1) ? -1 : _t1.getIntVal();
    }

    /**
     * 获取第三张牌
     *
     * @return 第三张牌
     */
    public MahjongTileDef getT2() {
        return _t2;
    }

    /**
     * 获取第三张牌整数值
     *
     * @return 整数值
     */
    public int getT2IntVal() {
        return (null == _t2) ? -1 : _t2.getIntVal();
    }

    /**
     * 来自用户 Id, 从谁那里吃的
     *
     * @return 来自用户 Id
     */
    public int getFromUserId() {
        return _fromUserId;
    }

    /**
     * 构建麻将吃牌消息
     *
     * @return 麻将吃牌消息
     */
    private MJ_weihai_Protocol.MahjongChiPengGang buildMahjongChiMsg() {
        return MJ_weihai_Protocol.MahjongChiPengGang.newBuilder()
            .setKind(MahjongChiPengGang.KindDef.CHI.getIntVal())
            .setTX(getTChiIntVal())
            .setT0(getT0IntVal())
            .setT1(getT1IntVal())
            .setT2(getT2IntVal())
            .setFromUserId(_fromUserId)
            .build();
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.MahjongChiResult.newBuilder()
            .setMahjongChi(buildMahjongChiMsg())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongChiBroadcast.newBuilder()
            .setUserId(_userId)
            .setMahjongChi(buildMahjongChiMsg())
            .build();
    }

    @Override
    public JSONObject buildJSONObj() {
        final JSONObject jsonObj = new JSONObject(true);
        jsonObj.put("clazzName", this.getClass().getSimpleName());
        jsonObj.put("userId", _userId);
        jsonObj.put("t0", getT0IntVal());
        jsonObj.put("t1", getT1IntVal());
        jsonObj.put("t2", getT2IntVal());
        jsonObj.put("tChi", getTChiIntVal());
        jsonObj.put("fromUserId", _fromUserId);

        return jsonObj;
    }
}
