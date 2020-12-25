package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 麻将胡牌或者自摸词条
 */
public class Wordz_MahjongHuOrZiMo implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 胡的是哪张牌
     */
    private final MahjongTileDef _t;

    /**
     * 是否胡 ( 他人点炮我胡牌 )
     */
    private final boolean _hu;

    /**
     * 是否自摸
     */
    private final boolean _ziMo;

    /**
     * 点炮用户 Id
     */
    private final int _dianPaoUserId;

    /**
     * 胡牌模式列表
     */
    private final Map<Integer, Integer> _huPatternMap;

    /**
     * 类参数构造器
     *
     * @param userId       用户 Id
     * @param t            胡的是哪张牌
     * @param huPatternMap 胡牌模式列表
     */
    public Wordz_MahjongHuOrZiMo(int userId, MahjongTileDef t, boolean hu, boolean ziMo, int dianPaoUserId, Map<Integer, Integer> huPatternMap) {
        _userId = userId;
        _t = t;
        _hu = hu;
        _ziMo = ziMo;
        _dianPaoUserId = dianPaoUserId;
        _huPatternMap = huPatternMap;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取胡的是哪张牌
     *
     * @return 麻将牌定义
     */
    public MahjongTileDef getT() {
        return _t;
    }

    /**
     * 获取胡的是哪张牌的整数值
     *
     * @return 整数值
     */
    public int getTIntVal() {
        return (null == _t) ? -1 : _t.getIntVal();
    }

    /**
     * 是否胡
     *
     * @return true = 胡, false = 不是
     */
    public boolean isHu() {
        return _hu;
    }

    /**
     * 是否自摸
     *
     * @return true = 自摸, false = 不是
     */
    public boolean isZiMo() {
        return _ziMo;
    }

    /**
     * 获取点炮用户 Id
     *
     * @return 点炮用户 Id
     */
    public int getDianPaoUserId() {
        return _dianPaoUserId;
    }

    /**
     * 获取胡牌模式字典
     *
     * @return 胡牌模式字典
     */
    public Map<Integer, Integer> getHuPatternMap() {
        return Objects.requireNonNullElse(_huPatternMap, Collections.emptyMap());
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.MahjongHuOrZiMoResult.newBuilder()
            .setT(getTIntVal())
            .setHu(_hu)
            .setZiMo(_ziMo)
            .setDianPaoUserId(_dianPaoUserId)
            .addAllHuPattern(getHuPatternKeyAndValList())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongHuOrZiMoBroadcast.newBuilder()
            .setUserId(_userId)
            .setT(getTIntVal())
            .setHu(_hu)
            .setZiMo(_ziMo)
            .setDianPaoUserId(_dianPaoUserId)
            .addAllHuPattern(getHuPatternKeyAndValList())
            .build();
    }

    /**
     * 获取胡牌模式键值列表
     *
     * @return 胡牌模式键值列表
     */
    private List<MJ_weihai_Protocol.KeyAndVal> getHuPatternKeyAndValList() {
        // 获取胡牌模式字典
        final Map<Integer, Integer> huPatternMap = getHuPatternMap();

        if (null == huPatternMap ||
            huPatternMap.isEmpty()) {
            return Collections.emptyList();
        }

        final List<MJ_weihai_Protocol.KeyAndVal> keyAndValList = new ArrayList<>(huPatternMap.size());

        for (Map.Entry<Integer, Integer> huPattern : huPatternMap.entrySet()) {
            if (null == huPattern ||
                null == huPattern.getKey() ||
                null == huPattern.getValue()) {
                continue;
            }

            keyAndValList.add(
                MJ_weihai_Protocol.KeyAndVal.newBuilder()
                    .setKey(huPattern.getKey())
                    .setVal(huPattern.getValue())
                    .build()
            );
        }

        return keyAndValList;
    }

    @Override
    public JSONObject buildJSONObj() {
        // 胡牌模式
        final JSONObject joHuPatternMap = new JSONObject(true);

        for (Map.Entry<Integer, Integer> entry : _huPatternMap.entrySet()) {
            joHuPatternMap.put(
                String.valueOf(entry.getKey()),
                entry.getValue()
            );
        }

        final JSONObject jsonObj = new JSONObject(true);
        jsonObj.put("clazzName", this.getClass().getSimpleName());
        jsonObj.put("userId", _userId);
        jsonObj.put("t", getTIntVal());
        jsonObj.put("hu", _hu);
        jsonObj.put("ziMo", _ziMo);
        jsonObj.put("dianPaoUserId", _dianPaoUserId);
        jsonObj.put("huPatternMap", joHuPatternMap);

        return jsonObj;
    }
}
