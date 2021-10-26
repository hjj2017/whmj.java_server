package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_DingPiao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定飘
 */
interface MJ_weihai_BizLogic$dingPiao {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$dingPiao.class);

    /**
     * 定飘
     *
     * @param userId  用户 Id
     * @param piaoX   漂几, 0 = 不飘, 1 = 飘_1, 2 = 飘_2, 3 = 飘_3, 4 = 飘_4
     * @param resultX 业务结果
     */
    default void dingPiao(final int userId, final int piaoX, BizResultWrapper<ReporterTeam> resultX) {
        if (userId <= 0 ||
            null == resultX) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
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

        if (currRoom.isDingPiaoEnded()) {
            LOGGER.error(
                "定飘已经结束, 不能重新定飘! userId = {}, atRoomId = {}",
                userId,
                currRoom.getRoomId()
            );
            return;
        }

        if (piaoX < 0 ||
            piaoX > 3) {
            LOGGER.error(
                "飘分选择错误, userId = {}, atRoomId = {}, piaoX = {}",
                userId,
                currRoom.getRoomId(),
                piaoX
            );
            return;
        }

        // 获取房间玩家并设置漂分
        final Player currPlayer = currRoom.getPlayerByUserId(userId);

        if (null == currPlayer) {
            LOGGER.error(
                "未找到玩家, userId = {}, atRoomId = {}",
                userId,
                currRoom.getRoomId()
            );
            return;
        }

        if (currPlayer.getCurrState().getPiaoX() >= 0) {
            LOGGER.error(
                "玩家不能重复定飘, userId = {}, atRoomId = {}",
                userId,
                currRoom.getRoomId()
            );
            return;
        }

        // 设置飘几
        currPlayer.getCurrState().setPiaoX(piaoX);

        LOGGER.info(
            "玩家定飘, userId = {}, atRoomId = {}, piaoX = {}",
            userId,
            currRoom.getRoomId(),
            piaoX
        );

        ReporterTeam rptrTeam = resultX.getFinalResult();

        if (null == rptrTeam) {
            rptrTeam = new ReporterTeam(currRoom.getRoomId());
            resultX.setFinalResult(rptrTeam);
        }

        // 定飘
        rptrTeam.addPrivateWordz(rptrTeam.addPublicWordz(
            new Wordz_DingPiao(userId, piaoX)
        ));

        if (isDingPiaoEnded(currRoom)) {
            LOGGER.info(
                "定飘完成! 准备开局, atRoomId = {}",
                currRoom.getRoomId()
            );

            // 定飘结束
            currRoom.setDingPiaoEnded(true);

            // 开局
            MJ_weihai_BizLogic.getInstance().beginNewRound(
                currRoom.getRoomId(), resultX
            );
        }
    }

    /**
     * 定飘是否结束
     *
     * @param currRoom 当前房间
     * @return true = 定飘已结束, false = 定飘未结束
     */
    static private boolean isDingPiaoEnded(Room currRoom) {
        if (null == currRoom) {
            return false;
        }

        for (Player currPlayer : currRoom.getPlayerListCopy()) {
            if (null == currPlayer ||
                currPlayer.getCurrState().getPiaoX() < 0) {
                return false;
            }
        }

        return true;
    }
}
