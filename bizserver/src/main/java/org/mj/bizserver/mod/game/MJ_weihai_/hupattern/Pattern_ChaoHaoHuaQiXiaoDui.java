package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.List;

/**
 * 超豪华七小对
 */
public class Pattern_ChaoHaoHuaQiXiaoDui implements IHuPatternTest {
    /**
     * 七小对牌型
     */
    static private final Pattern_QiXiaoDui PATTERN_QI_XIAO_DUI = new Pattern_QiXiaoDui();

    @Override
    public boolean test(
        List<MahjongChiPengGang> mahjongChiPengGangList, List<MahjongTileDef> mahjongInHand, MahjongTileDef mahjongAtLast) {

        if (!PATTERN_QI_XIAO_DUI.test(mahjongChiPengGangList, mahjongInHand, mahjongAtLast)) {
            // 如果不是七小对牌型,
            return false;
        }

        // 4 个同样的牌有几组
        int _4TheSameCounter = 0;
        // 获取最后的麻将牌所在位置
        final int specialIndex0 = mahjongInHand.indexOf(mahjongAtLast);

        for (int i = 0; i < specialIndex0 - 4; i += 2) {
            final MahjongTileDef t0 = mahjongInHand.get(i);
            final MahjongTileDef t1 = mahjongInHand.get(i + 1);
            final MahjongTileDef t2 = mahjongInHand.get(i + 2);
            final MahjongTileDef t3 = mahjongInHand.get(i + 3);

            if (null != t0 &&
                t0 == t1 &&
                t1 == t2 &&
                t2 == t3) {
                ++_4TheSameCounter;
            }

            if (_4TheSameCounter >= 3) {
                return true;
            }
        }

        // 最后一张牌最后出现的位置
        final int specialIndex1 = mahjongInHand.lastIndexOf(mahjongAtLast);

        if (2 == (specialIndex1 - specialIndex0)) {
            ++_4TheSameCounter;

            if (_4TheSameCounter >= 3) {
                return true;
            }
        }

        for (int i = specialIndex1 + 1; i < mahjongInHand.size() - 4; i += 2) {
            final MahjongTileDef t0 = mahjongInHand.get(i);
            final MahjongTileDef t1 = mahjongInHand.get(i + 1);
            final MahjongTileDef t2 = mahjongInHand.get(i + 2);
            final MahjongTileDef t3 = mahjongInHand.get(i + 3);

            if (null != t0 &&
                t0 == t1 &&
                t1 == t2 &&
                t2 == t3) {
                ++_4TheSameCounter;
            }

            if (_4TheSameCounter >= 3) {
                return true;
            }
        }

        return false;
    }
}
