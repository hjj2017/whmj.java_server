package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

/**
 * 麻将亮杠腚词条
 */
public class Wordz_MahjongLiangGangDing implements IWordz {
    /**
     * 第一张牌
     */
    private final MahjongTileDef _t0;

    /**
     * 第二张牌
     */
    private MahjongTileDef _t1;

    /**
     * 类参数构造器
     *
     * @param t0 第一张牌
     */
    public Wordz_MahjongLiangGangDing(MahjongTileDef t0) {
        _t0 = t0;
    }

    /**
     * 获取第一张麻将牌
     *
     * @return 第一张麻将牌
     */
    public MahjongTileDef getT0() {
        return _t0;
    }

    /**
     * 获取第一张麻将牌整数值
     *
     * @return 第一张麻将牌整数值
     */
    public int getT0IntVal() {
        return null == _t0 ? -1 : _t0.getIntVal();
    }

    /**
     * 获取第二张麻将牌
     *
     * @return 第二张麻将牌
     */
    public MahjongTileDef getT1() {
        return _t1;
    }

    /**
     * 获取第一张麻将牌整数值
     *
     * @return 第一张麻将牌整数值
     */
    public int getT1IntVal() {
        return null == _t1 ? -1 : _t1.getIntVal();
    }

    /**
     * 设置第二张牌
     *
     * @param val 枚举对象
     */
    public void setT1(MahjongTileDef val) {
        _t1 = val;
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return null;
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongLiangGangDingBroadcast.newBuilder()
            .setT0(getT0IntVal())
            .setT1(getT1IntVal())
            .build();
    }
}
