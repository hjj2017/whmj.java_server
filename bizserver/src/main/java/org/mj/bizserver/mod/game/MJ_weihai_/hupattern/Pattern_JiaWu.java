package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

/**
 * 夹五
 */
public class Pattern_JiaWu implements IHuPatternTest {
    /**
     * 夹胡
     */
    static private final Pattern_JiaHu PATTERN_JIA_HU = new Pattern_JiaHu();

    @Override
    public boolean test(Round currRound, Player currPlayer) {
        if (null == currRound ||
            null == currPlayer ||
            null == currPlayer.getCurrState().getMahjongHu()) {
            return false;
        }

        if (!currRound.getRuleSetting().isJiaWu()) {
            // 如果规则中没有勾选夹五,
            // 那么不计算夹五...
            return false;
        }

        if (currPlayer.getCurrState().getMahjongHu() != MahjongTileDef._5_WAN &&
            currPlayer.getCurrState().getMahjongHu() != MahjongTileDef._5_TIAO &&
            currPlayer.getCurrState().getMahjongHu() != MahjongTileDef._5_BING) {
            return false;
        }

        return PATTERN_JIA_HU.test(currRound, currPlayer);
    }
}
