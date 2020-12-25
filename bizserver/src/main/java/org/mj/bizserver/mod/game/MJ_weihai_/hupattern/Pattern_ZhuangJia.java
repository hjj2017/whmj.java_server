package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

/**
 * 庄家
 */
public class Pattern_ZhuangJia implements IHuPatternTest {
    @Override
    public boolean test(Round currRound, Player currPlayer) {
        return null != currPlayer
            && null != currPlayer.getCurrState().getMahjongHu()
            && currPlayer.getCurrState().isZhuangJia();
    }
}
