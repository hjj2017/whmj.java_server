package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

/**
 * 杠后炮
 */
public class Pattern_GangHouPao implements IHuPatternTest {
    @Override
    public boolean test(Round currRound, Player currPlayer) {
        if (null == currRound ||
            null == currPlayer ||
            null == currPlayer.getCurrState().getMahjongHu()) {
            // 如果当前玩家不是胡牌玩家,
            return false;
        }

        for (Player dianPaoPlayer : currRound.getPlayerListCopy()) {
            if (null != dianPaoPlayer &&
                dianPaoPlayer.getCurrState().isDianPao() &&
                dianPaoPlayer.getCurrState().getJustGangNum() > 0) {
                // 找到这个点炮的玩家,
                // 看看这个玩家刚好有杠的数量是否满足条件?
                return true;
            }
        }

        return false;
    }
}
