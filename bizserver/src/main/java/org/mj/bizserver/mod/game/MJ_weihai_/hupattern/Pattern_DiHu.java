package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

import java.util.List;

/**
 * 地胡
 */
public class Pattern_DiHu implements IHuPatternTest {
    @Override
    public boolean test(Round currRound, Player currPlayer) {
        if (null == currRound ||
            null == currPlayer ||
            null == currPlayer.getCurrState().getMahjongZiMo() || // XXX 注意: 地胡必须是自摸
            currPlayer.getCurrState().isZhuangJia()) {
            // 庄家不可能是地胡
            return false;
        }

        if (currRound.getTakeCardNum() == 1) {
            // 如果只发出一张牌,
            // 算地胡
            return true;
        }

        if (currRound.getTakeCardNum() == 2) {
            // 如果已经取出两张麻将牌,
            // 那么就得看看玩家是否有明杠?
            // 也就是说庄家抓到第一张牌之后打出,
            // 闲家杠牌 ( 只可能是明杠 ), 之后抓到的牌胡牌了
            //
            // 获取麻将吃碰杠列表
            final List<MahjongChiPengGang> mahjongChiPengGangList = currPlayer.getMahjongChiPengGangListCopy();

            return mahjongChiPengGangList.size() == 1 &&
                mahjongChiPengGangList.get(0).getKind() == MahjongChiPengGang.KindDef.MING_GANG;
        }

        return false;
    }
}
