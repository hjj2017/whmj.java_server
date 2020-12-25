package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

import java.util.List;

/**
 * 门清
 */
public class Pattern_MenQing implements IHuPatternTest {
    @Override
    public boolean test(Round currRound, Player currPlayer) {
        if (null == currRound ||
            null == currPlayer ||
            null == currPlayer.getCurrState().getMahjongZiMo()) {
            return false;
        }

        // 获取麻将吃碰杠列表
        List<MahjongChiPengGang> mahjongChiPengGangList = currPlayer.getMahjongChiPengGangListCopy();

        if (null != mahjongChiPengGangList &&
            !mahjongChiPengGangList.isEmpty()) {
            for (MahjongChiPengGang mahjongChiPengGang : mahjongChiPengGangList) {
                if (null == mahjongChiPengGang ||
                    mahjongChiPengGang.getKind() != MahjongChiPengGang.KindDef.AN_GANG) {
                    // 只有暗杠不破门清,
                    // 有吃、碰、明杠、补杠都不算门清
                    return false;
                }
            }
        }

        return true;
    }
}
