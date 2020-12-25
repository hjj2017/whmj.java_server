package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiPengGangHuSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongInHandChanged;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongPeng;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_RedirectActUserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 碰
 */
interface MJ_weihai_BizLogic$peng {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$peng.class);

    /**
     * 碰
     *
     * @param userId  用户 Id
     * @param resultX 业务结果
     */
    default void peng(int userId, BizResultWrapper<ReporterTeam> resultX) {
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

        if (null == sessionObj.getFromOtherzT()) {
            LOGGER.error(
                "\"吃碰杠胡会议\" 所携带的麻将牌为空, userId = {}, atRoomId = {}, roundIndex = {}",
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
                "还没轮到你碰牌呢, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        if (!currDialog.isUserIdCanPeng(userId)) {
            LOGGER.error(
                "用户 Id 不是可以碰牌的玩家, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 清除当前牌局记录的吃碰杠胡会议
        currDialog.eraseUserId(userId);
        currRound.setChiPengGangHuSession(null);

        // 获取碰牌来自玩家
        final Player fromPlayer = currRound.getPlayerByUserId(sessionObj.getFromUserId());

        if (null == fromPlayer) {
            LOGGER.error(
                "碰牌来自玩家为空, fromUserId = {}, atRoomId = {}, roundIndex = {}",
                sessionObj.getFromUserId(),
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 获取执行操作的玩家
        final Player execPlayer = currRound.getPlayerByUserId(userId);

        if (null == execPlayer) {
            LOGGER.error(
                "碰牌执行玩家为空, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 要碰的麻将牌
        final MahjongTileDef tPeng = sessionObj.getFromOtherzT();

        if (tPeng != fromPlayer.getMahjongOutput().peekLast()) {
            LOGGER.error(
                "碰牌与打出的最后一张牌不相同, userId = {}, atRoomId = {}, roundIndex = {}, t = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex(),
                tPeng.getIntVal()
            );
            return;
        }

        // 二次检查是否可以碰牌
        if (!checkCanPeng(execPlayer, tPeng)) {
            LOGGER.error("检查碰牌失败, userId = {}, atRoomId = {}, roundIndex = {}, t = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex(),
                tPeng.getIntVal()
            );
            return;
        }

        LOGGER.info(
            "玩家碰牌, userId = {}, atRoomId = {}, roundIndex = {}, t = {}",
            execPlayer.getUserId(),
            currRound.getRoomId(),
            currRound.getRoundIndex(),
            tPeng.getIntVal()
        );

        // 移除执行玩家手里的两张牌
        execPlayer.removeAMahjongTileInHand(tPeng);
        execPlayer.removeAMahjongTileInHand(tPeng);
        // 添加麻将碰牌
        execPlayer.addMahjongPeng(
            tPeng, fromPlayer.getUserId()
        );

        // 因为碰牌之后手里多了一张牌,
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

        // 记录碰牌
        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongPeng(
                execPlayer.getUserId(),
                tPeng, // 碰的是哪一张牌
                fromPlayer.getUserId()
            )
        )));

        // 扣除来自玩家打出的牌
        fromPlayer.getMahjongOutput().removeLast();

        // 修改指针方向
        currRound.redirectActSeatIndexByUserId(execPlayer.getUserId());

        if (null != currRound.getCurrActPlayer()) {
            rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(
                new Wordz_RedirectActUserId(
                    currRound.getCurrActPlayer().getUserId(),
                    currRound.getCurrActPlayer().getSeatIndex(),
                    currRound.getRoundIndex(),
                    currRound.getRemainCardNum(),
                    -1
                )
            ));
        }
    }

    /**
     * 检查是否可以碰牌
     *
     * @param execPlayer 执行碰牌的玩家
     * @param tPeng      要碰的是哪张牌
     * @return true = 可以碰牌, false = 不能碰牌
     */
    private boolean checkCanPeng(Player execPlayer, MahjongTileDef tPeng) {
        if (null == execPlayer ||
            null == tPeng) {
            return false;
        }

        int count = 0;

        for (MahjongTileDef currT : execPlayer.getMahjongInHandCopy()) {
            if (currT == tPeng) {
                ++count;
            }
        }

        return count >= 2;
    }
}
