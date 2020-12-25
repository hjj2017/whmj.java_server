package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.ArrayList;
import java.util.List;

/**
 * 混一色
 */
public class Pattern_HunYiSe implements IHuPatternTest {
    @Override
    public boolean test(
        List<MahjongChiPengGang> mahjongChiPengGangList, List<MahjongTileDef> mahjongInHand, MahjongTileDef mahjongAtLast) {

        if (null == mahjongInHand ||
            mahjongInHand.size() <= 0 ||
            null == mahjongAtLast) {
            return false;
        }

        // 获取花色
        final MahjongTileDef.Suit suit = mahjongAtLast.getSuit();

        for (MahjongTileDef tCurr : mahjongInHand) {
            if (null == tCurr ||
                tCurr.getSuit() != suit) {
                return false;
            }
        }

        // 是否有万、条、饼
        boolean hasW = false;
        boolean hasT = false;
        boolean hasB = false;
        // 是否有风 ( 东南西北 )、箭 ( 中发白 )
        boolean hasF = false;
        boolean hasJ = false;

        final List<MahjongTileDef> tList = new ArrayList<>(mahjongInHand);
        tList.add(mahjongAtLast);
        fillChiPengGang(tList, mahjongChiPengGangList);

        for (MahjongTileDef tCurr : tList) {
            if (null == tCurr) {
                return false;
            }

            // 是否有万条饼
            hasW = hasW || (tCurr.getSuit() == MahjongTileDef.Suit.WAN);
            hasT = hasT || (tCurr.getSuit() == MahjongTileDef.Suit.TIAO);
            hasB = hasB || (tCurr.getSuit() == MahjongTileDef.Suit.BING);
            hasF = hasF || (tCurr.getSuit() == MahjongTileDef.Suit.FENG);
            hasJ = hasJ || (tCurr.getSuit() == MahjongTileDef.Suit.JIAN);
        }

        int suiteCounter_WTB = 0;
        suiteCounter_WTB = hasW ? ++suiteCounter_WTB : suiteCounter_WTB;
        suiteCounter_WTB = hasT ? ++suiteCounter_WTB : suiteCounter_WTB;
        suiteCounter_WTB = hasB ? ++suiteCounter_WTB : suiteCounter_WTB;

        if (1 != suiteCounter_WTB) {
            return false;
        }

        if (!hasF &&
            !hasJ) {
            // 如果没有风、箭
            return false;
        }

        return true;
    }

    /**
     * 填充吃、碰、杠到目标麻将牌列表
     *
     * @param tList                  麻将牌列表
     * @param mahjongChiPengGangList 麻将吃碰杠列表
     */
    private void fillChiPengGang(
        final List<MahjongTileDef> tList, List<MahjongChiPengGang> mahjongChiPengGangList) {

        if (null == tList ||
            null == mahjongChiPengGangList ||
            mahjongChiPengGangList.isEmpty()) {
            return;
        }

        for (MahjongChiPengGang mahjongChiPengGang : mahjongChiPengGangList) {
            if (null == mahjongChiPengGang) {
                continue;
            }

            // 获取麻将牌
            MahjongTileDef t = mahjongChiPengGang.getTX();

            if (null != t) {
                tList.add(t);
            }
        }
    }
}
