package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

/**
 * 平胡
 */
public class Pattern_PingHu implements IHuPatternTest {
    @Override
    public boolean test(Round currRound, Player currPlayer) {
        return null != currPlayer && (
            currPlayer.getCurrState().isZiMo() || currPlayer.getCurrState().isHu()
        );
    }
}
