package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiChoiceQuestion;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiPengGangHuSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RuleSetting;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongChiPengGangHuOpHint;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongChuPai;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongInHandChanged;
import org.mj.comm.util.OutParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 出牌
 */
interface MJ_weihai_BizLogic$chuPai {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$chuPai.class);

    /**
     * 出牌
     *
     * @param userId  用户 Id
     * @param tChuPai 要出的是哪一张牌
     * @param resultX 业务结果
     */
    default void chuPai(final int userId, final MahjongTileDef tChuPai, final BizResultWrapper<ReporterTeam> resultX) {
        if (userId <= 0 ||
            null == tChuPai ||
            null == resultX) {
            return;
        }

        // 获取当前房间
        final Room currRoom = RoomGroup.getByUserId(userId);

        if (null == currRoom) {
            LOGGER.error(
                "当前房间为空, userId = {}",
                userId
            );
            return;
        }

        // 获取当前牌局
        final Round currRound = currRoom.getCurrRound();

        if (null == currRound) {
            LOGGER.error(
                "当前牌局为空, userId = {}, atRoomId = {}",
                userId,
                currRoom.getRoomId()
            );
            return;
        }

        if (null != currRound.getChiPengGangHuSession()) {
            LOGGER.error(
                "存在 \"吃碰杠胡会话\", 不能出牌! userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 获取行动玩家
        final Player execPlayer = currRound.getCurrActPlayer();

        if (null == execPlayer ||
            userId != execPlayer.getUserId()) {
            LOGGER.error(
                "还没轮到你出牌呢, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        ReporterTeam rptrTeam = resultX.getFinalResult();

        if (null == rptrTeam) {
            rptrTeam = new ReporterTeam(currRoom.getRoomId());
            resultX.setFinalResult(rptrTeam);
        }

        if (tChuPai == execPlayer.getMoPai()) {
            // 如果要打出的牌与刚摸到的牌相同,
            // 记录日志信息
            LOGGER.info(
                "打出刚摸到的牌, userId = {}, userName = {}, atRoomId = {}, roundIndex = {}, t = {}",
                userId,
                execPlayer.getUserName(),
                currRoom.getRoomId(),
                currRound.getRoundIndex(),
                tChuPai.getIntVal()
            );

            // 打出刚摸到的牌
            execPlayer.getMahjongOutput().add(execPlayer.getMoPai());
            execPlayer.setMoPai(null);

            rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
                new Wordz_MahjongChuPai(userId, tChuPai)
            )));
        } else {
            // 如果要打出的牌在玩家手里,
            // 先看看玩家手里是否真的有这张牌
            if (!execPlayer.hasAMahjongTileInHand(tChuPai)) {
                LOGGER.error(
                    "玩家手里没有这张牌, userId = {}, atRoomId = {}, roundIndex = {}, chuPai = {}",
                    userId,
                    currRoom.getRoomId(),
                    currRound.getRoundIndex(),
                    tChuPai.getIntVal()
                );
                return;
            }

            // 移除手里的牌, 并将刚摸到的牌放到手里
            execPlayer.removeAMahjongTileInHand(tChuPai);
            execPlayer.addAMahjongTileInHand(execPlayer.getMoPai());
            execPlayer.setMoPai(null);
            execPlayer.getMahjongOutput().add(tChuPai);

            LOGGER.info(
                "玩家出牌, userId = {}, atRoomId = {}, roundIndex = {}, chuPai = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex(),
                tChuPai.getIntVal()
            );

            rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
                new Wordz_MahjongChuPai(userId, tChuPai)
            )));

            rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
                new Wordz_MahjongInHandChanged(
                    userId,
                    execPlayer.getMahjongInHandCopy(),
                    execPlayer.getMoPai()
                )
            ));
        }

        // 出牌之后需要清零 "刚好有杠的数量",
        // XXX 注意: 是除我之外的其他人需要做这个操作,
        // 因为要考虑杠后点炮的情况
        for (Player pCurr : currRound.getPlayerListCopy()) {
            if (null != pCurr &&
                pCurr.getUserId() != execPlayer.getUserId()) {
                pCurr.getCurrState().resetJustGangNum();
            }
        }

        if (testChiPengGangHuAndExec(currRoom, currRound, userId, tChuPai, rptrTeam)) {
            // 如果有吃碰杠胡操作,
            return;
        }

        // 移动到下一个活动用户
        currRound.moveToNextActSeatIndex();

        // 下一个用户摸牌
        MJ_weihai_BizLogic.getInstance().moPai(currRoom.getRoomId(), resultX);
    }

    /**
     * 测试吃碰杠胡并执行 ( 给客户端发送操作提示 )
     *
     * @param currRound 当前牌局
     * @param userId    用户 Id
     * @param t         麻将牌
     * @param rptrTeam  记者小队
     * @return true = 有吃碰杠胡操作, false = 没有吃碰杠胡操作
     */
    private boolean testChiPengGangHuAndExec(
        final Room currRoom, final Round currRound, final int userId, final MahjongTileDef t, final ReporterTeam rptrTeam) {
        if (null == currRoom ||
            null == currRoom.getRuleSetting() ||
            null == currRound ||
            userId <= 0 ||
            null == t ||
            null == rptrTeam) {
            return false;
        }

        // ( 输出参数 ) 吃牌选择题
        final OutParam<ChiChoiceQuestion> out_chiChoiceQuestion = new OutParam<>();
        // 获取规则设置
        final RuleSetting ruleSetting = currRoom.getRuleSetting();

        // 查找可以吃碰杠的用户 Id
        int userIdCanChi = ruleSetting.isZhiPengBuChi()
            ? -1 // 如果只能碰不能吃, 那么就直接用 -1
            : ChiPengGangHuFinder.findUserIdCanChi(currRound, t, userId, out_chiChoiceQuestion);

        int userIdCanPeng = ChiPengGangHuFinder.findUserIdCanPeng(currRound, t, userId);
        int userIdCanMingGang = ChiPengGangHuFinder.findUserIdCanMingGang(currRound, t, userId);
        Set<Integer> userIdSetCanHu = ChiPengGangHuFinder.findUserIdSetCanHu(currRound, t, userId);

        if (-1 == userIdCanChi &&
            -1 == userIdCanPeng &&
            -1 == userIdCanMingGang &&
            isNullOrEmpty(userIdSetCanHu)) {
            // 如果没有可以吃碰杠胡的用户,
            return false;
        }

        // 如果有玩家可以吃碰杠胡
        final ChiPengGangHuSession sessionObj = new ChiPengGangHuSession(t, userId);
        sessionObj.putUserIdCanChi(userIdCanChi, out_chiChoiceQuestion.getVal());
        sessionObj.putUserIdCanPeng(userIdCanPeng);
        sessionObj.putUserIdCanMingGang(userIdCanMingGang);
        sessionObj.addAllUserIdCanHu(userIdSetCanHu);
        // 是否一炮多响
        sessionObj.putYiPaoDuoXiang(ruleSetting.isYiPaoDuoXiang());

        if (buildPrivateWordzAndAdd(
            currRound, sessionObj, rptrTeam)) {
            // 设置到当前牌局
            currRound.setChiPengGangHuSession(sessionObj);
            return true;
        }

        return false;
    }

    /**
     * 是否为空
     *
     * @param aSet 集合对象
     * @return true = 为空, false = 不为空
     */
    static private boolean isNullOrEmpty(Set<?> aSet) {
        return null == aSet || aSet.isEmpty();
    }

    /**
     * 构建并添加词条
     *
     * @param currRound  当前牌局
     * @param sessionObj 吃碰杠胡会话
     * @param rptrTeam   记者小队
     * @return true = 成功, false = 失败
     */
    static private boolean buildPrivateWordzAndAdd(
        Round currRound, ChiPengGangHuSession sessionObj, ReporterTeam rptrTeam) {
        if (null == currRound ||
            null == sessionObj ||
            null == sessionObj.getCurrDialog() ||
            null == rptrTeam) {
            return false;
        }

        // 获取吃碰杠胡对话
        ChiPengGangHuSession.Dialog currDialog = sessionObj.getCurrDialog();
        // 获取可以操作的用户 Id 集合
        Set<Integer> userIdSetCanOp = currDialog.getUserIdSetCanOp();

        if (null == userIdSetCanOp ||
            userIdSetCanOp.isEmpty()) {
            return false;
        }

        for (Integer userIdCanOp : userIdSetCanOp) {
            // 获取吃碰杠胡玩家
            final Player chiPengGangHuPlayer = currRound.getPlayerByUserId(userIdCanOp);

            if (null == chiPengGangHuPlayer) {
                continue;
            }

            boolean canChi = currDialog.isUserIdCanChi(userIdCanOp);
            boolean canPeng = currDialog.isUserIdCanPeng(userIdCanOp);
            boolean canMingGang = currDialog.isUserIdCanMingGang(userIdCanOp);
            boolean canHu = currDialog.isUserIdCanHu(userIdCanOp);

            rptrTeam.addPrivateWordz(new Wordz_MahjongChiPengGangHuOpHint(
                chiPengGangHuPlayer.getUserId(),
                canChi, sessionObj.getChiChoiceQuestion(), canPeng, canMingGang, canHu
            ));

            LOGGER.info(
                "准备给用户推送 \"吃碰杠胡操作\" 提示, userId = {}, atRoomId = {}, roundIndex = {}, 吃 = {}, 碰 = {}, 杠 = {}, 胡 = {}",
                userIdCanOp,
                currRound.getRoomId(),
                currRound.getRoundIndex(),
                canChi,
                canPeng,
                canMingGang,
                canHu
            );
        }

        return true;
    }
}
