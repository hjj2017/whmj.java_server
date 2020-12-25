package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_RedirectActUserId;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_RoundStarted;
import org.mj.bizserver.mod.game.MJ_weihai_.timertask.MJ_weihai_TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 开局
 */
interface MJ_weihai_BizLogic$beginNewRound {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$beginNewRound.class);

    /**
     * 开局
     *
     * @param roomId  房间 Id
     * @param resultX 业务结果
     */
    default void beginNewRound(final int roomId, final BizResultWrapper<ReporterTeam> resultX) {
        if (roomId <= 0 ||
            null == resultX) {
            return;
        }

        // 获取房间
        final Room currRoom = RoomGroup.getByRoomId(roomId);

        if (null == currRoom) {
            LOGGER.error(
                "房间为空, roomId = {}",
                roomId
            );
            return;
        }

        for (Player currPlayer : currRoom.getPlayerListCopy()) {
            if (null == currPlayer ||
                !currPlayer.getCurrState().isPrepare()) {
                LOGGER.error(
                    "有玩家未就坐或未准备好, atRoomId = {}",
                    roomId
                );
                return;
            }
        }

        // 获取当前牌局
        Round currRound = currRoom.getCurrRound();

        if (null != currRound &&
            currRound.isBegan()) {
            // 当前牌局已经开始,
            LOGGER.warn(
                "当前牌局已开始! atRoomId = {}, roundIndex = {}",
                roomId,
                currRound.getRoundIndex()
            );
            return;
        }

        // 如果所有人都已经准备好,
        // 则开局
        currRound = currRoom.beginNewRound();

        if (null == currRound) {
            LOGGER.error(
                "新建牌局为空, atRoomId = {}",
                roomId
            );
            return;
        }

        ReporterTeam rptrTeam = resultX.getFinalResult();

        if (null == rptrTeam) {
            rptrTeam = new ReporterTeam(currRoom.getRoomId());
            resultX.setFinalResult(rptrTeam);
        }

        // 执行发牌
        Dealer.execDeal(currRound, rptrTeam);

        // 已经取出的麻将牌数量清零
        currRound.resetTakeCardNum();
        // 设置为已开局
        currRound.setBegan(true);

        // 定庄
        int zhuangJiaUserId = DingZhuangStrategy.dingZhuang(
            currRoom, currRound
        );

        Player newZhuangJia = currRound.getPlayerByUserId(zhuangJiaUserId);

        if (null != newZhuangJia) {
            LOGGER.info(
                "玩家坐庄, userId = {}, atRoomId = {}, roundIndex = {}",
                zhuangJiaUserId,
                currRoom.getRoomId(),
                currRound.getRoundIndex()
            );
            newZhuangJia.getCurrState().setZhuangJia(true);
        }

        // 重定向活动座位索引
        currRound.redirectActSeatIndexByUserId(zhuangJiaUserId);

        // 添加词条
        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(
            new Wordz_RoundStarted(
                currRound.getRoundIndex(),
                zhuangJiaUserId // 庄家用户 Id
            )
        ));

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

        // 启动麻将定时任务
        MJ_weihai_TimerTask.getInstance().startUp();

        // 执行摸牌逻辑,
        // 令第一个活动玩家 ( 也就是庄家 ) 摸牌
        MJ_weihai_BizLogic.getInstance().moPai(currRoom.getRoomId(), resultX);
    }
}
