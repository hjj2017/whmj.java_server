package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.List;

/**
 * 手把一
 */
public class Pattern_ShouBaYi implements IHuPatternTest {
    @Override
    public boolean test(
        List<MahjongChiPengGang> mahjongChiPengGangList, List<MahjongTileDef> mahjongInHand, MahjongTileDef mahjongAtLast) {
        if (null == mahjongChiPengGangList ||
            mahjongChiPengGangList.size() < 3) {
            // 因为威海有亮风玩法,
            // 亮风之后吃碰杠的数量最大就只能是 3 了,
            // 那么手把一的条件也就变成了:
            // 1、至少吃碰杠数量 >= 3 ( 注意不是 4 );
            // 2、手里只能剩下一张麻将牌;
            return false;
        }

        return null != mahjongInHand
            && mahjongInHand.size() == 1;
    }
}
