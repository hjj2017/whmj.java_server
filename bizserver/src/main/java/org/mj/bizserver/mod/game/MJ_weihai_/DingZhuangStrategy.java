package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RuleSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 定庄策略
 */
class DingZhuangStrategy {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(DingZhuangStrategy.class);

    /**
     * 类默认构造器
     */
    private DingZhuangStrategy() {
    }

    /**
     * 定庄, 并获得用户 Id
     *
     * @param currRoom  当前房间
     * @param currRound 当前牌局
     * @return 庄家用户 Id
     */
    static int dingZhuang(Room currRoom, Round currRound) {
        if (null == currRoom ||
            null == currRound) {
            return -1;
        }

        if (0 == currRound.getRoundIndex()) {
            // 获取房主玩家
            Player roomOwner = currRound.getPlayerByUserId(currRoom.getOwnerId());

            if (null == roomOwner) {
                LOGGER.error(
                    "房主用户竟然为空, roomId = {}",
                    currRoom.getRoomId()
                );
                return -1;
            }

            return roomOwner.getUserId();
        }

        // 获取房间规则
        final RuleSetting ruleSetting = currRoom.getRuleSetting();

        if (ruleSetting.getMaxRound() > 0) {
            // 定庄 ( 最大局数版本 )
            return dingZhuang_verMaxRound(currRoom, currRound);
        } else if (ruleSetting.getMaxCircle() > 0) {
            // 定庄 ( 最大圈数版本 )
            return dingZhuang_verMaxCircle(currRoom, currRound);
        }

        return -1;
    }

    /**
     * 定庄, 获得玩家 Id ( 最大局数版本 )
     *
     * @param currRoom  当前房间
     * @param currRound 当前牌局
     * @return 庄家用户 Id
     */
    static private int dingZhuang_verMaxRound(Room currRoom, Round currRound) {
        if (null == currRoom ||
            null == currRound) {
            return -1;
        }

        // 获取上一局
        Round prevRound = currRoom.getRoundByIndex(currRound.getRoundIndex() - 1);

        if (null == prevRound) {
            return -1;
        }

        // 获取玩家列表
        final List<Player> playerList = prevRound.getPlayerListCopy();
        int zhuangJiaUserId = -1;
        List<Integer> huOrZiMoUserIdList = null;

        for (Player currPlayer : playerList) {
            if (null == currPlayer) {
                continue;
            }

            if (currPlayer.getCurrState().isZhuangJia()) {
                // 如果当前玩家是庄家
                // 获取座位索引
                zhuangJiaUserId = currPlayer.getUserId();
            }

            if (currPlayer.getCurrState().isZiMo() ||
                currPlayer.getCurrState().isHu()) {
                if (null == huOrZiMoUserIdList) {
                    huOrZiMoUserIdList = new ArrayList<>();
                }

                huOrZiMoUserIdList.add(
                    currPlayer.getUserId()
                );
            }
        }

        if (null == huOrZiMoUserIdList ||
            huOrZiMoUserIdList.contains(zhuangJiaUserId)) {
            // 如果上一局没人胡牌
            // 或者如果上一局中庄家有胡牌,
            // 那么这一局继续坐庄
            return zhuangJiaUserId;
        } else {
            // 如果上一局有人胡牌,
            // 但是上一局中庄家没胡牌,
            // 那么就让第一个胡牌的人来坐庄
            return huOrZiMoUserIdList.get(0);
        }
    }

    /**
     * 定庄, 并获得用户 Id ( 最大圈数版本 )
     *
     * @param currRoom  当前房间
     * @param currRound 当前牌局
     * @return 庄家用户 Id
     */
    static private int dingZhuang_verMaxCircle(Room currRoom, Round currRound) {
        if (null == currRoom ||
            null == currRound) {
            return -1;
        }

        // 获取上一局
        Round prevRound = currRoom.getRoundByIndex(currRound.getRoundIndex() - 1);

        if (null == prevRound) {
            return -1;
        }

        // 获取玩家列表
        final List<Player> playerList = prevRound.getPlayerListCopy();
        int zhuangJiaSeatIndex = -1;

        for (Player currPlayer : playerList) {
            if (null == currPlayer) {
                continue;
            }

            if (currPlayer.getCurrState().isZhuangJia()) {
                // 如果当前玩家是庄家
                // 获取座位索引
                zhuangJiaSeatIndex = currPlayer.getSeatIndex();

                if (currPlayer.getCurrState().isZiMo() ||
                    currPlayer.getCurrState().isHu()) {
                    // 如果庄家胡牌了,
                    // 那么这一局玩家继续坐庄
                    return currPlayer.getUserId();
                }
            }
        }

        if (-1 == zhuangJiaSeatIndex) {
            LOGGER.error(
                "庄家座位索引为空, atRoomId = {}, prevRoundIndex = {}",
                currRoom.getRoomId(),
                prevRound.getRoundIndex()
            );
            return -1;
        }

        // 如果庄家没有胡牌 ( XXX 注意: 包括荒庄 ),
        // 那么庄家的下家坐庄
        zhuangJiaSeatIndex = ++zhuangJiaSeatIndex % playerList.size();

        // 获取新的庄家
        Player newZhuangJia = currRound.getPlayerBySeatIndex(
            zhuangJiaSeatIndex
        );

        if (null == newZhuangJia) {
            LOGGER.error(
                "庄家为空, atRoomId = {}, roundIndex = {}, seatIndex = {}",
                currRoom.getRoomId(),
                currRound.getRoundIndex(),
                zhuangJiaSeatIndex
            );
            return -1;
        } else {
            return newZhuangJia.getUserId();
        }
    }
}
