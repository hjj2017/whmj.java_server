package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

/**
 * 吃牌选择题
 */
public final class ChiChoiceQuestion {
    /**
     * 要吃的是哪一张牌
     */
    private final MahjongTileDef _chiT;

    /**
     * 显示选项 A
     */
    private final boolean _displayOptionA;

    /**
     * 显示选项 B
     */
    private final boolean _displayOptionB;

    /**
     * 显示选项 C
     */
    private final boolean _displayOptionC;

    /**
     * 类参数构造器,
     * XXX 注意: 参数中有三个显示选项参数, 我们以吃 "三万" 这张麻将牌为例,
     * displayOptionA --> 意味着 --> 1 2 [3] 这样吃, 也就是从自己的手牌列表中拿出一万和二万, 吃掉三万;
     * displayOptionB --> 意味着 --> 2 [3] 4 这样吃, 也就是从自己的手牌列表中拿出二万和四万, 吃掉三万;
     * displayOptionC --> 意味着 --> [3] 4 5 这样吃, 也就是从自己的手牌列表中拿出四万和五万, 吃掉三万;
     *
     * @param chiT           要吃的是哪一张麻将牌
     * @param displayOptionA 显示选项 A
     * @param displayOptionB 显示选项 B
     * @param displayOptionC 显示选项 C
     */
    public ChiChoiceQuestion(
        MahjongTileDef chiT, boolean displayOptionA, boolean displayOptionB, boolean displayOptionC) {
        _chiT = chiT;
        _displayOptionA = displayOptionA;
        _displayOptionB = displayOptionB;
        _displayOptionC = displayOptionC;
    }

    /**
     * 获取要吃的那一张麻将牌
     *
     * @return 麻将牌
     */
    public MahjongTileDef getChiT() {
        return _chiT;
    }

    /**
     * 获取要吃的那一张麻将牌的整数值
     *
     * @return 整数值
     */
    public int getChiTIntVal() {
        return (null == _chiT) ? -1 : _chiT.getIntVal();
    }

    /**
     * 是否显示选项 A
     *
     * @return true = 显示, false = 不显示
     */
    public boolean isDisplayOptionA() {
        return _displayOptionA;
    }

    /**
     * 是否显示选项 B
     *
     * @return true = 显示, false = 不显示
     */
    public boolean isDisplayOptionB() {
        return _displayOptionB;
    }

    /**
     * 是否显示选项 C
     *
     * @return true = 显示, false = 不显示
     */
    public boolean isDisplayOptionC() {
        return _displayOptionC;
    }
}
