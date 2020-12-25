package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongLiangFeng;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.Collections;
import java.util.Map;

/**
 * 麻将补风词条
 */
public class Wordz_MahjongBuFeng implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 亮风种类
     */
    private final MahjongLiangFeng.KindDef _kind;

    /**
     * 计数器字典
     */
    private final Map<MahjongTileDef, Integer> _counterMap;

    /**
     * 类参数构造器
     *
     * @param userId     用户 Id
     * @param kind       亮风种类
     * @param counterMap 计数器字典
     */
    public Wordz_MahjongBuFeng(int userId, MahjongLiangFeng.KindDef kind, Map<MahjongTileDef, Integer> counterMap) {
        _userId = userId;
        _kind = kind;
        _counterMap = counterMap;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取亮风种类
     *
     * @return 亮风种类
     */
    public MahjongLiangFeng.KindDef getKind() {
        return _kind;
    }

    /**
     * 获取亮风种类整数值
     *
     * @return 亮风种类整数值
     */
    public int getKindIntVal() {
        return (null == _kind) ? -1 : _kind.getIntVal();
    }

    /**
     * 获取计数器字典副本
     *
     * @return 计数器字典副本
     */
    public Map<MahjongTileDef, Integer> getCounterMapCopy() {
        if (null == _counterMap) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(_counterMap);
        }
    }

    /**
     * 构建麻将亮风消息
     *
     * @return 麻将亮风消息
     */
    private MJ_weihai_Protocol.MahjongLiangFeng buildMahjongLiangFengMsg() {
        // 获取计数器字典
        Map<MahjongTileDef, Integer> counterMap = _counterMap;

        if (null == counterMap) {
            counterMap = Collections.emptyMap();
        }

        int nKind = (null == _kind) ? -1 : _kind.getIntVal();

        return MJ_weihai_Protocol.MahjongLiangFeng.newBuilder()
            .setKind(nKind)
            .setNumOfDongFeng(counterMap.getOrDefault(MahjongTileDef.DONG_FENG, 0))
            .setNumOfNanFeng(counterMap.getOrDefault(MahjongTileDef.NAN_FENG, 0))
            .setNumOfXiFeng(counterMap.getOrDefault(MahjongTileDef.XI_FENG, 0))
            .setNumOfBeiFeng(counterMap.getOrDefault(MahjongTileDef.BEI_FENG, 0))
            .setNumOfHongZhong(counterMap.getOrDefault(MahjongTileDef.HONG_ZHONG, 0))
            .setNumOfFaCai(counterMap.getOrDefault(MahjongTileDef.FA_CAI, 0))
            .setNumOfBaiBan(counterMap.getOrDefault(MahjongTileDef.BAI_BAN, 0))
            .build();
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.MahjongBuFengResult.newBuilder()
            .setMahjongLiangFeng(buildMahjongLiangFengMsg())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongBuFengBroadcast.newBuilder()
            .setUserId(_userId)
            .setMahjongLiangFeng(buildMahjongLiangFengMsg())
            .build();
    }

    @Override
    public JSONObject buildJSONObj() {
        if (null == _counterMap ||
            _counterMap.size() <= 0) {
            return null;
        }

        final JSONObject joCounterMap = new JSONObject(true);

        for (Map.Entry<MahjongTileDef, Integer> entry : _counterMap.entrySet()) {
            joCounterMap.put(
                String.valueOf(entry.getKey().getIntVal()),
                entry.getValue()
            );
        }

        final JSONObject jsonObj = new JSONObject(true);
        jsonObj.put("clazzName", this.getClass().getSimpleName());
        jsonObj.put("userId", _userId);
        jsonObj.put("kind", getKindIntVal());
        jsonObj.put("counterMap", joCounterMap);

        return jsonObj;
    }
}
