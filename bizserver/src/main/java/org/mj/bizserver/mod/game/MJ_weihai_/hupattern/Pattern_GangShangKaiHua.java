package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

/**
 * 杠上开花
 */
public class Pattern_GangShangKaiHua implements IHuPatternTest {
    @Override
    public boolean test(Round currRound, Player currPlayer) {
        return null != currPlayer
            && null != currPlayer.getCurrState().getMahjongZiMo()
            && currPlayer.getCurrState().getJustGangNum() > 0;
    }
}
