package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiPengGangHuSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.StateTable;
import org.mj.bizserver.mod.game.MJ_weihai_.hupattern.HuFormula;
import org.mj.bizserver.mod.game.MJ_weihai_.hupattern.HuPatternJudge;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongHuOrZiMo;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongLiangDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 胡牌
 */
interface MJ_weihai_BizLogic$hu {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$hu.class);

    /**
     * 胡牌
     *
     * @param userId  用户 Id
     * @param resultX 业务结果
     */
    default void hu(int userId, final BizResultWrapper<ReporterTeam> resultX) {
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
                "还没轮到你胡牌呢, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        if (!currDialog.isUserIdCanHu(userId) &&
            !currDialog.isUserIdCanZiMo(userId)) {
            LOGGER.error(
                "用户 Id 既不是可以胡牌的玩家也不是可以自摸的玩家, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 获取执行胡牌操作的玩家
        final Player execPlayer = currRound.getPlayerByUserId(userId);

        if (null == execPlayer) {
            LOGGER.error(
                "执行胡牌操作的玩家为空, userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 获取关键麻将牌
        final MahjongTileDef keyT = currDialog.isUserIdCanZiMo(execPlayer.getUserId())
            ? execPlayer.getMoPai()
            : sessionObj.getFromOtherzT();

        if (!HuFormula.test(
            execPlayer.getMahjongInHandCopy(),
            keyT)) {
            LOGGER.error(
                "胡牌测试失败, userId = {}, atRoomId = {}, roundIndex = {}, mahjongInHand = {}, keyT = {}",
                execPlayer.getUserId(),
                currRoom.getRoomId(),
                currRound.getRoundIndex(),
                execPlayer.getMahjongInHandIntValList(),
                (null == keyT) ? -1 : keyT.getIntVal()
            );
            return;
        }

        LOGGER.info(
            "玩家胡牌, userId = {}, atRoomId = {}, roundIndex = {}, mahjongInHand = {}, keyT = {}, ziMo = {}",
            execPlayer.getUserId(),
            currRoom.getRoomId(),
            currRound.getRoundIndex(),
            execPlayer.getMahjongInHandIntValList(),
            keyT.getIntVal(),
            currDialog.isUserIdCanZiMo(execPlayer.getUserId())
        );

        // 获取当前状态表
        final StateTable statTab1 = execPlayer.getCurrState();

        if (currDialog.isUserIdCanZiMo(execPlayer.getUserId())) {
            // 设置为自摸
            statTab1.setZiMo(true);
            statTab1.setMahjongZiMo(keyT);
        } else {
            // 设置为胡牌
            statTab1.setHu(true);
            statTab1.setMahjongHu(keyT);

            // 找到那个不幸的用户
            Player dianPaoPlayer = currRound.getPlayerByUserId(sessionObj.getFromUserId());

            if (null != dianPaoPlayer) {
                dianPaoPlayer.getCurrState().setDianPao(true);
            }
        }

        // 擦除用户 Id, 避免重复提示
        currDialog.eraseUserId(execPlayer.getUserId());

        // XXX 注意: 在这里进行胡牌模式判定,
        // 不过在这一步还不计算分数!
        // 因为要考虑发生一炮多响的情况,
        // 需要等待所有人都胡牌完成才计算分数...
        // 判定胡牌牌型
        HuPatternJudge.judge(currRound, execPlayer);

        ReporterTeam rptrTeam = resultX.getFinalResult();

        if (null == rptrTeam) {
            rptrTeam = new ReporterTeam(currRoom.getRoomId());
            resultX.setFinalResult(rptrTeam);
        }

        // 麻将亮倒
        rptrTeam.addPublicWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongLiangDao(
                execPlayer.getUserId(),
                execPlayer.getMahjongInHandCopy(),
                execPlayer.getMoPai()
            )
        ));

        // 麻将胡牌 or 自摸
        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongHuOrZiMo(
                userId,
                keyT,
                execPlayer.getCurrState().isHu(),
                execPlayer.getCurrState().isZiMo(),
                sessionObj.getFromUserId(),
                execPlayer.getSettlementResult().getHuPatternMapCopy()
            )
        )));

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

        // 当前牌局结束
        currRound.setChiPengGangHuSession(null);
        currRound.setEnded(true);

        // 统计总分和次数
        Scorer.countTotalScoreAndTimez(currRoom, currRound);
        // 当牌局结束
        MJ_weihai_BizLogic.getInstance().onCurrRoundEnded(
            currRoom, currRound
        );
    }
}
