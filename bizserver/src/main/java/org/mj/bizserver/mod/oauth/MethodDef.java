package org.mj.bizserver.mod.oauth;

import java.util.HashMap;
import java.util.Map;

/**
 * ( 登录 ) 方式定义
 */
enum MethodDef {
    /**
     * 测试员登录
     */
    TESTER_LOGIN(0, "testerLogin"),

    /**
     * 游客登录
     */
    GUEST_LOGIN(1, "guestLogin"),

    /**
     * Ukey 登录
     */
    UKEY_LOGIN(2, "ukeyLogin"),

    /**
     * 手机号+验证码登录
     */
    PHONE_NUMBER_LOGIN(1000, "phoneNumberLogin"),

    /**
     * 微信登录
     */
    WEI_XIN_LOGIN(2000, "weiXinLogin"),

    /**
     * 微信公众号登录
     */
    WEI_XIN_GONG_ZHONG_HAO_LOGIN(2010, "weiXinGongZhongHaoLogin"),
    ;

    /**
     * 整数值字典
     */
    static private final Map<Integer, MethodDef> INT_VAL_MAP = new HashMap<>();

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
    MethodDef(int intVal, String strVal) {
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
     * @return 字符值
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
    static public MethodDef valueOf(int intVal) {
        if (INT_VAL_MAP.isEmpty()) {
            for (MethodDef $enum : values()) {
                INT_VAL_MAP.put($enum.getIntVal(), $enum);
            }
        }

        return INT_VAL_MAP.getOrDefault(
            intVal,
            null
        );
    }
}
