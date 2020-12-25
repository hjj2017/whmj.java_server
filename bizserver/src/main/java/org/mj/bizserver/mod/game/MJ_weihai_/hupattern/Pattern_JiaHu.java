package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 夹胡, 例如: 有 3 万、5 万, 胡 4 万
 */
public class Pattern_JiaHu implements IHuPatternTest {
    @Override
    public boolean test(Round currRound, Player currPlayer) {
        if (null == currRound ||
            null == currPlayer ||
            null == currPlayer.getCurrState().getMahjongHu()) {
            return false;
        }

        if (!currRound.getRuleSetting().isJiaDang()) {
            // 如果规则中没有勾选夹档,
            // 那么不计算夹胡...
            return false;
        }

        return test(
            currPlayer.getMahjongChiPengGangListCopy(),
            currPlayer.getMahjongInHandCopy(),
            currPlayer.getCurrState().getMahjongHu()
        );
    }

    @Override
    public boolean test(
        List<MahjongChiPengGang> mahjongChiPengGangList,
        List<MahjongTileDef> mahjongInHand,
        MahjongTileDef mahjongAtLast) {
        if (null == mahjongInHand ||
            mahjongInHand.size() < 4) {
            // 如果手中的牌的数量 < 4,
            // 那不可能有夹胡的情况
            // 因为算上最后一张牌至少应该能凑成 一个顺子 + 加对子的形式
            return false;
        }

        if (null == mahjongAtLast ||
            mahjongAtLast.getSuit() == MahjongTileDef.Suit.FENG ||
            mahjongAtLast.getSuit() == MahjongTileDef.Suit.JIAN) {
            // 东西南北中发白,
            // 没法算作夹胡...
            return false;
        }

        if (mahjongAtLast == MahjongTileDef._1_WAN ||
            mahjongAtLast == MahjongTileDef._9_WAN ||
            mahjongAtLast == MahjongTileDef._1_TIAO ||
            mahjongAtLast == MahjongTileDef._9_TIAO ||
            mahjongAtLast == MahjongTileDef._1_BING ||
            mahjongAtLast == MahjongTileDef._9_BING) {
            // 如果是 1 和 9,
            // 那也不可能是夹胡
            return false;
        }

        // 定义两边的牌
        MahjongTileDef t0 = MahjongTileDef.valueOf(mahjongAtLast.getIntVal() - 1);
        MahjongTileDef t1 = MahjongTileDef.valueOf(mahjongAtLast.getIntVal() + 1);

        if (!mahjongInHand.contains(t0) ||
            !mahjongInHand.contains(t1)) {
            // 如果手牌中没有两边的牌,
            // 则不可能是夹胡
            return false;
        }

        // 接下来模拟吃牌,
        // 如果吃牌之后剩下的牌型还能胡牌,
        // 那么就可能算是夹胡...
        // 吃牌过程就是把 t0 和 t2 从手里拿走,
        // 并且认为手中的最右边那张牌算是要打出的牌,
        // 看看用最右边的那张牌是不是可以胡牌
        List<MahjongTileDef> tempList = new ArrayList<>(mahjongInHand);
        tempList.remove(t0);
        tempList.remove(t1);
        tempList.sort(Comparator.comparingInt(MahjongTileDef::getIntVal));

        if (!HuFormula.test(
            tempList.subList(0, tempList.size() - 1),
            tempList.get(tempList.size() - 1))) {
            // 如果不能胡牌, 则说明不是夹档,
            // 胡的这张牌 mahjongAtLast 是凑对子用的...
            // 例如: AAA B CCC (B)
            // 虽然在上一步可以顺利模拟吃牌 ABC,
            // 但是剩下的牌 AA B C (C) 无法胡牌!
            return false;
        }

        // 执行到这里还是可以胡牌,
        // 则很有可能是夹档,
        // 但是需要排除这样的情况,
        // 例如: A B CC D EE (B) <== 整理可得 ==> A(B)C BCD EE
        // B 可以和第一个 AC 凑出一个顺子,
        // 而且模拟吃牌之后还能胡牌,
        // 但是却有个 BCD,
        // 这可能也不满足夹胡的 "视觉效果".
        // 怎么理解?
        // 因为我们更习惯将上面的牌这样凑: ABC (B)CD EE.
        // 同理, 下面这样的情况可能也不满足夹胡,
        // 例如: A BB C D EE (C) <== 整理可得 ==> ABC B(C)D EE
        // 这样的情况就出现二义性了,
        // 因为我们还可以这样凑: AB(C) BCD EE.
        // 这样我们就无法界定到底算不算是夹胡了

        t0 = MahjongTileDef.valueOf(mahjongAtLast.getIntVal() - 2);
        t1 = MahjongTileDef.valueOf(mahjongAtLast.getIntVal() - 1);

        if (null != t0 &&
            null != t1) {
            if (tempList.contains(t0) &&
                tempList.contains(t1)) {
                return false;
            }
        }

        t0 = MahjongTileDef.valueOf(mahjongAtLast.getIntVal() + 1);
        t1 = MahjongTileDef.valueOf(mahjongAtLast.getIntVal() + 2);

        if (null != t0 &&
            null != t1) {
            if (tempList.contains(t0) &&
                tempList.contains(t1)) {
                return false;
            }
        }

        return true;
    }
}
