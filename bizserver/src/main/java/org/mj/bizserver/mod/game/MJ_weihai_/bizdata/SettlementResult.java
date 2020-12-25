package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import java.util.Collections;
import java.util.Map;

/**
 * 结算结果, 隶属于玩家, 结构如下:
 * <pre>
 * Player
 *   |
 *   +-- StateTable
 *   |
 *   +-- SettlementResult ( 当前类 )
 * </pre>
 * 一个玩家持有一个状态表还有一个结算结果,
 * 状态表中记录玩家当前是否已经准备好, 是否自摸、胡牌、点炮?
 * 而结算结果只在牌局结束算分的时候会用到...
 *
 * @see StateTable
 */
public class SettlementResult {
    /**
     * 坐庄次数
     */
    private int _zuoZhuangTimez = 0;

    /**
     * 胡牌次数
     */
    private int _huPaiTimez = 0;

    /**
     * 点炮次数
     */
    private int _dianPaoTimez = 0;

    /**
     * 自摸次数
     */
    private int _ziMoTimez = 0;

    /**
     * 杠牌模式字典
     * key = 杠牌模式 ( 明杠、暗杠、补杠 ), val = 番数
     */
    private Map<Integer, Integer> _gangPatternMap;

    /**
     * 胡牌模式字典
     * key = 胡牌模式, val = 番数
     */
    private Map<Integer, Integer> _huPatternMap;

    /**
     * 亮风番数
     */
    private int _liangFengFan = 0;

    /**
     * 类默认构造器
     */
    SettlementResult() {
    }

    /**
     * 获取坐庄次数
     *
     * @return 坐庄次数
     */
    public int getZuoZhuangTimez() {
        return _zuoZhuangTimez;
    }

    /**
     * 获取胡牌次数
     *
     * @return 胡牌次数
     */
    public int getHuPaiTimez() {
        return _huPaiTimez;
    }

    /**
     * 获取点炮次数
     *
     * @return 点炮次数
     */
    public int getDianPaoTimez() {
        return _dianPaoTimez;
    }

    /**
     * 获取自摸次数
     *
     * @return 自摸次数
     */
    public int getZiMoTimez() {
        return _ziMoTimez;
    }

    /**
     * 获取杠牌模式字典副本
     *
     * @return 杠牌模式字典, key = 杠牌模式, val = 番数
     */
    public Map<Integer, Integer> getGangPatternMapCopy() {
        if (null == _gangPatternMap) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(_gangPatternMap);
        }
    }

    /**
     * 获取杠牌番数总和
     *
     * @return 杠牌番数总和
     */
    public int sumOfGangFan() {
        if (null == _gangPatternMap) {
            return 0;
        }

        int sumVal = 0;

        for (Integer fan : _gangPatternMap.values()) {
            if (null != fan) {
                sumVal += fan;
            }
        }

        return sumVal;
    }

    /**
     * 设置杠牌模式字典
     *
     * @param val 杠牌模式字典
     */
    public void setGangPatternMap(Map<Integer, Integer> val) {
        _gangPatternMap = val;
    }

    /**
     * 获取胡牌模式字典副本
     *
     * @return 胡牌模式字典, key = 胡牌模式, val = 番数
     * @see org.mj.bizserver.mod.game.MJ_weihai_.hupattern.HuPatternDef
     * @see org.mj.bizserver.mod.game.MJ_weihai_.hupattern.HuPatternJudge
     */
    public Map<Integer, Integer> getHuPatternMapCopy() {
        if (null == _huPatternMap) {
            return Collections.emptyMap();
        } else {
            return Collections.unmodifiableMap(_huPatternMap);
        }
    }

    /**
     * 计算胡牌番数总和
     *
     * @return 胡牌番数总和
     */
    public int sumOfHuFan() {
        int sumVal = 0;

        for (Integer fan : _huPatternMap.values()) {
            if (null != fan) {
                sumVal += fan;
            }
        }

        return sumVal;
    }

    /**
     * 设置胡牌模式字典
     *
     * @param val 胡牌模式字典
     */
    public void setHuPatternMap(Map<Integer, Integer> val) {
        _huPatternMap = val;
    }

    /**
     * 获取亮风番数
     *
     * @return 亮风番数
     */
    public int getLiangFengFan() {
        return _liangFengFan;
    }

    /**
     * 设置亮风番数
     *
     * @param val 亮风番数
     */
    public void setLiangFengFan(int val) {
        _liangFengFan = val;
    }

    /**
     * 增加次数
     *
     * @param yesZuoZhuang 增加坐庄次数
     * @param yesHuPai     增加胡牌次数
     * @param yesDianPao   增加点炮次数
     * @param yesZiMo      增加自摸次数
     */
    public void doIncreaseTimez(boolean yesZuoZhuang, boolean yesHuPai, boolean yesDianPao, boolean yesZiMo) {
        if (yesZuoZhuang) {
            ++_zuoZhuangTimez;
        }

        if (yesHuPai) {
            ++_huPaiTimez;
        }

        if (yesDianPao) {
            ++_dianPaoTimez;
        }

        if (yesZiMo) {
            ++_ziMoTimez;
        }
    }

    /**
     * 释放资源
     */
    public void free() {
        if (null != _gangPatternMap) {
            _gangPatternMap.clear();
        }

        if (null != _huPatternMap) {
            _huPatternMap.clear();
        }

        _gangPatternMap = null;
        _huPatternMap = null;
    }
}
