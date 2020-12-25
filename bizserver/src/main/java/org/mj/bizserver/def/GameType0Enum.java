package org.mj.bizserver.def;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏类型 0
 */
public enum GameType0Enum {
    /**
     * 麻将
     */
    MAHJONG(1, "Mahjong"),

    /**
     * 扑克
     */
    POKER(2, "Poker"),
    ;

    /**
     * 整数值字典
     */
    static private final Map<Integer, GameType0Enum> INT_AND_VAL_MAP = new HashMap<>();

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
    GameType0Enum(int intVal, String strVal) {
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

    /**
     * 将整数值转换成枚举值
     *
     * @param intVal 整数值
     * @return 枚举值
     */
    static public GameType0Enum valueOf(int intVal) {
        if (INT_AND_VAL_MAP.isEmpty()) {
            for (GameType0Enum $enum : values()) {
                INT_AND_VAL_MAP.putIfAbsent($enum.getIntVal(), $enum);
            }
        }

        return INT_AND_VAL_MAP.getOrDefault(
            intVal,
            null
        );
    }
}
