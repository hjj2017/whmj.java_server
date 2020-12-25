package org.mj.bizserver.mod.club.membercenter.bizdata;

import java.util.HashMap;
import java.util.Map;

/**
 * 亲友圈状态枚举
 */
public enum ClubStateEnum {
    /**
     * 正常状态
     */
    NORMAL(1, "normal"),

    /**
     * 已经解散
     */
    DISSOLVED(-4, "dissolved"),
    ;

    /**
     * 整数值字典
     */
    static private final Map<Integer, ClubStateEnum> INT_VAL_MAP = new HashMap<>();

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
    ClubStateEnum(final int intVal, final String strVal) {
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
    static public ClubStateEnum valueOf(int intVal) {
        if (INT_VAL_MAP.isEmpty()) {
            for (ClubStateEnum $enum : values()) {
                INT_VAL_MAP.putIfAbsent($enum.getIntVal(), $enum);
            }
        }

        return INT_VAL_MAP.getOrDefault(
            intVal,
            null
        );
    }
}
