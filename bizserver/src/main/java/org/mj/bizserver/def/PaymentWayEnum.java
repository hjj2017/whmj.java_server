package org.mj.bizserver.def;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付方式定义
 */
public enum PaymentWayEnum {
    /**
     * 房主支付
     */
    ROOM_OWNER(1, "roomOwner"),

    /**
     * AA 支付
     */
    AA(2, "AA"),

    /**
     * 亲友圈支付
     */
    CLUB(0, "club"),
    ;

    /**
     * 整数值字典
     */
    static private final Map<Integer, PaymentWayEnum> INT_AND_VAL_MAP = new HashMap<>();

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
    PaymentWayEnum(int intVal, String strVal) {
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
     * 根据整数值获取枚举值
     *
     * @param intVal 整数值
     * @return 枚举值
     */
    static public PaymentWayEnum valueOf(int intVal) {
        if (INT_AND_VAL_MAP.isEmpty()) {
            for (PaymentWayEnum $enum : values()) {
                INT_AND_VAL_MAP.putIfAbsent($enum.getIntVal(), $enum);
            }
        }

        return INT_AND_VAL_MAP.getOrDefault(
            intVal,
            null
        );
    }
}
