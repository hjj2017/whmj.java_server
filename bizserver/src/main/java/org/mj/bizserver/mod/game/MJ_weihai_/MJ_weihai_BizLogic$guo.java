package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiPengGangHuSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongChiPengGangHuOpHint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 过 ( 玩家手动取消吃碰杠胡 )
 */
interface MJ_weihai_BizLogic$guo {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$guo.class);

    /**
     * 过
     *
     * @param userId  用户 Id
     * @param resultX 业务结果
     */
    default void guo(int userId, BizResultWrapper<ReporterTeam> resultX) {
        if (userId <= 0 ||
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
                "还没轮到你过牌呢, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 擦除用户 Id
        currDialog.eraseUserId(userId);

        if (!currDialog.isFinished()) {
            // 如果当前对话尚未结束,
            // 这种条件一般是出现在一炮多响的情况
            LOGGER.warn(
                "对话尚未结束, 需要等待其他用户操作, atRoomId = {}, roundIndex = {}",
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 当前玩家执行 "过" 操作之后,
        // 移动下一对话
        sessionObj.moveToNextDialog();

        ReporterTeam rptrTeam = resultX.getFinalResult();

        if (null == rptrTeam) {
            rptrTeam = new ReporterTeam(currRoom.getRoomId());
            resultX.setFinalResult(rptrTeam);
        }

        // 更新当前对话
        currDialog = sessionObj.getCurrDialog();

        if (null != currDialog) {
            // 如果当前对话框不为空,
            // 则说明还有可以吃碰杠胡的玩家,
            if (buildPrivateWordzAndAdd(currRound, sessionObj, rptrTeam)) {
                return;
            }
        }

        //
        // 如果用户 Id 为空,
        // 这说明已经没有可以吃碰杠的用户了,
        // 可以回到游戏主逻辑!
        //

        // 清除吃碰杠胡会话
        currRound.setChiPengGangHuSession(null);

        // 获取当前活动玩家
        Player currActPlayer = currRound.getCurrActPlayer();

        if (null != currActPlayer &&
            null != currActPlayer.getMoPai()) {
            //
            // 如果是摸牌之后产生的 "吃碰杠胡会话",
            // 那就等待出牌...
            return;
        }

        // 移到下一座位
        currRound.moveToNextActSeatIndex();

        // 执行摸牌逻辑
        MJ_weihai_BizLogic.getInstance().moPai(
            currRoom.getRoomId(),
            resultX
        );
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
