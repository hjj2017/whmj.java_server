package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

/**
 * 天胡
 */
public class Pattern_TianHu implements IHuPatternTest {
    @Override
    public boolean test(Round currRound, Player currPlayer) {
        if (null == currRound ||
            null == currPlayer ||
            null == currPlayer.getCurrState().getMahjongZiMo()) {
            return false;
        }

        // 庄家起手胡牌
        return currRound.getTakeCardNum() == 1
            && currPlayer.getCurrState().isZhuangJia();
    }
}
