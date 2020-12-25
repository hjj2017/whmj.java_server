package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

/**
 * 自摸
 */
public class Pattern_ZiMo implements IHuPatternTest {
    @Override
    public boolean test(Round currRound, Player currPlayer) {
        return null != currRound
            && null != currPlayer
            && null != currPlayer.getCurrState().getMahjongZiMo();
    }
}
