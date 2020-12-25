package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.def.WorkModeDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RuleSetting;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongInHandChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 发牌人
 */
final class Dealer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(Dealer.class);

    /**
     * 随机对象
     */
    static private final Random RAND = new Random();

    /**
     * 私有化类默认构造器
     */
    private Dealer() {
    }

    /**
     * 执行发牌逻辑
     *
     * @param currRound 当前牌局
     * @param rptrTeam  记者小队
     */
    static void execDeal(Round currRound, ReporterTeam rptrTeam) {
        if (null == currRound ||
            null == currRound.getRuleSetting() ||
            null == rptrTeam) {
            return;
        }

        // 先洗牌
        List<MahjongTileDef> tList = shuffle(currRound.getRuleSetting());
        currRound.putAllMahjongTile(tList);

        for (int i = 0; i < currRound.getPlayerCount(); i++) {
            // 获取当前玩家
            final Player currPlayer = currRound.getPlayerBySeatIndex(i);

            if (null == currPlayer ||
                currPlayer.getMahjongInHandCopy().size() > 0) {
                continue;
            }

            for (int j = 0; j < 13; j++) {
                // 给每个玩家发 13 张牌
                currPlayer.addAMahjongTileInHand(currRound.takeAMahjongTile());
            }

            LOGGER.info(
                "开局发牌, userId = {}, atRoomId = {}, roundIndex = {}, mahjongInHand = {}",
                currPlayer.getUserId(),
                currRound.getRoomId(),
                currRound.getRoundIndex(),
                currPlayer.getMahjongInHandIntValList()
            );

            rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
                new Wordz_MahjongInHandChanged(
                    currPlayer.getUserId(),
                    currPlayer.getMahjongInHandCopy(),
                    null
                )
            )).createMaskCopy());
        }
    }

    /**
     * 洗牌
     *
     * @return 麻将牌定义列表
     */
    static private List<MahjongTileDef> shuffle(RuleSetting ruleSetting) {
        // 临时的麻将牌列表
        List<MahjongTileDef> t0List = new ArrayList<>();

        for (MahjongTileDef t : MahjongTileDef.values()) {
            if (null != ruleSetting &&
                ruleSetting.isBuDaiFeng()) {
                if (t.getSuit() == MahjongTileDef.Suit.FENG ||
                    t.getSuit() == MahjongTileDef.Suit.JIAN) {
                    // 如果不带风,
                    // 那么遇到东南西北中发白就直接跳过
                    continue;
                }
            }

            for (int i = 0; i < 4; i++) {
                t0List.add(t);
            }
        }

        List<MahjongTileDef> t1List = new ArrayList<>(t0List.size());

        while (t0List.size() > 0) {
            // 随机抽取一张牌
            int rIndex = RAND.nextInt(t0List.size());
            t1List.add(t0List.remove(rIndex));
        }

        if (WorkModeDef.currIsDevMode()) {
            // 只有在开发模式下才有机会修正麻将牌列表,
            // 正式环境是不可以的...
            t1List = lastMend(t1List);
        }

        return t1List;
    }

    /**
     * 修正麻将牌列表, 一般是在开发测试环境下调用
     *
     * @param t1List 麻将牌列表
     * @return 修正后的麻将牌列表
     */
    static private List<MahjongTileDef> lastMend(List<MahjongTileDef> t1List) {
//        t1List = java.util.Arrays.asList(
//            MahjongTileDef._1_TIAO,
//            MahjongTileDef._2_TIAO,
//
//            MahjongTileDef._1_WAN,
//            MahjongTileDef._1_WAN,
//            MahjongTileDef._1_WAN,
//            MahjongTileDef._2_WAN,
//            MahjongTileDef._2_WAN,
//            MahjongTileDef._2_WAN,
//            MahjongTileDef._3_WAN,
//            MahjongTileDef._3_WAN,
//            MahjongTileDef._3_WAN,
//            MahjongTileDef._4_WAN,
//            MahjongTileDef._4_WAN,
//            MahjongTileDef._4_WAN,
//            MahjongTileDef._5_WAN,
//
//            MahjongTileDef._1_BING,
//            MahjongTileDef._1_BING,
//            MahjongTileDef._4_TIAO,
//            MahjongTileDef._4_TIAO,
//            MahjongTileDef._4_TIAO,
//            MahjongTileDef._4_TIAO,
//            MahjongTileDef._1_WAN,
//            MahjongTileDef._2_WAN,
//            MahjongTileDef._7_BING,
//            MahjongTileDef._8_BING,
//            MahjongTileDef.HONG_ZHONG,
//            MahjongTileDef._2_WAN,
//            MahjongTileDef._3_WAN,
//
//            MahjongTileDef._5_BING,
//            MahjongTileDef._5_WAN,
//            MahjongTileDef.BAI_BAN,
//            MahjongTileDef._4_WAN,
//            MahjongTileDef._1_TIAO,
//            MahjongTileDef._2_WAN
//        );

        return t1List;
    }
}
