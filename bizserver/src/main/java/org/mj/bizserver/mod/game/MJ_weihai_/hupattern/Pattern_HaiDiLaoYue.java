package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

/**
 * 海底捞月
 */
public class Pattern_HaiDiLaoYue implements IHuPatternTest {
    @Override
    public boolean test(Round currRound, Player currPlayer) {
        if (null == currRound ||
            null == currPlayer) {
            return false;
        }

        return null != currPlayer.getCurrState().getMahjongZiMo()
            && currRound.getRemainCardNum() <= 0;
    }
}
