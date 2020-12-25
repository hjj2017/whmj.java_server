package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

import java.util.List;

/**
 * 胡牌模式测试
 */
public interface IHuPatternTest {
    /**
     * 测试胡牌牌型,
     * 胡牌牌型和胡牌公式不一样,
     * 胡牌公式是计算当前麻将牌的排列组合方式是否可以胡牌?
     * 可以根据固定算法计算得到...
     * <p>
     * 而胡牌牌型是不固定的, 会根据地方玩法不同而变化.
     * 另外, 在测试胡牌牌型的时候会检查吃、碰、杠分组, 甚至是胡的哪一张牌?
     * <p>
     * XXX 注意: 要进行胡牌模式测试,
     * 必须满足 null != currPlayer.getCurrState().getHuPai() 这个条件!
     * 也就是说必须得记录胡牌...
     * 而这个胡牌是在 MJ_weihai_BizLogic#hu 函数中设置的.
     *
     * @param currRound  当前牌局
     * @param currPlayer 当前玩家
     * @return true = 模式成立, false = 不是该模式
     */
    default boolean test(Round currRound, Player currPlayer) {
        if (null == currPlayer) {
            return false;
        }

        if (null == currPlayer.getCurrState().getMahjongZiMo() &&
            null == currPlayer.getCurrState().getMahjongHu()) {
            return false;
        }

        // 获取自摸麻将牌
        MahjongTileDef mahjongAtLast = currPlayer.getCurrState().getMahjongZiMo();

        if (null == mahjongAtLast) {
            // 获取胡牌麻将牌
            mahjongAtLast = currPlayer.getCurrState().getMahjongHu();
        }

        return test(
            currPlayer.getMahjongChiPengGangListCopy(),
            currPlayer.getMahjongInHandCopy(),
            mahjongAtLast
        );
    }

    /**
     * 测试胡牌牌型
     *
     * @param mahjongChiPengGangList 麻将吃碰杠胡列表
     * @param mahjongInHand          麻将手牌列表
     * @param mahjongAtLast          最后一张麻将牌
     * @return true = 模式成立, false = 不是该模式
     */
    default boolean test(
        List<MahjongChiPengGang> mahjongChiPengGangList, List<MahjongTileDef> mahjongInHand, MahjongTileDef mahjongAtLast) {
        return false;
    }
}
