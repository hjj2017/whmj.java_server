package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RuleSetting;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_Prepare;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_SelectPiaoHint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 准备
 */
public interface MJ_weihai_BizLogic$prepare {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$prepare.class);

    /**
     * 准备
     *
     * @param userId  用户 Id
     * @param yes     是否准备好, 0 = 取消准备, 1 = 准备好
     * @param resultX 业务结果
     */
    default void prepare(int userId, int yes, BizResultWrapper<ReporterTeam> resultX) {
        if (null == resultX) {
            return;
        }

        // 获取房间
        Room currRoom = RoomGroup.getByUserId(userId);

        if (null == currRoom) {
            LOGGER.error("房间为空, userId = {}", userId);
            return;
        }

        // 获取当前牌局
        Round currRound = currRoom.getCurrRound();

        if (null != currRound &&
            currRound.isBegan()) {
            // 当前牌局已经开始,
            LOGGER.warn(
                "当前牌局已开始, 不能重复准备! userId = {}, atRoomId = {}, roundIndex = {}",
                userId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        // 获取执行玩家
        Player execPlayer = currRoom.getPlayerByUserId(userId);

        if (null == execPlayer) {
            LOGGER.error(
                "玩家为空, userId = {}, atRoomId = {}",
                userId,
                currRoom.getRoomId()
            );
            return;
        }

        if (execPlayer.getCurrState().isPrepare()) {
            LOGGER.error(
                "玩家已经准备好, 不能重复准备! userId = {}, atRoomId = {}",
                userId,
                currRoom.getRoomId()
            );
            return;
        }

        ReporterTeam rptrTeam = resultX.getFinalResult();

        if (null == rptrTeam) {
            rptrTeam = new ReporterTeam(currRoom.getRoomId());
            resultX.setFinalResult(rptrTeam);
        }

        execPlayer.getCurrState().setPrepare(1 == yes);

        // 准备词条
        final Wordz_Prepare wPrepare = new Wordz_Prepare(
            execPlayer.getUserId(), yes
        );

        // 添加词条
        rptrTeam.addPublicWordz(rptrTeam.addPrivateWordz(
            wPrepare
        ));

        // 获取最大用户数量
        final RuleSetting ruleSetting = currRoom.getRuleSetting();
        final int maxPlayer = ruleSetting.getMaxPlayer();

        for (int seatIndex = 0; seatIndex < maxPlayer; seatIndex++) {
            // 根据座位索引获取当前玩家
            execPlayer = currRoom.getPlayerBySeatIndex(seatIndex);

            if (null == execPlayer ||
                !execPlayer.getCurrState().isPrepare()) {
                // 如果有任何人还没准备好,
                // 就不继续向下执行了...
                return;
            }
        }

        wPrepare.setAllReady(true);

        // 游戏正式开始
        currRoom.setOfficialStarted(true);

        if (ruleSetting.isPiaoFen() &&
            !currRoom.isDingPiaoEnded()) {
            // 如果带飘分,
            // 那么先提示玩家选飘
            LOGGER.info(
                "给玩家发送选飘提示, atRoomId = {}",
                currRoom.getRoomId()
            );

            // 可以选择: 不飘、飘_1、飘_2、飘_3, 不能选择: 飘_4
            rptrTeam.addPublicWordz(new Wordz_SelectPiaoHint(true, true, true, true, false));
            return;
        }

        // 开局
        MJ_weihai_BizLogic.getInstance().beginNewRound(
            currRoom.getRoomId(),
            resultX
        );
    }
}
