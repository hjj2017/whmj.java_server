package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 碰碰胡
 */
public class Pattern_PengPengHu implements IHuPatternTest {
    @Override
    public boolean test(
        List<MahjongChiPengGang> mahjongChiPengGangList, List<MahjongTileDef> mahjongInHand, MahjongTileDef mahjongAtLast) {

        if (null == mahjongInHand ||
            mahjongInHand.isEmpty() ||
            null == mahjongAtLast) {
            return false;
        }

        int counter = 0;

        for (MahjongChiPengGang mahjongChiPengGang : mahjongChiPengGangList) {
            if (null == mahjongChiPengGang ||
                mahjongChiPengGang.getKind() == MahjongChiPengGang.KindDef.CHI) {
                // 如果有吃牌那不可能是碰碰胡,
                // 因为碰碰胡要求有 4 副刻子...
                return false;
            }

            ++counter;
        }

        Map<MahjongTileDef, Integer> tempMap = new HashMap<>();

        for (MahjongTileDef tCurr : mahjongInHand) {
            if (null == tCurr) {
                continue;
            }

            int num = tempMap.getOrDefault(tCurr, 0);
            tempMap.put(tCurr, ++num);
        }

        for (Integer num : tempMap.values()) {
            if (3 == num) {
                ++counter;
            }
        }

        return 4 == counter;
    }
}
