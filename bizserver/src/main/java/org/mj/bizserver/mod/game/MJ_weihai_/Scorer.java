package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.SettlementResult;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.StateTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记分员
 */
final class Scorer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(Scorer.class);

    /**
     * 私有化类默认构造器
     */
    private Scorer() {
    }

    /**
     * 统计总分和次数,
     * 也就是将当前牌局中的玩家数据累加到当前房间中的玩家
     *
     * @param currRoom  当前房间
     * @param currRound 当前牌局
     */
    static void countTotalScoreAndTimez(Room currRoom, Round currRound) {
        // 获取玩家列表
        List<Player> playerList = currRound.getPlayerListCopy();

        // 自摸玩家
        Player ziMoPlayer = null;
        // 胡牌玩家列表和点炮玩家
        List<Player> huPlayerList = null;
        Player dianPaoPlayer = null;

//
// 第一步:
// 先确定自摸玩家或者胡牌、点炮玩家
///////////////////////////////////////////////////////////////////////

        for (Player currPlayer : playerList) {
            if (null == currPlayer) {
                continue;
            }

            // 统计亮风番数
            countLiangFengFan(currPlayer);
            // 统计杠牌番数
            countGangFan(currPlayer);

            // 获取状态表
            final StateTable statTab1 = currPlayer.getCurrState();

            if (statTab1.isZiMo()) {
                // 是自摸玩家
                ziMoPlayer = currPlayer;
            } else if (statTab1.isHu()) {
                if (null == huPlayerList) {
                    huPlayerList = new ArrayList<>();
                }

                huPlayerList.add(currPlayer);
            } else if (statTab1.isDianPao()) {
                // 是点炮玩家
                dianPaoPlayer = currPlayer;
            }
        }

//
// 第二步:
// 自摸算自摸的分数, 点炮算点炮的分数
///////////////////////////////////////////////////////////////////////

        if (null != ziMoPlayer) {
            // 执行自摸算分
            doZiMoScore(currRound, ziMoPlayer);
        } else if (null != huPlayerList &&
            !huPlayerList.isEmpty() &&
            null != dianPaoPlayer) {
            // 执行点炮算分
            doDianPaoScore(currRound, huPlayerList, dianPaoPlayer);
        }

//
// 第三步:
// 将成绩累加到牌局玩家和房间玩家
///////////////////////////////////////////////////////////////////////

        // 最大总分
        int maxTotalScore = -1;

        for (Player currPlayer : playerList) {
            if (null == currPlayer) {
                continue;
            }

            // 计算总分
            final int totalScore = currPlayer.getTotalScore() + currPlayer.getCurrScore();

            if (totalScore > 0 &&
                totalScore > maxTotalScore) {
                maxTotalScore = totalScore;
            }

            // 设置总分
            currPlayer.setTotalScore(totalScore);

            // 获取房间中的玩家
            Player roomPlayer = currRoom.getPlayerByUserId(currPlayer.getUserId());

            if (null == roomPlayer) {
                continue;
            }

            roomPlayer.setTotalScore(totalScore);

            // 获取状态表
            final StateTable statTab1 = currPlayer.getCurrState();

            roomPlayer.getSettlementResult().doIncreaseTimez(
                statTab1.isZhuangJia(),
                statTab1.isHu(),
                statTab1.isDianPao(),
                statTab1.isZiMo()
            );
        }

//
// 第四步:
// 更新大赢家标志
///////////////////////////////////////////////////////////////////////

        for (Player currPlayer : currRoom.getPlayerListCopy()) {
            if (null == currPlayer) {
                continue;
            }

            currPlayer.setBigWinner(
                maxTotalScore > 0 &&
                currPlayer.getTotalScore() >= maxTotalScore
            );
        }
    }

    /**
     * 执行摸牌算分
     *
     * @param currRound  当前牌局
     * @param ziMoPlayer 自摸玩家
     */
    static private void doZiMoScore(Round currRound, Player ziMoPlayer) {
        if (null == currRound ||
            null == ziMoPlayer) {
            return;
        }

        int winFan;
        int totalWinScore = 0;

        // 先计算自摸玩家 ( 赢家 ) 的总番数,
        // 总番数 = 亮风番数 + 杠牌番数总和 + 胡牌模式的番数总和
        // XXX 注意: 这个番数将作为输家的基础番数
        //
        final SettlementResult stmtResult1 = ziMoPlayer.getSettlementResult();
        winFan = stmtResult1.getLiangFengFan()
            + stmtResult1.sumOfGangFan()
            + stmtResult1.sumOfHuFan();

        // 遍历所有的输家,
        // 计算每个输家所输掉的番数和分数,
        // 输掉的番数 = 自摸玩家 ( 赢家 ) 的总番数 + 输家的亮风番数 + 输家的杠牌番数
        // 输掉的分数 = 输掉的番数 x 底分
        // XXX 注意: 如果玩家选择了 "漂", 这个会影响底分...
        //
        final List<Player> playerList = currRound.getPlayerListCopy();

        for (Player losePlayer : playerList) {
            if (null == losePlayer ||
                losePlayer.getUserId() == ziMoPlayer.getUserId()) {
                continue;
            }

            // 将赢家番数作为输家的基础番数
            int loseFan = winFan;

            // 获取输家结算结果
            final SettlementResult stmtResult0 = losePlayer.getSettlementResult();
            // 累加杠番
            loseFan += stmtResult0.getLiangFengFan()
                + stmtResult0.sumOfGangFan();

            if (currRound.getRuleSetting().is64FanFengDing()) {
                // 如果是 64 番封顶
                loseFan = Math.min(64, loseFan);
            }

            // 飘分 = 赢家飘分 + 输家飘分
            int piaoX = Math.max(0, ziMoPlayer.getCurrState().getPiaoX()) + Math.max(0, losePlayer.getCurrState().getPiaoX());

            int loseScore = loseFan + piaoX;
            totalWinScore += loseScore;

            LOGGER.info(
                "玩家 userId = {} 输掉 {} 分, atRoomId = {}, roundIndex = {}",
                losePlayer.getUserId(),
                loseScore,
                currRound.getRoomId(),
                currRound.getRoundIndex()
            );

            losePlayer.setCurrScore(-loseScore);
        }

        LOGGER.info(
            "玩家 userId = {} 自摸, 赢得 {} 分, atRoomId = {}, roundIndex = {}",
            ziMoPlayer.getUserId(),
            totalWinScore,
            currRound.getRoomId(),
            currRound.getRoundIndex()
        );

        ziMoPlayer.setCurrScore(totalWinScore);
    }

    /**
     * 执行点炮算分
     *
     * @param currRound     当前牌局
     * @param huPlayerList  胡牌玩家列表
     * @param dianPaoPlayer 点炮玩家
     */
    static private void doDianPaoScore(Round currRound, List<Player> huPlayerList, Player dianPaoPlayer) {
        if (null == currRound ||
            null == huPlayerList ||
            huPlayerList.isEmpty() ||
            null == dianPaoPlayer) {
            return;
        }

        int loseFan;
        int totalLoseScore = 0;

        // 先计算点炮玩家 ( 输家 ) 的总番数,
        // 总番数 = 亮风番数 + 杠牌番数总和
        // XXX 注意: 这个番数将作为赢家的基础番数
        //
        final SettlementResult stmtResult0 = dianPaoPlayer.getSettlementResult();
        loseFan = stmtResult0.getLiangFengFan()
            + stmtResult0.sumOfGangFan();

        // 遍历所有的赢家,
        // 计算每个赢家所赢得的番数和分数,
        // 赢得的番数 = 点炮玩家 ( 输家 ) 的总番数 + 赢家的亮风番数 + 赢家的杠牌番数 + 赢家的胡牌番数
        // 赢得的分数 = 输掉的番数 x 底分
        // XXX 注意: 如果玩家选择了 "漂", 这个会影响底分...
        //
        for (Player huPlayer : huPlayerList) {
            if (null == huPlayer) {
                continue;
            }

            int winFan = loseFan;

            final SettlementResult stmtResult1 = huPlayer.getSettlementResult();
            winFan += stmtResult1.getLiangFengFan()
                + stmtResult1.sumOfGangFan()
                + stmtResult1.sumOfHuFan();

            if (currRound.getRuleSetting().is64FanFengDing()) {
                // 如果是 64 番封顶
                winFan = Math.min(64, winFan);
            }

            // 飘分 = 赢家飘分 + 输家飘分
            int piaoX = Math.max(0, huPlayer.getCurrState().getPiaoX()) + Math.max(0, dianPaoPlayer.getCurrState().getPiaoX());

            int winScore = winFan + piaoX;
            totalLoseScore += winScore;

            LOGGER.info(
                "玩家 userId = {} 胡牌, 赢得 {} 分, atRoomId = {}, roundIndex = {}",
                huPlayer.getUserId(),
                winScore,
                currRound.getRoomId(),
                currRound.getRoundIndex()
            );

            huPlayer.setCurrScore(winScore);
        }

        LOGGER.info(
            "玩家 userId = {} 点炮, 输掉 {} 分, atRoomId = {}, roundIndex = {}",
            dianPaoPlayer.getUserId(),
            totalLoseScore,
            currRound.getRoomId(),
            currRound.getRoundIndex()
        );

        dianPaoPlayer.setCurrScore(-totalLoseScore);
    }

    /**
     * 统计亮风番数
     *
     * @param currPlayer 当前玩家
     */
    static private void countLiangFengFan(Player currPlayer) {
        if (null == currPlayer ||
            null == currPlayer.getMahjongLiangFeng().getKind()) {
            return;
        }

        // 获取亮风字典
        final Map<MahjongTileDef, Integer> liangFengMap = currPlayer.getMahjongLiangFeng().getCounterMapCopy();
        // 亮风总数
        int totalNum = 0;

        for (Integer num : liangFengMap.values()) {
            totalNum += num;
        }

        // 亮风总数 > 3 个,
        // 每多出一个加 1 分...
        currPlayer.getSettlementResult().setLiangFengFan(
            Math.max(0, totalNum - 3)
        );
    }

    /**
     * 统计杠牌番数
     *
     * @param currPlayer 当前玩家
     */
    static private void countGangFan(Player currPlayer) {
        if (null == currPlayer) {
            return;
        }

        // 杠牌模式字典
        Map<Integer, Integer> gangPatternMap = null;

        for (MahjongChiPengGang mahjongChiPengGang : currPlayer.getMahjongChiPengGangListCopy()) {
            if (null == mahjongChiPengGang) {
                continue;
            }

            if (mahjongChiPengGang.getKind() == MahjongChiPengGang.KindDef.MING_GANG ||
                mahjongChiPengGang.getKind() == MahjongChiPengGang.KindDef.BU_GANG) {
                if (null == gangPatternMap) {
                    gangPatternMap = new HashMap<>();
                }

                // 如果是明杠或者补杠,
                // 记 1 番
                int fan = gangPatternMap.getOrDefault(
                    mahjongChiPengGang.getKindIntVal(),
                    0 // 默认为 0
                );

                gangPatternMap.put(
                    mahjongChiPengGang.getKindIntVal(),
                    fan + 1
                );
            } else if (mahjongChiPengGang.getKind() == MahjongChiPengGang.KindDef.AN_GANG) {
                if (null == gangPatternMap) {
                    gangPatternMap = new HashMap<>();
                }

                // 如果是暗杠,
                // 记 2 番
                int fan = gangPatternMap.getOrDefault(
                    mahjongChiPengGang.getKindIntVal(),
                    0 // 默认为 0
                );

                gangPatternMap.put(
                    mahjongChiPengGang.getKindIntVal(),
                    fan + 2
                );
            }
        }

        currPlayer.getSettlementResult().setGangPatternMap(gangPatternMap);
    }
}
