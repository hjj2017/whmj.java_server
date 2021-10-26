package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiPengGangHuSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongChi;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongInHandChanged;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_RedirectActUserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 吃
 */
interface MJ_weihai_BizLogic$chi {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$chi.class);

    /**
     * 吃, XXX 注意: 选择选项 selectedOption = 0 | 1 | 2
     * 我们以吃 "三万" 这张牌为例:
     * 0 --> 代表选择选项 A --> 也就是这样吃牌 1 2 [3]
     * 1 --> 代表选择选项 B --> 也就是这样吃牌 2 [3] 4
     * 2 --> 代表选择选项 C --> 也就是这样吃牌 [3] 4 5
     *
     * @param userId         用户 Id
     * @param selectedOption 选择选项
     */
    default void chi(
        int userId, int selectedOption, BizResultWrapper<ReporterTeam> resultX) {
        if (userId <= 0 ||
            selectedOption < 0 ||
            selectedOption > 2 ||
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
            // XXX 注意:
            // 吃牌时一定是有一张牌是来自他人的!
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
                "还没轮到你吃牌呢, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        if (!currDialog.isUserIdCanChi(userId)) {
            LOGGER.error(
                "用户 Id 不是可以吃牌的玩家, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 清除当前牌局记录的吃碰杠胡会议
        currRound.setChiPengGangHuSession(null);

        // 获取吃牌来自玩家
        final Player fromPlayer = currRound.getPlayerByUserId(sessionObj.getFromUserId());

        if (null == fromPlayer ||
            null == fromPlayer.getMahjongOutput()) {
            LOGGER.error(
                "吃牌来自玩家或者麻将打出牌列表为空, fromPlayerUserId = {}, atRoomId = {}, roundIndex = {}",
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
                "执行吃牌操作的玩家为空, execPlayerUserId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 要吃的麻将牌
        final MahjongTileDef chiT = sessionObj.getFromOtherzT();

        if (chiT != fromPlayer.getMahjongOutput().peekLast()) {
            LOGGER.error(
                "吃牌与打出的最后一张牌不相同, userId = {}, atRoomId = {}, roundIndex = {}, chiT = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex(),
                chiT.getIntVal()
            );
            return;
        }

        MahjongTileDef t0, t1, t2;

        switch (selectedOption) {
            case 0:
                t0 = MahjongTileDef.valueOf(chiT.getIntVal() - 2);
                t1 = MahjongTileDef.valueOf(chiT.getIntVal() - 1);
                t2 = chiT;
                break;

            case 1:
                t0 = MahjongTileDef.valueOf(chiT.getIntVal() - 1);
                t1 = chiT;
                t2 = MahjongTileDef.valueOf(chiT.getIntVal() + 1);
                break;

            case 2:
                t0 = chiT;
                t1 = MahjongTileDef.valueOf(chiT.getIntVal() + 1);
                t2 = MahjongTileDef.valueOf(chiT.getIntVal() + 2);
                break;

            default:
                t0 = t1 = t2 = null;
                break;
        }

        if (null == t0 ||
            null == t1 ||
            null == t2) {
            LOGGER.error(
                "吃牌失败, 计算另外两张牌得到空值! userId = {}, atRoomId = {}, roundIndex = {}, chiT = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex(),
                chiT.getIntVal()
            );
            return;
        }

        // 二次检查是否可以吃牌
        if (!checkCanChi(execPlayer, t0, t1, t2, chiT)) {
            LOGGER.error("二次检查吃牌失败, userId = {}, atRoomId = {}, roundIndex = {}, t0 = {}, t1 = {}, t2 = {}, chiT = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex(),
                t0.getIntVal(),
                t1.getIntVal(),
                t2.getIntVal(),
                chiT.getIntVal()
            );
            return;
        }

        LOGGER.info(
            "玩家吃牌, userId = {}, atRoomId = {}, roundIndex = {}, t0 = {}, t1 = {}, t2 = {}, chiT = {}",
            execPlayer.getUserId(),
            currRound.getRoomId(),
            currRound.getRoundIndex(),
            t0.getIntVal(),
            t1.getIntVal(),
            t2.getIntVal(),
            chiT.getIntVal()
        );

        MahjongTileDef[] tempArray = {
            t0, t1, t2,
        };

        for (MahjongTileDef currT : tempArray) {
            if (currT != chiT) {
                execPlayer.removeAMahjongTileInHand(currT);
            }
        }

        // 添加麻将吃牌
        execPlayer.addMahjongChi(
            chiT, t0, t1, t2, fromPlayer.getUserId()
        );

        // 因为吃牌之后手里多了一张牌,
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
            new Wordz_MahjongChi(
                execPlayer.getUserId(),
                chiT, // 吃的是哪一张牌
                t0, t1, t2,
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
     * 检查是否可以吃牌
     *
     * @param execPlayer 执行吃牌的玩家
     * @param t0         第一张牌
     * @param t1         第二张牌
     * @param t2         第三张牌
     * @param tChi       要吃的麻将牌
     * @return true = 可以吃牌, false = 不能吃牌
     */
    static private boolean checkCanChi(
        Player execPlayer, MahjongTileDef t0, MahjongTileDef t1, MahjongTileDef t2, MahjongTileDef tChi) {
        if (null == execPlayer ||
            null == t0 ||
            null == t1 ||
            null == t2 ||
            null == tChi) {
            return false;
        }

        if (tChi != t0 &&
            tChi != t1 &&
            tChi != t2) {
            return false;
        }

        if (t0 != tChi &&
            !execPlayer.hasAMahjongTileInHand(t0)) {
            // 如果第一张牌不是要吃的牌,
            // 但玩家手里却没有这张牌
            return false;
        } else if (t1 != tChi &&
            !execPlayer.hasAMahjongTileInHand(t1)) {
            // 如果第二张牌不是要吃的牌,
            // 但玩家手里却没有这张牌
            return false;
        } else if (t2 != tChi &&
            !execPlayer.hasAMahjongTileInHand(t2)) {
            // 如果第三张牌不是要吃的牌,
            // 但玩家手里却没有这张牌
            return false;
        } else {
            return true;
        }
    }
}
