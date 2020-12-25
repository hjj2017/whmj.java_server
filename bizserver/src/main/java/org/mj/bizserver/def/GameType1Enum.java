package org.mj.bizserver.def;

import java.util.HashMap;
import java.util.Map;

/**
 * 游戏类型 X 定义
 */
public enum GameType1Enum {
    /**
     * 威海麻将
     */
    MJ_weihai_(GameType0Enum.MAHJONG, 1001, "MJ_weihai_"),
    ;

    /**
     * 整数值字典
     */
    static private final Map<Integer, GameType1Enum> INT_AND_VAL_MAP = new HashMap<>();

    /**
     * 游戏类型 0
     */
    private final GameType0Enum _gameType0;

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
     * @param gameType0 游戏类型 0
     * @param intVal 整数值
     * @param strVal 字符串值
     */
    GameType1Enum(GameType0Enum gameType0, int intVal, String strVal) {
        _gameType0 = gameType0;
        _intVal = intVal;
        _strVal = strVal;
    }

    /**
     * 获取游戏类型 0
     *
     * @return 游戏类型 0
     */
    public GameType0Enum getGameType0() {
        return _gameType0;
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
    static public GameType1Enum valueOf(int intVal) {
        if (INT_AND_VAL_MAP.isEmpty()) {
            for (GameType1Enum $enum : values()) {
                INT_AND_VAL_MAP.putIfAbsent($enum.getIntVal(), $enum);
            }
        }

        return INT_AND_VAL_MAP.getOrDefault(
            intVal,
            null
        );
    }
}
