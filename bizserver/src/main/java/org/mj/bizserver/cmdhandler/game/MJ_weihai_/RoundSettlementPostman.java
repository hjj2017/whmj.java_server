package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongLiangFeng;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.StateTable;

import java.util.List;
import java.util.Map;

/**
 * 牌局结算投递员
 */
final class RoundSettlementPostman {
    /**
     * 私有化类默认构造器
     */
    private RoundSettlementPostman() {
    }

    /**
     * 根据当前牌局投递牌局结算
     *
     * @param currRound 当前牌局
     */
    static void post(Round currRound) {
        if (null == currRound) {
            return;
        }

        MJ_weihai_Protocol.RoundSettlementBroadcast.Builder b0 = MJ_weihai_Protocol.RoundSettlementBroadcast.newBuilder();

        // 获取玩家列表
        List<Player> playerList = currRound.getPlayerListCopy();

        for (Player currPlayer : playerList) {
            if (null == currPlayer ||
                null == currPlayer.getCurrState()) {
                continue;
            }

            // 获取状态表
            final StateTable statTab = currPlayer.getCurrState();

            MJ_weihai_Protocol.RoundSettlementBroadcast.SettlementItem.Builder b1 = MJ_weihai_Protocol.RoundSettlementBroadcast.SettlementItem.newBuilder();
            b1.setUserId(currPlayer.getUserId())
                .setCurrScore(currPlayer.getCurrScore())
                .setTotalScore(currPlayer.getTotalScore())
                .setSeatIndex(currPlayer.getSeatIndex())
                .setPiaoX(currPlayer.getCurrState().getPiaoX())
                .setRoomOwnerFlag(currPlayer.isRoomOwner())
                .setZhuangJiaFlag(currPlayer.getCurrState().isZhuangJia())
                // 设置麻将手中的牌、摸牌、打出的牌
                .addAllMahjongInHand(currPlayer.getMahjongInHandIntValList())
                .setMahjongHuOrZiMo(statTab.isZiMo() ? statTab.getMahjongZiMoIntVal() : statTab.getMahjongHuIntVal())
                .setHu(statTab.isHu())
                .setDianPao(statTab.isDianPao())
                .setZiMo(statTab.isZiMo());

            // 填充胡牌模式列表
            fillHuPatternList(b1, currPlayer);
            // 填充杠牌模式列表
            fillGangPatternList(b1, currPlayer);
            // 填充吃碰杠列表
            fillChiPengGangList(b1, currPlayer);
            // 填充亮风
            fillLiangFeng(b1, currPlayer);
            // 添加结算项
            b0.addSettlementItem(b1);
        }

        // 广播牌局结算
        GameBroadcaster.broadcast(currRound, b0.build());
    }

    /**
     * 填充胡牌模式列表
     *
     * @param rootBuilder 消息构建器
     * @param currPlayer  当前玩家
     */
    static private void fillHuPatternList(
        MJ_weihai_Protocol.RoundSettlementBroadcast.SettlementItem.Builder rootBuilder,
        Player currPlayer) {
        if (null == rootBuilder ||
            null == currPlayer) {
            return;
        }

        // 获取胡牌模式字典
        Map<Integer, Integer> huPatternMap = currPlayer.getSettlementResult().getHuPatternMapCopy();

        if (null == huPatternMap ||
            huPatternMap.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, Integer> huPattern : huPatternMap.entrySet()) {
            if (null == huPattern ||
                null == huPattern.getKey() ||
                null == huPattern.getValue()) {
                continue;
            }

            rootBuilder.addHuPattern(
                MJ_weihai_Protocol.KeyAndVal.newBuilder()
                    .setKey(huPattern.getKey())
                    .setVal(huPattern.getValue())
            );
        }
    }

    /**
     * 填充杠牌模式列表
     *
     * @param rootBuilder 消息构建器
     * @param currPlayer  当前玩家
     */
    static private void fillGangPatternList(
        MJ_weihai_Protocol.RoundSettlementBroadcast.SettlementItem.Builder rootBuilder,
        Player currPlayer) {
        if (null == rootBuilder ||
            null == currPlayer) {
            return;
        }

        // 获取胡牌模式字典
        final Map<Integer, Integer> gangPatternMap = currPlayer.getSettlementResult().getGangPatternMapCopy();

        if (null == gangPatternMap ||
            gangPatternMap.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, Integer> gangPattern : gangPatternMap.entrySet()) {
            if (null == gangPattern ||
                null == gangPattern.getKey() ||
                null == gangPattern.getValue()) {
                continue;
            }

            rootBuilder.addGangPattern(
                MJ_weihai_Protocol.KeyAndVal.newBuilder()
                    .setKey(gangPattern.getKey())
                    .setVal(gangPattern.getValue())
            );
        }
    }

    /**
     * 填充吃碰杠列表
     *
     * @param rootBuilder 消息构建器
     * @param currPlayer  当前玩家
     */
    static private void fillChiPengGangList(
        MJ_weihai_Protocol.RoundSettlementBroadcast.SettlementItem.Builder rootBuilder,
        Player currPlayer) {
        if (null == rootBuilder ||
            null == currPlayer) {
            return;
        }

        for (MahjongChiPengGang mahjongChiPengGang : currPlayer.getMahjongChiPengGangListCopy()) {
            if (null == mahjongChiPengGang) {
                continue;
            }

            rootBuilder.addMahjongChiPengGang(MJ_weihai_Protocol.MahjongChiPengGang.newBuilder()
                .setKind(mahjongChiPengGang.getKindIntVal())
                .setTX(mahjongChiPengGang.getTXIntVal())
                .setT0(mahjongChiPengGang.getT0IntVal())
                .setT1(mahjongChiPengGang.getT1IntVal())
                .setT2(mahjongChiPengGang.getT2IntVal())
                .setFromUserId(mahjongChiPengGang.getFromUserId())
            );
        }
    }

    /**
     * 填充亮风
     *
     * @param rootBuilder 消息构建器
     * @param currPlayer  当前玩家
     */
    static private void fillLiangFeng(
        MJ_weihai_Protocol.RoundSettlementBroadcast.SettlementItem.Builder rootBuilder,
        Player currPlayer) {
        if (null == rootBuilder ||
            null == currPlayer) {
            return;
        }

        // 麻将亮风
        final MahjongLiangFeng liangFeng = currPlayer.getMahjongLiangFeng();

        if (null == liangFeng.getKind()) {
            return;
        }

        // 获取计数器字典
        final Map<MahjongTileDef, Integer> counterMap = liangFeng.getCounterMapCopy();

        // 亮风消息构建器
        MJ_weihai_Protocol.MahjongLiangFeng.Builder b = MJ_weihai_Protocol.MahjongLiangFeng.newBuilder()
            // 亮风种类
            .setKind(liangFeng.getKind().getIntVal())
            // 东南西北
            .setNumOfDongFeng(counterMap.getOrDefault(MahjongTileDef.DONG_FENG, 0))
            .setNumOfNanFeng(counterMap.getOrDefault(MahjongTileDef.NAN_FENG, 0))
            .setNumOfXiFeng(counterMap.getOrDefault(MahjongTileDef.XI_FENG, 0))
            .setNumOfBeiFeng(counterMap.getOrDefault(MahjongTileDef.BEI_FENG, 0))
            // 中发白
            .setNumOfHongZhong(counterMap.getOrDefault(MahjongTileDef.HONG_ZHONG, 0))
            .setNumOfFaCai(counterMap.getOrDefault(MahjongTileDef.FA_CAI, 0))
            .setNumOfBaiBan(counterMap.getOrDefault(MahjongTileDef.BAI_BAN, 0));

        rootBuilder.setMahjongLiangFeng(b);
    }
}
