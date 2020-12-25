package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiPengGangHuSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongLiangFeng;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongBuFeng;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongInHandChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 补风
 */
interface MJ_weihai_BizLogic$buFeng {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$buFeng.class);

    /**
     * 补风
     *
     * @param userId  用户 Id
     * @param resultX 业务结果
     */
    default void buFeng(int userId, BizResultWrapper<ReporterTeam> resultX) {
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
                "还没轮到你补风呢, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        if (!currDialog.isUserIdCanBuFeng(userId)) {
            LOGGER.error(
                "用户 Id 不是可以补风的玩家, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 清除当前牌局记录的吃碰杠胡会议
        currDialog.eraseUserId(userId);
        currRound.setChiPengGangHuSession(null);

        // 获取执行操作的玩家
        final Player execPlayer = currRound.getPlayerByUserId(userId);

        if (null == execPlayer) {
            LOGGER.error(
                "执行玩家为空, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 获取补风麻将牌
        final MahjongTileDef tBuFeng = MJ_weihai_BizLogic.getInstance().findMahjongCanBuFeng(execPlayer);

        if (null == tBuFeng) {
            LOGGER.error(
                "玩家可以补风的牌为空!, userId = {}, atRoomId = {}, roundIndex = {}",
                execPlayer.getUserId(),
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 获取麻将亮风
        final MahjongLiangFeng liangFeng = execPlayer.getMahjongLiangFeng();

        LOGGER.info(
            "玩家补风, userId = {}, atRoomId = {}, roundIndex = {}, tBuFeng = {}",
            execPlayer.getUserId(),
            currRoom.getRoomId(),
            currRound.getRoundIndex(),
            tBuFeng.getIntVal()
        );

        if (tBuFeng != execPlayer.getMoPai()) {
            // 如果补风牌在手里,
            // 就移除掉
            // 并且将刚摸到的牌添加到手中
            execPlayer.removeAMahjongTileInHand(tBuFeng);
            execPlayer.addAMahjongTileInHand(execPlayer.getMoPai());
        }

        execPlayer.setMoPai(null);
        liangFeng.doBuFeng(tBuFeng);

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
        
        // 记录麻将补风
        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongBuFeng(
                execPlayer.getUserId(),
                execPlayer.getMahjongLiangFeng().getKind(),
                execPlayer.getMahjongLiangFeng().getCounterMapCopy()
            )
        )));

        // 补风后摸一张牌
        MJ_weihai_BizLogic.getInstance().moPai(
            currRoom.getRoomId(),
            resultX
        );
    }
}
