package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.List;

/**
 * 七小对
 */
public class Pattern_QiXiaoDui implements IHuPatternTest {
    @Override
    public boolean test(
        List<MahjongChiPengGang> mahjongChiPengGangList, List<MahjongTileDef> mahjongInHand, MahjongTileDef mahjongAtLast) {

        if (null != mahjongChiPengGangList &&
            !mahjongChiPengGangList.isEmpty()) {
            // 不能有吃、碰、杠
            return false;
        }

        if (13 != mahjongInHand.size() ||
            null == mahjongAtLast) {
            // 必须是 14 张牌
            return false;
        }

        // 特殊索引
        int specialIndex = mahjongInHand.indexOf(mahjongAtLast);

        if (specialIndex < 0) {
            // 手牌里找不到最后的这张麻将牌,
            // 凑不成一对
            return false;
        }

        for (int i = 0; i < mahjongInHand.size(); i += 2) {
            if (i == specialIndex) {
                // XXX 注意: 如果是特殊位置则跳过,
                // 例如这样的牌型 AA B CC DD EE FF GG [B],
                // 最后一张牌是 B,
                // 那么第一个 B 出现的位置 ( specialIndex = 2 ) 会被跳过!
                ++i;
            }

            if ((i + 1) >= mahjongInHand.size()) {
                // 事先判断一下是否会越界
                return false;
            }

            final MahjongTileDef t0 = mahjongInHand.get(i);
            final MahjongTileDef t1 = mahjongInHand.get(i + 1);

            if (null == t0 ||
                t0 != t1) {
                return false;
            }
        }

        return true;
    }
}
