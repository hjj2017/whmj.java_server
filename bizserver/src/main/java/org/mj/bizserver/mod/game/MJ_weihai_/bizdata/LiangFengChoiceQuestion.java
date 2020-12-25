package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import java.util.Collections;
import java.util.Set;

/**
 * 亮风选择题
 */
public final class LiangFengChoiceQuestion {
    /**
     * 乱锚
     */
    private final boolean _luanMao;

    /**
     * 显示选项 - 东风
     */
    private final boolean _displayOptionDongFeng;

    /**
     * 显示选项 - 南风
     */
    private final boolean _displayOptionNanFeng;

    /**
     * 显示选项 - 西风
     */
    private final boolean _displayOptionXiFeng;

    /**
     * 显示选项 - 北风
     */
    private final boolean _displayOptionBeiFeng;

    /**
     * 显示选项 - 红中
     */
    private final boolean _displayOptionHongZhong;

    /**
     * 显示选项 - 发财
     */
    private final boolean _displayOptionFaCai;

    /**
     * 显示选项 - 白板
     */
    private final boolean _displayOptionBaiBan;

    /**
     * 类参数构造器
     *
     * @param luanMao                乱锚
     * @param displayOptionDongFeng  显示选项 - 东风
     * @param displayOptionNanFeng   显示选项 - 南风
     * @param displayOptionXiFeng    显示选项 - 西风
     * @param displayOptionBeiFeng   显示选项 - 北风
     * @param displayOptionHongZhong 显示选项 - 红中
     * @param displayOptionFaCai     显示选项 - 发财
     * @param displayOptionBaiBan    显示选项 - 白板
     */
    public LiangFengChoiceQuestion(
        boolean luanMao,
        boolean displayOptionDongFeng,
        boolean displayOptionNanFeng,
        boolean displayOptionXiFeng,
        boolean displayOptionBeiFeng,
        boolean displayOptionHongZhong,
        boolean displayOptionFaCai,
        boolean displayOptionBaiBan) {
        _luanMao = luanMao;
        _displayOptionDongFeng = displayOptionDongFeng;
        _displayOptionNanFeng = displayOptionNanFeng;
        _displayOptionXiFeng = displayOptionXiFeng;
        _displayOptionBeiFeng = displayOptionBeiFeng;
        _displayOptionHongZhong = displayOptionHongZhong;
        _displayOptionFaCai = displayOptionFaCai;
        _displayOptionBaiBan = displayOptionBaiBan;
    }

    /**
     * 类参数构造器
     *
     * @param luanMao 乱锚
     * @param tSet    麻将牌集合
     */
    public LiangFengChoiceQuestion(boolean luanMao, Set<MahjongTileDef> tSet) {
        _luanMao = luanMao;

        if (null == tSet) {
            tSet = Collections.emptySet();
        }

        _displayOptionDongFeng = tSet.contains(MahjongTileDef.DONG_FENG);
        _displayOptionNanFeng = tSet.contains(MahjongTileDef.NAN_FENG);
        _displayOptionXiFeng = tSet.contains(MahjongTileDef.XI_FENG);
        _displayOptionBeiFeng = tSet.contains(MahjongTileDef.BEI_FENG);
        _displayOptionHongZhong = tSet.contains(MahjongTileDef.HONG_ZHONG);
        _displayOptionFaCai = tSet.contains(MahjongTileDef.FA_CAI);
        _displayOptionBaiBan = tSet.contains(MahjongTileDef.BAI_BAN);
    }

    /**
     * 是否乱锚
     *
     * @return true = 乱锚, false = 不是
     */
    public boolean isLuanMao() {
        return _luanMao;
    }

    /**
     * 是否显示选项 - 东风
     *
     * @return true = 显示, false = 不显示
     */
    public boolean isDisplayOptionDongFeng() {
        return _displayOptionDongFeng;
    }

    /**
     * 是否显示选项 - 南风
     *
     * @return true = 显示, false = 不显示
     */
    public boolean isDisplayOptionNanFeng() {
        return _displayOptionNanFeng;
    }

    /**
     * 是否显示选项 - 西风
     *
     * @return true = 显示, false = 不显示
     */
    public boolean isDisplayOptionXiFeng() {
        return _displayOptionXiFeng;
    }

    /**
     * 是否显示选项 - 北风
     *
     * @return true = 显示, false = 不显示
     */
    public boolean isDisplayOptionBeiFeng() {
        return _displayOptionBeiFeng;
    }

    /**
     * 是否显示选项 - 红中
     *
     * @return true = 显示, false = 不显示
     */
    public boolean isDisplayOptionHongZhong() {
        return _displayOptionHongZhong;
    }

    /**
     * 是否显示选项 - 发财
     *
     * @return true = 显示, false = 不显示
     */
    public boolean isDisplayOptionFaCai() {
        return _displayOptionFaCai;
    }

    /**
     * 是否显示选项 - 白板
     *
     * @return true = 显示, false = 不显示
     */
    public boolean isDisplayOptionBaiBan() {
        return _displayOptionBaiBan;
    }
}
