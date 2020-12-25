package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import java.util.HashMap;
import java.util.Map;

/**
 * 麻将牌定义
 */
public enum MahjongTileDef {
    // 万
    _1_WAN(21, "_1_Wan", Suit.WAN),
    _2_WAN(22, "_2_Wan", Suit.WAN),
    _3_WAN(23, "_3_Wan", Suit.WAN),
    _4_WAN(24, "_4_Wan", Suit.WAN),
    _5_WAN(25, "_5_Wan", Suit.WAN),
    _6_WAN(26, "_6_Wan", Suit.WAN),
    _7_WAN(27, "_7_Wan", Suit.WAN),
    _8_WAN(28, "_8_Wan", Suit.WAN),
    _9_WAN(29, "_9_Wan", Suit.WAN),

    // 条
    _1_TIAO(41, "_1_Tiao", Suit.TIAO),
    _2_TIAO(42, "_2_Tiao", Suit.TIAO),
    _3_TIAO(43, "_3_Tiao", Suit.TIAO),
    _4_TIAO(44, "_4_Tiao", Suit.TIAO),
    _5_TIAO(45, "_5_Tiao", Suit.TIAO),
    _6_TIAO(46, "_6_Tiao", Suit.TIAO),
    _7_TIAO(47, "_7_Tiao", Suit.TIAO),
    _8_TIAO(48, "_8_Tiao", Suit.TIAO),
    _9_TIAO(49, "_9_Tiao", Suit.TIAO),

    // 饼
    _1_BING(81, "_1_Bing", Suit.BING),
    _2_BING(82, "_2_Bing", Suit.BING),
    _3_BING(83, "_3_Bing", Suit.BING),
    _4_BING(84, "_4_Bing", Suit.BING),
    _5_BING(85, "_5_Bing", Suit.BING),
    _6_BING(86, "_6_Bing", Suit.BING),
    _7_BING(87, "_7_Bing", Suit.BING),
    _8_BING(88, "_8_Bing", Suit.BING),
    _9_BING(89, "_9_Bing", Suit.BING),

    // 东南西北
    DONG_FENG(101, "DongFeng", Suit.FENG),
    NAN_FENG(103, "NanFeng", Suit.FENG),
    XI_FENG(105, "XiFeng", Suit.FENG),
    BEI_FENG(107, "BeiFeng", Suit.FENG),

    // 中发白
    HONG_ZHONG(126, "HongZhong", Suit.JIAN),
    FA_CAI(188, "FaCai", Suit.JIAN),
    BAI_BAN(255, "BaiBan", Suit.JIAN),
    ;

    /**
     * 遮罩值
     */
    static public final int MASK_VAL = 7;

    /**
     * 整数值字典
     */
    static private final Map<Integer, MahjongTileDef> INT_AND_VAL_MAP = new HashMap<>();

    /**
     * 整数值
     */
    private final int _intVal;

    /**
     * 字符串值
     */
    private final String _strVal;

    /**
     * 花色
     */
    private final Suit _suit;

    /**
     * 麻将牌
     *
     * @param intVal 整数值
     * @param strVal 字符串值
     * @param suit   花色
     */
    MahjongTileDef(int intVal, String strVal, Suit suit) {
        _intVal = intVal;
        _strVal = strVal;
        _suit = suit;
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
     * 获取花色
     *
     * @return 花色
     */
    public Suit getSuit() {
        return _suit;
    }

    /**
     * 将整数值转换成枚举值
     *
     * @param intVal 整数值
     * @return 枚举值
     */
    static public MahjongTileDef valueOf(int intVal) {
        if (INT_AND_VAL_MAP.isEmpty()) {
            for (MahjongTileDef $enum : values()) {
                INT_AND_VAL_MAP.putIfAbsent($enum.getIntVal(), $enum);
            }
        }

        return INT_AND_VAL_MAP.getOrDefault(
            intVal,
            null
        );
    }

    /**
     * 是否是万、条、饼
     *
     * @param val 麻将牌
     * @return true = 是万、条、饼, false = 不是
     */
    static public boolean isWanTiaoBing(MahjongTileDef val) {
        if (null == val) {
            return false;
        } else {
            return val.getSuit() == Suit.WAN
                || val.getSuit() == Suit.TIAO
                || val.getSuit() == Suit.BING;
        }
    }

    /**
     * 牌型花色
     */
    public enum Suit {
        /**
         * 万
         */
        WAN(20, "Wan"),

        /**
         * 条
         */
        TIAO(40, "Tiao"),

        /**
         * 饼
         */
        BING(80, "Bing"),

        /**
         * 风
         */
        FENG(100, "Feng"),

        /**
         * 箭 ( 中发白 )
         */
        JIAN(200, "Jian"),
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
         * 枚举参数构造器
         *
         * @param intVal 整数值
         * @param strVal 字符串值
         */
        Suit(int intVal, String strVal) {
            _intVal = intVal;
            _strVal = strVal;
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
    }
}
