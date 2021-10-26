package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiPengGangHuSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongLiangFeng;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RuleSetting;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongInHandChanged;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongLiangFeng;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * 亮风
 */
interface MJ_weihai_BizLogic$liangFeng {
    /**
     * 日至对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$liangFeng.class);

    /**
     * 亮风
     *
     * @param userId  用户 Id
     * @param t0      第一张牌
     * @param t1      第二张牌
     * @param t2      第三张牌
     * @param resultX 业务结果
     */
    default void liangFeng(
        int userId, MahjongTileDef t0, MahjongTileDef t1, MahjongTileDef t2, BizResultWrapper<ReporterTeam> resultX) {
        if (userId <= 0 ||
            null == t0 ||
            null == t1 ||
            null == t2 ||
            null == resultX) {
            return;
        }

        // 获取当前房间
        Room currRoom = RoomGroup.getByUserId(userId);

        if (null == currRoom) {
            LOGGER.error(
                "当前房间为空, userId = {}",
                userId
            );
            return;
        }

        // 获取当前牌局
        Round currRound = currRoom.getCurrRound();

        if (null == currRound) {
            LOGGER.error(
                "当前牌局为空, userId = {}, atRoomId = {}",
                userId,
                currRoom.getRoomId()
            );
            return;
        }

        // 获取吃碰杠胡会话
        ChiPengGangHuSession sessionObj = currRound.getChiPengGangHuSession();

        if (null == sessionObj) {
            LOGGER.error(
                "\"吃碰杠胡会议\" 为空, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 获取当前对话
        ChiPengGangHuSession.Dialog currDialog = sessionObj.getCurrDialog();

        if (null == currDialog) {
            LOGGER.error(
                "\"吃碰杠胡对话\" 为空, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        if (!currDialog.isCurrActUserId(userId)) {
            LOGGER.error(
                "还没轮到你亮风呢, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        if (!currDialog.isUserIdCanLiangFeng(userId)) {
            LOGGER.error(
                "用户 Id 不是可以亮风的玩家, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        if (!checkBaseCond(t0, t1, t2, currRound.getRuleSetting())) {
            LOGGER.error(
                "三张要亮风的牌不满足基本条件, userId = {}, atRoomId = {}, roundIndex = {}, t0 = {}, t1 = {}, t2 = {}, isLuanMao = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex(),
                t0.getIntVal(),
                t1.getIntVal(),
                t2.getIntVal(),
                currRound.getRuleSetting().isLuanMao()
            );
            return;
        }

        // 获取执行操作的玩家
        final Player execPlayer = currRound.getPlayerByUserId(userId);

        if (null == execPlayer) {
            LOGGER.error(
                "当前玩家为空, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        Set<MahjongTileDef> tempSet = new HashSet<>(3);
        tempSet.add(t0);
        tempSet.add(t1);
        tempSet.add(t2);

        // 先查找并移除刚摸到的麻将牌
        tempSet.remove(execPlayer.getMoPai());

        for (MahjongTileDef tX : tempSet) {
            if (!execPlayer.hasAMahjongTileInHand(tX)) {
                // 看看手中是否有指定的麻将牌,
                // 如果有缺失,
                // 是不能亮风的...
                LOGGER.error(
                    "三张要亮风的牌至少有一张不在玩家手牌列表中, userId = {}, atRoomId = {}, roundIndex = {}, t0 = {}, t1 = {}, t2 = {}, isLuanMao = {}, mahjongInHand = {}",
                    userId,
                    currRoom.getRoomId(),
                    currRound.getRoundIndex(),
                    t0.getIntVal(),
                    t1.getIntVal(),
                    t2.getIntVal(),
                    currRound.getRuleSetting().isLuanMao(),
                    execPlayer.getMahjongInHandIntValList()
                );
                return;
            }
        }

        // 清除当前牌局记录的吃碰杠胡会议
        currRound.setChiPengGangHuSession(null);

        LOGGER.info(
            "玩家亮风, userId = {}, atRoomId = {}, roundIndex = {}, t0 = {}, t1 = {}, t2 = {}, isLuanMao = {}",
            userId,
            currRoom.getRoomId(),
            currRound.getRoundIndex(),
            t0.getIntVal(),
            t1.getIntVal(),
            t2.getIntVal(),
            currRound.getRuleSetting().isLuanMao()
        );

        if (execPlayer.getMoPai() == t0 ||
            execPlayer.getMoPai() == t1 ||
            execPlayer.getMoPai() == t2) {
            execPlayer.setMoPai(null);
        }

        // 移除手中的麻将牌
        for (MahjongTileDef tX : tempSet) {
            execPlayer.removeAMahjongTileInHand(tX);
        }

        if (null != execPlayer.getMoPai()) {
            // 如果刚摸到的牌不为空值,
            // 则说明刚摸到的牌没有用来亮风,
            // 要将这张牌放到手牌列表中...
            execPlayer.addAMahjongTileInHand(execPlayer.getMoPai());
            execPlayer.setMoPai(null);
        }

        // 亮风种类
        MahjongLiangFeng.KindDef liangFengKind;

        if (currRound.getRuleSetting().isLuanMao()) {
            // 乱锚
            liangFengKind = MahjongLiangFeng.KindDef.LUAN_MAO;
        } else {
            if (t0.getSuit() == MahjongTileDef.Suit.FENG) {
                // 亮的是风牌 ( 东南西北 )
                liangFengKind = MahjongLiangFeng.KindDef.FENG;
            } else {
                // 亮的是箭牌 ( 中发白 )
                liangFengKind = MahjongLiangFeng.KindDef.JIAN;
            }
        }

        // 执行亮风
        execPlayer.getMahjongLiangFeng().doLiangFeng(
            liangFengKind, t0, t1, t2
        );

        // 因为亮风之后该打出的牌还没有打出,
        // 那么手里的最后一张牌默认设置为摸到的牌,
        // 以方便后面调用自动出牌逻辑...
        MahjongTileDef freeT = execPlayer.getTheRightmostMahjongTileInHand();
        execPlayer.removeAMahjongTileInHand(freeT);
        execPlayer.setMoPai(freeT);

        ReporterTeam rptrTeam = resultX.getFinalResult();

        if (null == rptrTeam) {
            rptrTeam = new ReporterTeam(currRoom.getRoomId());
            resultX.setFinalResult(rptrTeam);
        }

        // 记录手牌变化
        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongInHandChanged(
                execPlayer.getUserId(),
                execPlayer.getMahjongInHandCopy(),
                execPlayer.getMoPai()
            )
        )).createMaskCopy());

        // 记录麻将亮风
        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongLiangFeng(
                execPlayer.getUserId(),
                execPlayer.getMahjongLiangFeng().getKind(),
                execPlayer.getMahjongLiangFeng().getCounterMapCopy()
            )
        )));
    }

    /**
     * 检查基础条件,
     * <ol>
     *     <li>三张牌必须是东南西北中发白;</li>
     *     <li>如果不是乱锚, 那么三张牌花色必须一致;</li>
     * </ol>
     *
     * @param t0          第一张牌
     * @param t1          第二张牌
     * @param t2          第三张牌
     * @param ruleSetting 规则设置
     * @return true = 满足基础条件, false = 不满足基础条件
     */
    static private boolean checkBaseCond(MahjongTileDef t0, MahjongTileDef t1, MahjongTileDef t2, RuleSetting ruleSetting) {
        if (null == ruleSetting) {
            return false;
        }

        final MahjongTileDef[] tArray = {
            t0, t1, t2,
        };

        for (MahjongTileDef currT : tArray) {
            if (null == currT) {
                return false;
            }

            if (currT.getSuit() != MahjongTileDef.Suit.FENG &&
                currT.getSuit() != MahjongTileDef.Suit.JIAN) {
                return false;
            }
        }

        if (t0 == t1 ||
            t0 == t2 ||
            t1 == t2) {
            // 如果三张牌中有两张是相同的,
            // 也不能放风
            return false;
        }

        if (ruleSetting.isLuanMao()) {
            return true;
        } else {
            return t0.getSuit() == t1.getSuit() && t0.getSuit() == t2.getSuit();
        }
    }
}
