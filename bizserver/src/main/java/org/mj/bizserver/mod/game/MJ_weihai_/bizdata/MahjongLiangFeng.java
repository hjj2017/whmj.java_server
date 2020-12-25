package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 麻将亮风,
 * 一个数据结构, 在这里记录了亮风的种类和每张牌的数量...
 * XXX 注意: 亮风是威海麻将特有的一种玩法
 */
public final class MahjongLiangFeng {
    /**
     * 种类
     */
    private KindDef _kind;

    /**
     * 计数器字典
     */
    private Map<MahjongTileDef, Integer> _counterMap = null;

    /**
     * 计数器字典副本
     */
    private Map<MahjongTileDef, Integer> _counterMapCopy = null;

    /**
     * 类默认构造器
     */
    MahjongLiangFeng() {
    }

    /**
     * 获取亮风种类
     *
     * @return 亮风种类
     */
    public KindDef getKind() {
        return _kind;
    }

    /**
     * 获取计数器字典副本
     *
     * @return 计数器字典
     */
    public Map<MahjongTileDef, Integer> getCounterMapCopy() {
        if (null == _counterMapCopy) {
            if (null == _counterMap) {
                _counterMapCopy = Collections.emptyMap();
            } else {
                _counterMapCopy = Collections.unmodifiableMap(Map.copyOf(_counterMap));
            }
        }

        return _counterMapCopy;
    }

    /**
     * 执行亮风
     *
     * @param kind 种类
     * @param t0   第一张牌
     * @param t1   第二张牌
     * @param t2   第三张牌
     */
    public void doLiangFeng(KindDef kind, MahjongTileDef t0, MahjongTileDef t1, MahjongTileDef t2) {
        if (null == kind ||
            null == t0 ||
            null == t1 ||
            null == t2) {
            return;
        }

        _kind = kind;

        final MahjongTileDef[] tArray = { t0, t1, t2 };

        for (MahjongTileDef currT : tArray) {
            if (null == currT) {
                return;
            }

            if (currT.getSuit() != MahjongTileDef.Suit.FENG &&
                currT.getSuit() != MahjongTileDef.Suit.JIAN) {
                return;
            }

            if (null != kind.getMahjongSuit() &&
                currT.getSuit() != kind.getMahjongSuit()) {
                return;
            }
        }

        _counterMap = new HashMap<>();
        _counterMap.put(t0, 1);
        _counterMap.put(t1, 1);
        _counterMap.put(t2, 1);
        _counterMapCopy = null;
    }

    /**
     * 执行补风
     *
     * @param t 麻将牌
     */
    public void doBuFeng(MahjongTileDef t) {
        if (null == t) {
            return;
        }

        if (t.getSuit() != MahjongTileDef.Suit.FENG &&
            t.getSuit() != MahjongTileDef.Suit.JIAN) {
            return;
        }

        if (null != _kind.getMahjongSuit() &&
            t.getSuit() != _kind.getMahjongSuit()) {
            return;
        }

        if (null == _counterMap) {
            return;
        }

        int count = _counterMap.getOrDefault(t, 0);
        _counterMap.put(t, count + 1);
        _counterMapCopy = null;
    }

    /**
     * 种类定义
     */
    public enum KindDef {
        /**
         * 风
         */
        FENG(1, "Feng", MahjongTileDef.Suit.FENG),

        /**
         * 箭
         */
        JIAN(2, "Jian", MahjongTileDef.Suit.JIAN),

        /**
         * 乱锚
         */
        LUAN_MAO(3, "LuanMao", null),
        ;

        /**
         * 整数值
         */
        private final int _intVal;

        /**
         * 字符串值
         */
        private final String _strVal;

        /**
         * 麻将牌花色
         */
        private final MahjongTileDef.Suit _mahjongSuit;

        /**
         * 枚举参数构造器
         *
         * @param intVal      整数值
         * @param strVal      字符串值
         * @param mahjongSuit 麻将牌花色
         */
        KindDef(int intVal, String strVal, MahjongTileDef.Suit mahjongSuit) {
            _intVal = intVal;
            _strVal = strVal;
            _mahjongSuit = mahjongSuit;
        }

        /**
         * 获取整数值
         *
         * @return 整数值
         */
        public int getIntVal() {
            return _intVal;
        }

        /**
         * 获取字符串值
         *
         * @return 字符串值
         */
        public String getStrVal() {
            return _strVal;
        }

        /**
         * 获取麻将牌花色
         *
         * @return 麻将牌花色
         */
        public MahjongTileDef.Suit getMahjongSuit() {
            return _mahjongSuit;
        }
    }

    /**
     * 释放资源
     */
    public void free() {
        if (null != _counterMap) {
            _counterMap.clear();
        }

        _counterMap = null;
        _counterMapCopy = null;
    }
}
