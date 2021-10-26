package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiPengGangHuSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongAnGang;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongBuGang;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongInHandChanged;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongMingGang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 杠
 */
interface MJ_weihai_BizLogic$gang {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$gang.class);

    /**
     * 杠
     *
     * @param userId  用户 Id
     * @param resultX 业务结果
     */
    default void gang(final int userId, BizResultWrapper<ReporterTeam> resultX) {
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
                "吃碰杠胡会话为空, userId = {}, atRoomId = {}, roundIndex = {}",
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
                "还没轮到你杠牌呢, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        if (!currDialog.isUserIdCanMingGang(userId) &&
            !currDialog.isUserIdCanAnGang(userId) &&
            !currDialog.isUserIdCanBuGang(userId)) {
            LOGGER.error(
                "用户 Id 不是可以杠牌的玩家, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 清除当前牌局记录的吃碰杠胡会议
        currRound.setChiPengGangHuSession(null);

        // 获取执行玩家
        final Player execPlayer = currRound.getPlayerByUserId(userId);

        if (null == execPlayer) {
            LOGGER.error(
                "执行杠牌操作的玩家为空, userId = {}, atRoomId = {}, roundIndex = {}",
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

        if (currDialog.isUserIdCanMingGang(userId)) {
            // 获取出牌玩家
            Player chuPaiPlayer = currRound.getPlayerByUserId(sessionObj.getFromUserId());
            // 执行明杠
            doMingGang(
                currRound, execPlayer, chuPaiPlayer, sessionObj.getFromOtherzT(), resultX
            );
        } else if (currDialog.isUserIdCanBuGang(userId)) {
            // 如果即可以暗杠又可以补杠,
            // 优先执行补杠!
            // 因为暗杠是任何时候都会被检测和执行的...
            doBuGang(
                currRound, execPlayer, resultX
            );
        } else {
            // 执行暗杠
            doAnGang(
                currRound, execPlayer, resultX
            );
        }
    }

    /**
     * 执行明杠
     *
     * @param currRound  当前牌局
     * @param execPlayer 执行玩家
     * @param fromPlayer 杠牌来自玩家
     * @param mingGangT  杠牌
     * @param resultX    业务结果
     */
    private void doMingGang(
        Round currRound, Player execPlayer, Player fromPlayer, MahjongTileDef mingGangT, BizResultWrapper<ReporterTeam> resultX) {
        if (null == currRound ||
            null == execPlayer ||
            null == fromPlayer ||
            null == mingGangT ||
            null == resultX ||
            null == resultX.getFinalResult()) {
            return;
        }

        int counter = 0;

        for (MahjongTileDef tCurr : execPlayer.getMahjongInHandCopy()) {
            if (tCurr == mingGangT) {
                ++counter;
            }
        }

        if (counter < 3) {
            LOGGER.error(
                "玩家手里的牌数量 < 3 张, 没法明杠! userId = {}, atRoomId = {}, roundIndex = {}, mingGangT = {}",
                execPlayer.getUserId(),
                currRound.getRoomId(),
                currRound.getRoundIndex(),
                mingGangT.getIntVal()
            );
            return;
        }

        if (mingGangT != fromPlayer.getMahjongOutput().peekLast()) {
            LOGGER.error(
                "杠牌与打出的最后一张牌不相同, userId = {}, atRoomId = {}, roundIndex = {}, mingGangT = {}",
                execPlayer.getUserId(),
                currRound.getRoomId(),
                currRound.getRoundIndex(),
                mingGangT.getIntVal()
            );
            return;
        }

        LOGGER.info(
            "玩家明杠, userId = {}, atRoomId = {}, roundIndex = {}, mingGangT = {}",
            execPlayer.getUserId(),
            currRound.getRoomId(),
            currRound.getRoundIndex(),
            mingGangT.getIntVal()
        );

        // 移除手牌并添加明杠
        execPlayer.removeAMahjongTileInHand(mingGangT);
        execPlayer.removeAMahjongTileInHand(mingGangT);
        execPlayer.removeAMahjongTileInHand(mingGangT);
        execPlayer.addMahjongMingGang(
            mingGangT, fromPlayer.getUserId()
        );

        // 增加刚好有杠的数量
        execPlayer.getCurrState().increaseJustGangNum();

        // 获取记者小队
        ReporterTeam rptrTeam = resultX.getFinalResult();
        // 记录手牌变化
        rptrTeam.addPublicWordz(rptrTeam.addPrivateWordz(rptrTeam.addPlaybackWordz(
            new Wordz_MahjongInHandChanged(
                execPlayer.getUserId(),
                execPlayer.getMahjongInHandCopy(),
                execPlayer.getMoPai()
            )
        )).createMaskCopy());

        // 记录杠牌
        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongMingGang(
                execPlayer.getUserId(),
                mingGangT, // 杠牌
                fromPlayer.getUserId()
            )
        )));

        // 移除最后打出的牌
        fromPlayer.getMahjongOutput().removeLast();

        // 重定向活动座位索引
        currRound.redirectActSeatIndexByUserId(execPlayer.getUserId());

        // 执行摸牌
        MJ_weihai_BizLogic.getInstance().moPai(
            currRound.getRoomId(),
            resultX
        );
    }

    /**
     * 执行补杠
     *
     * @param currRound  当前牌局
     * @param execPlayer 执行玩家
     * @param resultX    业务结果
     */
    private void doBuGang(
        Round currRound, Player execPlayer, BizResultWrapper<ReporterTeam> resultX) {
        if (null == currRound ||
            null == execPlayer ||
            null == resultX ||
            null == resultX.getFinalResult()) {
            return;
        }

        // 获取摸到的麻将牌作为补杠牌
        final MahjongTileDef buGangT = execPlayer.getMoPai();

        if (null == buGangT) {
            LOGGER.error(
                "玩家摸牌为空, 请问拿什么补杠? userId = {}, atRoomId = {}, roundIndex = {}",
                execPlayer.getUserId(),
                currRound.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        if (!execPlayer.canBuGang(buGangT)) {
            LOGGER.error(
                "没有找到可以补杠的碰牌! userId = {}, atRoomId = {}, roundIndex = {}, buGangT = {}",
                execPlayer.getUserId(),
                currRound.getRoomId(),
                currRound.getRoundIndex(),
                buGangT.getIntVal()
            );
            return;
        }

        LOGGER.info(
            "玩家补杠, userId = {}, atRoomId = {}, roundIndex = {}, buGangT = {}",
            execPlayer.getUserId(),
            currRound.getRoomId(),
            currRound.getRoundIndex(),
            buGangT.getIntVal()
        );

        // 清除摸到的牌并将碰牌升级到补杠
        execPlayer.setMoPai(null);
        execPlayer.upgradePengToBuGang(buGangT);
        // 增加刚好有杠的数量
        execPlayer.getCurrState().increaseJustGangNum();

        // 获取记者小队
        ReporterTeam rptrTeam = resultX.getFinalResult();
        // 记录杠牌
        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongBuGang(
                execPlayer.getUserId(),
                buGangT // 杠牌
            )
        )));

        // 执行摸牌
        MJ_weihai_BizLogic.getInstance().moPai(
            currRound.getRoomId(),
            resultX
        );
    }

    /**
     * 执行暗杠
     *
     * @param execPlayer 执行玩家
     * @param resultX    业务结果
     */
    private void doAnGang(
        Round currRound, Player execPlayer, BizResultWrapper<ReporterTeam> resultX) {
        if (null == currRound ||
            null == execPlayer ||
            null == resultX ||
            null == resultX.getFinalResult()) {
            return;
        }

        // 查找可以暗杠的麻将牌
        final MahjongTileDef anGangT = MJ_weihai_BizLogic.getInstance().findMahjongCanAnGang(execPlayer);

        if (null == anGangT) {
            LOGGER.info(
                "玩家没有可以暗杠的麻将牌, userId = {}, atRoomId = {}, roundIndex = {}",
                execPlayer.getUserId(),
                currRound.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        LOGGER.info(
            "玩家暗杠, userId = {}, atRoomId = {}, roundIndex = {}, anGangT = {}",
            execPlayer.getUserId(),
            currRound.getRoomId(),
            currRound.getRoundIndex(),
            anGangT.getIntVal()
        );

        execPlayer.removeAMahjongTileInHand(anGangT);
        execPlayer.removeAMahjongTileInHand(anGangT);
        execPlayer.removeAMahjongTileInHand(anGangT);
        execPlayer.removeAMahjongTileInHand(anGangT);

        if (execPlayer.getMoPai() != anGangT) {
            // 如果刚摸到的牌不是暗杠牌,
            // 那么需要将刚摸到的牌加入到手牌列表中...
            execPlayer.addAMahjongTileInHand(execPlayer.getMoPai());
        }

        execPlayer.setMoPai(null);

        // 添加暗杠
        execPlayer.addMahjongAnGang(anGangT);
        // 增加刚好有杠的数量
        execPlayer.getCurrState().increaseJustGangNum();

        // 获取记者小队
        ReporterTeam rptrTeam = resultX.getFinalResult();
        // 记录手牌变化
        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongInHandChanged(
                execPlayer.getUserId(),
                execPlayer.getMahjongInHandCopy(),
                execPlayer.getMoPai()
            )
        )).createMaskCopy());

        // 记录杠牌
        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongAnGang(
                execPlayer.getUserId(),
                anGangT // 杠牌
            )
        )));

        // 执行摸牌
        MJ_weihai_BizLogic.getInstance().moPai(
            currRound.getRoomId(),
            resultX
        );
    }
}
