package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.game.MJ_weihai_.RoomOverDetermine;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiChoiceQuestion;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiPengGangHuSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.LiangFengChoiceQuestion;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongChiPengGang;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongLiangFeng;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 同步房间数据指令处理器
 */
public final class SyncRoomDataCmdHandler implements ICmdHandler<MyCmdHandlerContext, MJ_weihai_Protocol.SyncRoomDataCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(SyncRoomDataCmdHandler.class);

    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        MJ_weihai_Protocol.SyncRoomDataCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        // 获取当前房间
        Room currRoom = RoomGroup.getByUserId(ctx.getFromUserId());

        if (null == currRoom ||
            RoomOverDetermine.determine(currRoom) ||
            currRoom.isForcedEnd()) {
            LOGGER.error(
                "用户所在房间为空或者已经结束, userId = {}",
                ctx.getFromUserId()
            );
            return;
        }

        // 获取执行玩家
        Player execPlayer = currRoom.getPlayerByUserId(ctx.getFromUserId());

        if (null == execPlayer) {
            LOGGER.error(
                "玩家不在房间中, userId = {}, atRoomId = {}",
                ctx.getFromUserId(),
                currRoom.getRoomId()
            );
            return;
        }

        // 创建消息构建器
        MJ_weihai_Protocol.SyncRoomDataResult.Builder b = MJ_weihai_Protocol.SyncRoomDataResult.newBuilder();

        // 填充房间和规则
        fillRoomAndRuleItem(b, currRoom);
        // 填充当前牌局
        fillCurrRound(b, currRoom);
        // 填充所有玩家
        fillAllPlayer(b, currRoom, ctx.getFromUserId());

        ctx.writeAndFlush(b.build());

        // 添加到广播器
        GameBroadcaster.add(
            ctx.getNettyChannel(), ctx.getRemoteSessionId(), ctx.getFromUserId()
        );

        // 单独发送选飘提示广播
        sendSelectPiaoHintBroadcast(
            ctx, currRoom
        );

        // 单独发送亮杠腚广播
        sendLiangGangDingBroadcast(
            ctx, currRoom
        );

        // 发送吃碰杠胡操作提示
        sendChiPengGangHuOpHintResult(
            ctx, currRoom
        );

        // 构建并发送房间解散消息,
        // 如果有的话...
        DissolveTheRoomCmdHandler.buildMsgAndSend(currRoom);
    }

    /**
     * 填充规则条目
     *
     * @param b        结果构建器
     * @param currRoom 当前房间
     */
    static private void fillRoomAndRuleItem(
        MJ_weihai_Protocol.SyncRoomDataResult.Builder b, Room currRoom) {
        if (null == b ||
            null == currRoom) {
            return;
        }

        b.setRoomId(currRoom.getRoomId());
        b.setRoomCreateTime(currRoom.getCreateTime());
        b.setRoomOwnerId(currRoom.getOwnerId());

        // 获取规则字典
        Map<Integer, Integer> ruleMap = currRoom.getRuleSetting().getInnerMap();

        for (Map.Entry<Integer, Integer> ruleItem : ruleMap.entrySet()) {
            if (null == ruleItem ||
                null == ruleItem.getKey() ||
                null == ruleItem.getValue()) {
                continue;
            }

            // 添加规则项目
            b.addRuleItem(
                MJ_weihai_Protocol.KeyAndVal.newBuilder()
                    .setKey(ruleItem.getKey())
                    .setVal(ruleItem.getValue())
            );
        }
    }

    /**
     * 填充牌局
     *
     * @param rootBuilder 结果构建器
     * @param currRoom    当前房间
     */
    static private void fillCurrRound(MJ_weihai_Protocol.SyncRoomDataResult.Builder rootBuilder, Room currRoom) {
        // 获取当前牌局
        final Round currRound = currRoom.getCurrRound();

        if (null == currRound) {
            // 如果当前牌局为空,
            // 全部默认为 -1
            rootBuilder.setCurrRoundIndex(-1);
            rootBuilder.setCurrActUserId(-1);
            rootBuilder.setRemainCardNum(-1);
            rootBuilder.setRemainTime(-1);
            return;
        }

        rootBuilder.setCurrRoundIndex(currRound.getRoundIndex());
        rootBuilder.setRemainCardNum(currRound.getRemainCardNum()); // 剩余卡牌 ( 麻将牌 ) 数量
        rootBuilder.setRemainTime(-1);

        if (null != currRound.getCurrActPlayer()) {
            rootBuilder.setCurrActUserId(
                currRound.getCurrActPlayer().getUserId()
            );
        }
    }

    /**
     * 填充 ( 所有 ) 玩家
     *
     * @param rootBuilder 根建器
     * @param currRoom    当前房间
     * @param fromUserId  来自用户 Id
     */
    static private void fillAllPlayer(MJ_weihai_Protocol.SyncRoomDataResult.Builder rootBuilder, Room currRoom, int fromUserId) {
        if (null == rootBuilder ||
            null == currRoom) {
            return;
        }

        List<Player> playerList;

        // 获取当前牌局
        Round currRound = currRoom.getCurrRound();

        if (null != currRound) {
            // 从当前牌局获取玩家列表
            playerList = currRound.getPlayerListCopy();
        } else {
            // 从当前房间获取玩家列表
            playerList = currRoom.getPlayerListCopy();
        }

        if (null == playerList) {
            LOGGER.error(
                "玩家列表为空, roomId = {}", currRoom.getRoomId()
            );
            return;
        }

        for (Player currPlayer : playerList) {
            if (null == currPlayer) {
                continue;
            }

            // 创建新的构建器
            MJ_weihai_Protocol.Player.Builder b = MJ_weihai_Protocol.Player.newBuilder();

            // 获取麻将手牌列表
            List<Integer> mahjongInHand = currPlayer.getMahjongInHandIntValList();
            // 获取麻将摸牌
            int mahjongMoPai = currPlayer.getMoPaiIntVal();

            // 使用遮罩值
            final boolean usingMaskVal = (currPlayer.getUserId() != fromUserId);

            if (usingMaskVal) {
                mahjongInHand = getMaskValList(mahjongInHand);
                mahjongMoPai = Math.min(mahjongMoPai, MahjongTileDef.MASK_VAL);
            }

            b.setUserId(currPlayer.getUserId())
                .setUserName(Objects.requireNonNullElse(currPlayer.getUserName(), ""))
                .setHeadImg(Objects.requireNonNullElse(currPlayer.getHeadImg(), ""))
                .setSex(currPlayer.getSex())
                .setClientIpAddr(Objects.requireNonNullElse(currPlayer.getClientIpAddr(), ""))
                .setSeatIndex(currPlayer.getSeatIndex())
                .setCurrScore(currPlayer.getCurrScore())
                .setTotalScore(currPlayer.getTotalScore())
                .setPiaoX(currPlayer.getCurrState().getPiaoX())
                .setRoomOwnerFlag(currPlayer.isRoomOwner())
                .setZhuangJiaFlag(currPlayer.getCurrState().isZhuangJia())
                // 设置麻将手中的牌、摸牌、打出的牌
                .addAllMahjongInHand(mahjongInHand)
                .setMahjongMoPai(mahjongMoPai)
                .addAllMahjongOutput(currPlayer.getMahjongOutputIntValList());

            // 填充麻将亮风
            fillLiangFeng(b, currPlayer);
            // 填充吃碰杠列表
            fillChiPengGangList(b, currPlayer, usingMaskVal);

            rootBuilder.addPlayer(b);
        }
    }

    /**
     * 获取遮罩值列表
     *
     * @param origValList 原值列表
     * @return 遮罩值列表
     */
    static private List<Integer> getMaskValList(List<Integer> origValList) {
        if (null == origValList ||
            origValList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> maskValList = new ArrayList<>();

        for (int i = 0; i < origValList.size(); i++) {
            maskValList.add(MahjongTileDef.MASK_VAL);
        }

        return maskValList;
    }

    /**
     * 填充亮风
     *
     * @param rootBuilder 消息构建器
     * @param currPlayer  当前玩家
     */
    static private void fillLiangFeng(MJ_weihai_Protocol.Player.Builder rootBuilder, Player currPlayer) {
        if (null == rootBuilder ||
            null == currPlayer) {
            return;
        }

        // 获取麻将亮风
        final MahjongLiangFeng liangFeng = currPlayer.getMahjongLiangFeng();

        if (null == liangFeng.getKind()) {
            // 玩家还没有亮风
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

        // 设置麻将亮风
        rootBuilder.setMahjongLiangFeng(b);
    }

    /**
     * 填充吃碰杠列表
     *
     * @param rootBuilder  消息构建器
     * @param currPlayer   当前玩家
     * @param usingMaskVal 使用遮罩值
     */
    static private void fillChiPengGangList(
        MJ_weihai_Protocol.Player.Builder rootBuilder, Player currPlayer, boolean usingMaskVal) {
        if (null == rootBuilder ||
            null == currPlayer) {
            return;
        }

        for (MahjongChiPengGang mahjongChiPengGang : currPlayer.getMahjongChiPengGangListCopy()) {
            if (null == mahjongChiPengGang) {
                continue;
            }

            // 获取吃碰杠牌
            int tx = mahjongChiPengGang.getTXIntVal();

            if (MahjongChiPengGang.KindDef.AN_GANG == mahjongChiPengGang.getKind() &&
                usingMaskVal) {
                tx = MahjongTileDef.MASK_VAL;
            }

            rootBuilder.addMahjongChiPengGang(MJ_weihai_Protocol.MahjongChiPengGang.newBuilder()
                .setKind(mahjongChiPengGang.getKindIntVal())
                .setTX(tx)
                .setT0(mahjongChiPengGang.getT0IntVal())
                .setT1(mahjongChiPengGang.getT1IntVal())
                .setT2(mahjongChiPengGang.getT2IntVal())
                .setFromUserId(mahjongChiPengGang.getFromUserId())
            );
        }
    }

    /**
     * 给用户单独发送选飘提示广播
     *
     * @param ctx      信道处理器上下文
     * @param currRoom 当前房间
     */
    static private void sendSelectPiaoHintBroadcast(
        MyCmdHandlerContext ctx, Room currRoom) {
        if (null == ctx ||
            null == currRoom) {
            return;
        }

        if (!currRoom.getRuleSetting().isPiaoFen() ||
            currRoom.isDingPiaoEnded()) {
            // 如果当前房间没有勾选飘分,
            // 或者如果定飘已结束,
            return;
        }

        if (currRoom.getPlayerListCopy().size() < currRoom.getRuleSetting().getMaxPlayer()) {
            // 如果玩家还没有到齐,
            return;
        }

        for (Player currPlayer : currRoom.getPlayerListCopy()) {
            if (null == currPlayer ||
                !currPlayer.getCurrState().isPrepare()) {
                // 如果还有人没有准备好,
                return;
            }
        }

        // 获取当前玩家
        Player currPlayer = currRoom.getPlayerByUserId(ctx.getFromUserId());

        if (null == currPlayer) {
            return;
        }

        if (currPlayer.getCurrState().getPiaoX() >= 0) {
            // 玩家已选过飘,
            return;
        }

        MJ_weihai_Protocol.SelectPiaoHintBroadcast msgObj = MJ_weihai_Protocol.SelectPiaoHintBroadcast.newBuilder()
            .setBuPiao(true)
            .setPiao1(true)
            .setPiao2(true)
            .setPiao3(true)
            .build();

        ctx.writeAndFlush(msgObj);
    }

    /**
     * 单独发送亮杠腚广播
     *
     * @param ctx      信道处理器上下文
     * @param currRoom 当前房间
     */
    static private void sendLiangGangDingBroadcast(
        MyCmdHandlerContext ctx,
        Room currRoom) {
        if (null == ctx ||
            null == currRoom) {
            return;
        }

        if (!currRoom.getRuleSetting().isLiangGangDing()) {
            return;
        }

        // 获取当前牌局
        final Round currRound = currRoom.getCurrRound();

        if (null == currRound ||
            currRound.isEnded()) {
            return;
        }

        final MahjongTileDef t0 = currRound.getLiangGangDingT0();
        int t0IntVal = (null == t0) ? -1 : t0.getIntVal();
        int t1IntVal = -1; // 按照需求先显示一个, 所以第二张牌用 -1 代替

        MJ_weihai_Protocol.MahjongLiangGangDingBroadcast msgObj = MJ_weihai_Protocol.MahjongLiangGangDingBroadcast.newBuilder()
            .setT0(t0IntVal)
            .setT1(t1IntVal)
            .build();

        ctx.writeAndFlush(msgObj);
    }

    /**
     * 发送吃碰杠胡操作提示
     *
     * @param ctx      信道处理器上下文
     * @param currRoom 当前房间
     */
    static private void sendChiPengGangHuOpHintResult(
        MyCmdHandlerContext ctx,
        Room currRoom) {
        if (null == ctx ||
            null == currRoom) {
            return;
        }

        // 获取当前牌局
        final Round currRound = currRoom.getCurrRound();

        if (null == currRound) {
            return;
        }

        // 获取吃碰杠胡会议
        final ChiPengGangHuSession sessionObj = currRound.getChiPengGangHuSession();

        if (null == sessionObj) {
            return;
        }

        // 获取当前对话
        final ChiPengGangHuSession.Dialog currDialog = sessionObj.getCurrDialog();

        if (null == currDialog ||
            !currDialog.isCurrActUserId(ctx.getFromUserId())) {
            return;
        }

        int fromUserId = ctx.getFromUserId();

        boolean canChi = currDialog.isUserIdCanChi(fromUserId);
        boolean canPeng = currDialog.isUserIdCanPeng(fromUserId);
        boolean canMingGang = currDialog.isUserIdCanMingGang(fromUserId);
        boolean canAnGang = currDialog.isUserIdCanAnGang(fromUserId);
        boolean canBuGang = currDialog.isUserIdCanBuGang(fromUserId);
        boolean canHu = currDialog.isUserIdCanHu(fromUserId);
        boolean canZiMo = currDialog.isUserIdCanZiMo(fromUserId);
        boolean canLiangFeng = currDialog.isUserIdCanLiangFeng(fromUserId);
        boolean canBuFeng = currDialog.isUserIdCanBuFeng(fromUserId);

        MJ_weihai_Protocol.MahjongChiPengGangHuOpHintResult.Builder
            b0 = MJ_weihai_Protocol.MahjongChiPengGangHuOpHintResult.newBuilder()
            .setOpHintChi(canChi)
            .setOpHintPeng(canPeng)
            .setOpHintGang(canMingGang || canAnGang || canBuGang)
            .setOpHintHu(canHu || canZiMo)
            .setOpHintLiangFeng(canLiangFeng)
            .setOpHintBuFeng(canBuFeng);

        // 获取吃牌选择题
        final ChiChoiceQuestion chiChoiceQuestion = sessionObj.getChiChoiceQuestion();

        if (canChi &&
            null != chiChoiceQuestion) {
            // 构建吃牌选择题
            MJ_weihai_Protocol.ChiChoiceQuestion.Builder b1 = MJ_weihai_Protocol.ChiChoiceQuestion.newBuilder()
                .setChiT(chiChoiceQuestion.getChiTIntVal())
                .setDisplayOptionA(chiChoiceQuestion.isDisplayOptionA())
                .setDisplayOptionB(chiChoiceQuestion.isDisplayOptionB())
                .setDisplayOptionC(chiChoiceQuestion.isDisplayOptionC());

            b0.setChiChoiceQuestion(b1);
        }

        // 获取亮风选择题
        final LiangFengChoiceQuestion liangFengChoiceQuestion = sessionObj.getLiangFengChoiceQuestion();

        if (canLiangFeng &&
            null != liangFengChoiceQuestion) {
            MJ_weihai_Protocol.LiangFengChoiceQuestion.Builder b2 = MJ_weihai_Protocol.LiangFengChoiceQuestion.newBuilder()
                .setLuanMao(liangFengChoiceQuestion.isLuanMao())
                .setDisplayOptionDongFeng(liangFengChoiceQuestion.isDisplayOptionDongFeng())
                .setDisplayOptionNanFeng(liangFengChoiceQuestion.isDisplayOptionNanFeng())
                .setDisplayOptionXiFeng(liangFengChoiceQuestion.isDisplayOptionXiFeng())
                .setDisplayOptionBeiFeng(liangFengChoiceQuestion.isDisplayOptionBeiFeng())
                .setDisplayOptionHongZhong(liangFengChoiceQuestion.isDisplayOptionHongZhong())
                .setDisplayOptionFaCai(liangFengChoiceQuestion.isDisplayOptionFaCai())
                .setDisplayOptionBaiBan(liangFengChoiceQuestion.isDisplayOptionBaiBan());

            b0.setLiangFengChoiceQuestion(b2);
        }

        LOGGER.info(
            "数据同步! 给玩家发送吃碰杠胡操作提示, userId = {}, atRoomId = {}, roundIndex = {}, t = {}, canChi = {}, canPeng = {}, canGang = {}, canHu = {}, canLiangFeng = {}, canBuFeng = {}",
            fromUserId,
            currRoom.getRoomId(),
            currRound.getRoundIndex(),
            sessionObj.getFromOtherzTIntVal(),
            canChi,
            canPeng,
            canMingGang || canAnGang || canBuGang,
            canHu,
            canLiangFeng,
            canBuFeng
        );

        ctx.writeAndFlush(b0.build());
    }
}
