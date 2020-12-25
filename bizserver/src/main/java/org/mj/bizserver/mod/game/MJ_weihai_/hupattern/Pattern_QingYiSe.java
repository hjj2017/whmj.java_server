package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.List;

/**
 * 清一色
 */
public class Pattern_QingYiSe implements IHuPatternTest {
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

        if (null != mahjongChiPengGangList &&
            mahjongChiPengGangList.size() > 0) {
            // 所有的吃、碰、明杠、暗杠都得是同一花色
            for (MahjongChiPengGang mahjongChiPengGang : mahjongChiPengGangList) {
                if (null == mahjongChiPengGang) {
                    continue;
                }

                // 获取麻将牌
                MahjongTileDef t = mahjongChiPengGang.getTX();

                if (null != t &&
                    t.getSuit() != suit) {
                    return false;
                }
            }
        }

        return true;
    }
}
