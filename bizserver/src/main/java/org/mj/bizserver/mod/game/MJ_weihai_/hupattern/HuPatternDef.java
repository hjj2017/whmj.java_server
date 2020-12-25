package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

/**
 * 胡牌模式定义
 */
public enum HuPatternDef {
    /**
     * 平胡
     */
    PING_HU(1, "PingHu", new Pattern_PingHu(), 1),

    /**
     * 庄家
     */
    ZHUANG_JIA(1001, "ZhuangJia", new Pattern_ZhuangJia(), 2),

    /**
     * 自摸
     */
    ZI_MO(1002, "ZiMo", new Pattern_ZiMo(), 1),

    /**
     * 七小对
     */
    QI_XIAO_DUI(1007, "QiXiaDui", new Pattern_QiXiaoDui(), 4),

    /**
     * 豪华七小对
     */
    HAO_HUA_QI_XIAO_DUI(1017, "HaoHuaQiXiaoDui", new Pattern_HaoHuaQiXiaoDui(), 8),

    /**
     * 双豪华七小对
     */
    SHUANG_HAO_HUA_QI_XIAO_DUI(1027, "ShuangHaoHuaQiXiaoDui", new Pattern_ShuangHaoHuaQiXiaoDui(), 16),

    /**
     * 超豪华七小对
     */
    CHAO_HAO_HUA_QI_XIAO_DUI(1037, "ChaoHaoHuaQiXiaoDui", new Pattern_ChaoHaoHuaQiXiaoDui(), 16),

    /**
     * 清一色
     */
    QING_YI_SE(1111, "QingYiSe", new Pattern_QingYiSe(), 4),

    /**
     * 混一色
     */
    HUN_YI_SE(1112, "HunYiSe", new Pattern_HunYiSe(), 2),

    /**
     * 碰碰胡
     */
    PENG_PENG_HU(2222, "PengPengHu", new Pattern_PengPengHu(), 2),

    /**
     * 门清
     */
    MEN_QING(4000, "MenQing", new Pattern_MenQing(), 2),

    /**
     * 手把一
     */
    SHOU_BA_YI(4001, "ShouBaYi", new Pattern_ShouBaYi(), 2),

    /**
     * 杠上开花
     */
    GANG_SHANG_KAI_HUA(4003, "GangShangKaiHua", new Pattern_GangShangKaiHua(), 2),

    /**
     * 杠后炮
     */
    GANG_HOU_PAO(4004, "GangHouPao", new Pattern_GangHouPao(), 2),

    /**
     * 夹胡
     */
    JIA_HU(5001, "JiaHu", new Pattern_JiaHu(), 2),

    /**
     * 夹五
     */
    JIA_WU(5005, "JiaWu", new Pattern_JiaWu(), 2),

    /**
     * 天胡
     */
    TIAN_HU(9001, "TianHu", new Pattern_TianHu(), 4),

    /**
     * 地胡
     */
    D_HU(9002, "DiHu", new Pattern_DiHu(), 4),

    /**
     * 海底捞月
     */
    HAI_DI_LAO_YUE(9999, "HaiDiLaoYue", new Pattern_HaiDiLaoYue(), 2),
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
     * 胡牌模式测试
     */
    private final IHuPatternTest _patternTest;

    /**
     * 番数
     */
    private final int _fan;

    /**
     * 枚举参数构造器
     *
     * @param intVal      整数值
     * @param strVal      字符串值
     * @param patternTest 胡牌模式测试
     * @param fan         番数
     */
    HuPatternDef(int intVal, String strVal, IHuPatternTest patternTest, int fan) {
        _intVal = intVal;
        _strVal = strVal;
        _patternTest = patternTest;
        _fan = fan;
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
     * 获取胡牌模式测试
     *
     * @return 胡牌模式测试
     */
    public IHuPatternTest getPatternTest() {
        return _patternTest;
    }

    /**
     * 获取番数
     *
     * @return 番数
     */
    public int getFan() {
        return _fan;
    }
}
