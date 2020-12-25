package org.mj.bizserver.def;

/**
 * 服务器工作类型定义
 */
public enum ServerJobTypeEnum {
    /**
     * 账户
     */
    PASSPORT(1, "passport"),

    /**
     * 大厅
     */
    HALL(1 << 1, "hall"),

    /**
     * 游戏
     */
    GAME(1 << 2, "game"),

    /**
     * 亲友圈
     */
    CLUB(1 << 3, "club"),

    /**
     * 聊天
     */
    CHAT(1 << 4, "chat"),

    /**
     * 战绩
     */
    RECORD(1 << 5, "record"),

    /**
     * 排行榜
     */
    RANK(1 << 6, "rank"),
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
    ServerJobTypeEnum(int intVal, String strVal) {
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
