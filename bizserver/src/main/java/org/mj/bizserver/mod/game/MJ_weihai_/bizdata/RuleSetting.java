package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.PaymentWayEnum;

import java.util.Map;

/**
 * 规则设置
 */
public final class RuleSetting {
    /**
     * 支付方式
     */
    static final int KEY_PAYMENT_WAY = 1002;

    /**
     * 玩家数量
     */
    static final int KEY_MAX_PLAYER = 1003;

    /**
     * 最大局数
     */
    static final int KEY_MAX_ROUND = 1004;

    /**
     * 最大圈数
     */
    static final int KEY_MAX_CIRCLE = 1005;

    /**
     * 玩法 - 夹档
     */
    static final int KEY_PLAY_METHOD_JIA_DANG = 2001;

    /**
     * 玩法 - 一炮多响
     */
    static final int KEY_PLAY_METHOD_YI_PAO_DUO_XIANG = 2002;

    /**
     * 玩法 - 乱锚
     */
    static final int KEY_PLAY_METHOD_LUAN_MAO = 2003;

    /**
     * 玩法 - 不荒庄
     */
    static final int KEY_PLAY_METHOD_BU_HUANG_ZHUANG = 2004;

    /**
     * 玩法 - 只碰不吃
     */
    static final int KEY_PLAY_METHOD_ZHI_PENG_BU_CHI = 2005;

    /**
     * 玩法 - 夹五
     */
    static final int KEY_PLAY_METHOD_JIA_WU = 2006;

    /**
     * 玩法 - 不带风
     */
    static final int KEY_PLAY_METHOD_BU_DAI_FENG = 2007;

    /**
     * 玩法 - 亮杠腚
     */
    static final int KEY_PLAY_METHOD_LIANG_GANG_DING = 2008;

    /**
     * 玩法 - 飘分
     */
    static final int KEY_PLAY_METHOD_PIAO_FEN = 2009;

    /**
     * 玩法 - 64 番封顶
     */
    static final int KEY_PLAY_METHOD_64_FAN_FENG_DING = 2010;

    /**
     * 内置字典
     */
    private final Map<Integer, Integer> _innerMap;

    /**
     * 类参数构造器
     *
     * @param ruleMap 规则字典
     * @throws IllegalArgumentException if null == ruleMap || ruleMap.isEmpty
     */
    public RuleSetting(Map<Integer, Integer> ruleMap) {
        if (null == ruleMap ||
            ruleMap.isEmpty()) {
            throw new IllegalArgumentException("ruleMap");
        }

        _innerMap = ruleMap;
    }

    /**
     * 获取内置字典
     *
     * @return 内置字典
     */
    public Map<Integer, Integer> getInnerMap() {
        return _innerMap;
    }

    /**
     * 获取支付方式
     *
     * @return 支付方式
     */
    public PaymentWayEnum getPaymentWay() {
        return PaymentWayEnum.valueOf(_innerMap.getOrDefault(
            KEY_PAYMENT_WAY, 0
        ));
    }

    /**
     * 获取玩家数量
     *
     * @return 玩家数量
     */
    public int getMaxPlayer() {
        return _innerMap.getOrDefault(KEY_MAX_PLAYER, 2);
    }

    /**
     * 获取最大局数
     *
     * @return 最大局数
     */
    public int getMaxRound() {
        return _innerMap.getOrDefault(KEY_MAX_ROUND, -1);
    }

    /**
     * 获取最大圈数
     *
     * @return 最大圈数
     */
    public int getMaxCircle() {
        return _innerMap.getOrDefault(KEY_MAX_CIRCLE, -1);
    }

    /**
     * 是否夹档
     *
     * @return true = 夹档, false = 不夹档
     */
    public boolean isJiaDang() {
        return _innerMap.getOrDefault(KEY_PLAY_METHOD_JIA_DANG, 0) == 1;
    }

    /**
     * 是否一炮多响
     *
     * @return true = 一炮多响, false = 不是
     */
    public boolean isYiPaoDuoXiang() {
        return _innerMap.getOrDefault(KEY_PLAY_METHOD_YI_PAO_DUO_XIANG, 0) == 1;
    }

    /**
     * 是否乱锚
     *
     * @return true = 乱锚, false = 不是
     */
    public boolean isLuanMao() {
        return _innerMap.getOrDefault(KEY_PLAY_METHOD_LUAN_MAO, 0) == 1;
    }

    /**
     * 是否不荒庄,
     * 如果勾选了不荒庄, 那么将所有的牌都抓完!
     * 如果没有勾选不荒庄, 剩余 14 张牌不抓
     *
     * @return true = 不荒庄, false = 荒庄
     */
    public boolean isBuHuangZhuang() {
        return _innerMap.getOrDefault(KEY_PLAY_METHOD_BU_HUANG_ZHUANG, 0) == 1;
    }

    /**
     * 是否只碰不吃
     *
     * @return true = 只碰不吃, false = 可以碰也可以吃
     */
    public boolean isZhiPengBuChi() {
        return _innerMap.getOrDefault(KEY_PLAY_METHOD_ZHI_PENG_BU_CHI, 0) == 1;
    }

    /**
     * 是否夹五
     *
     * @return true = 夹五, false = 不是夹五
     */
    public boolean isJiaWu() {
        return _innerMap.getOrDefault(KEY_PLAY_METHOD_JIA_WU, 0) == 1;
    }

    /**
     * 是否不带风,
     * 如果选择了不带风,
     * 那么 "东西南北中发白" 在发牌的时候都扣除掉
     *
     * @return true = 不带风, false = 带风
     */
    public boolean isBuDaiFeng() {
        return _innerMap.getOrDefault(KEY_PLAY_METHOD_BU_DAI_FENG, 0) == 1;
    }

    /**
     * 是否亮杠腚
     *
     * @return true = 亮杠腚, false = 非亮杠腚
     */
    public boolean isLiangGangDing() {
        return _innerMap.getOrDefault(KEY_PLAY_METHOD_LIANG_GANG_DING, 0) == 1;
    }

    /**
     * 是否带飘分
     *
     * @return true = 带飘分, false = 不带飘分
     */
    public boolean isPiaoFen() {
        return _innerMap.getOrDefault(KEY_PLAY_METHOD_PIAO_FEN, 0) == 1;
    }

    /**
     * 是否 64 番封顶
     *
     * @return true = 封顶, false = 不封顶
     */
    public boolean is64FanFengDing() {
        return _innerMap.getOrDefault(KEY_PLAY_METHOD_64_FAN_FENG_DING, 0) == 1;
    }

    /**
     * 验证规则是否有效
     *
     * @return true = 规则有效, false = 规则无效
     */
    public boolean validate() {
        if (isLuanMao() &&
            isLuanMao() == isBuDaiFeng()) {
            // 乱锚和不带风不能同时勾选
            return false;
        }

        if (isJiaWu() &&
            !isJiaDang()) {
            // 夹五必夹档
            return false;
        }

        if (2 == getMaxPlayer()) {
            if (getMaxCircle() > 0) {
                // 只有 2 个玩家, 不能按圈计算牌局
                return false;
            } else {
                return +6 == getMaxRound()
                    || 12 == getMaxRound()
                    || 24 == getMaxRound();
            }
        } else if (3 == getMaxPlayer()) {
            if (getMaxCircle() > 0) {
                // 只有 3 个玩家, 不能按圈计算牌局
                return false;
            } else {
                return +5 == getMaxRound()
                    || 10 == getMaxRound()
                    || 20 == getMaxRound();
            }
        } else if (4 == getMaxPlayer()) {
            if (getMaxRound() > 0) {
                // 如果是 4 个玩家, 只能按照圈结算牌局
                return false;
            } else {
                return 1 == getMaxCircle()
                    || 2 == getMaxCircle()
                    || 4 == getMaxCircle();
            }
        }

        return false;
    }

    /**
     * 创建 JSON 对象
     *
     * @return JSON 对象
     */
    public JSONObject toJSON() {
        JSONObject joRoot = new JSONObject();

        for (Integer key : _innerMap.keySet()) {
            joRoot.put(String.valueOf(key), _innerMap.get(key));
        }

        return joRoot;
    }
}
