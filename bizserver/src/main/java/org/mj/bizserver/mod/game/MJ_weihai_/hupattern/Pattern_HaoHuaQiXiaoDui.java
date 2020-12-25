package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.List;

/**
 * 豪华七小对
 */
public class Pattern_HaoHuaQiXiaoDui implements IHuPatternTest {
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

        for (int i = 0; i < mahjongInHand.size() - 4; i += 2) {
            // 获取前三张麻将牌
            final MahjongTileDef t0 = mahjongInHand.get(i);
            final MahjongTileDef t1 = mahjongInHand.get(i + 1);
            final MahjongTileDef t2 = mahjongInHand.get(i + 2);

            if (null != t0 &&
                t0 == t1 &&
                t0 == t2) {
                // 如果前三张牌都一样
                if (t0 == mahjongAtLast) {
                    // 如果最后一张牌也一样,
                    // 这说明能凑成 4 张相同的牌
                    return true;
                }

                // 获取第四张牌
                final MahjongTileDef t3 = mahjongInHand.get(i + 3);

                if (t0 == t3) {
                    // 如果第四张牌也一样
                    return true;
                }
            }
        }

        return false;
    }
}
