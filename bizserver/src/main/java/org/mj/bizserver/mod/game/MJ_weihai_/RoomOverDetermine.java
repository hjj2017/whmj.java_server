package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RuleSetting;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 房间结束判定,
 * <p>
 * XXX 注意: 房间结束属于业务逻辑!
 * 在威海麻将中除了一般意义上的 "局" 的概念, 还有个 "圈" 的概念...
 * 圈, 指的是每个人必须上庄一次算 1 圈.
 * 如果庄家胡牌 ( 包括自摸和胡牌 ), 那么下一局该玩家继续坐庄.
 * 如果庄家没有胡牌, 那么庄家的下家坐庄.
 * 重复以上逻辑直至牌局内的所有人都至少坐庄一次,
 * 才算 1 圈
 * <p>
 * XXX 注意: 在最后一个玩家坐庄时, 如果该玩家胡牌了, 那么此时 1 圈还不能被判定为结束!
 * 必须是最后一个玩家坐庄, 并且没有胡牌, 1 圈才算结束...
 */
public class RoomOverDetermine {
    /**
     * 私有化类默认构造器
     */
    private RoomOverDetermine() {
    }

    /**
     * 判定房间是否已经结束
     *
     * @param currRoom 当前房间
     * @return true = 已经结束, false = 尚未结束
     */
    static public boolean determine(Room currRoom) {
        if (null == currRoom) {
            return true;
        }

        // 获取规则设置
        final RuleSetting ruleSetting = currRoom.getRuleSetting();

        if (null == ruleSetting) {
            return true;
        }

        if (ruleSetting.getMaxRound() > 0) {
            // 如果是按照最大局数算的
            if (currRoom.getEndedRoundCount() >= ruleSetting.getMaxRound()) {
                currRoom.setEndOfAllRound(Room.EndOfAllRoundEnum.YES);
                return true;
            } else {
                currRoom.setEndOfAllRound(Room.EndOfAllRoundEnum.NO);
                return false;
            }
        }

        // 否则按照最大圈数计算
        return determine_byMaxCircle(
            currRoom, ruleSetting.getMaxCircle()
        );
    }

    /**
     * 根据最大圈数判定牌局是否结束
     *
     * @param currRoom  当前房间
     * @param maxCircle 最大圈数
     * @return true = 已结束, false = 未结束
     */
    static private boolean determine_byMaxCircle(
        Room currRoom, int maxCircle) {
        if (null == currRoom ||
            Room.EndOfAllRoundEnum.YES == currRoom.getEndOfAllRound() ||
            maxCircle <= 0) {
            return true;
        }

        if (Room.EndOfAllRoundEnum.NO == currRoom.getEndOfAllRound()) {
            // 如果已经明确没有结束
            return false;
        }

        // 获取已经结束的牌局数量
        int roundCount = currRoom.getEndedRoundCount();

        Set<Integer> zhuangJiaSet = new HashSet<>();
        int circleCount = 0;

        for (int i = 0; i < roundCount; i++) {
            // 获取当前牌局
            final Round currRound = currRoom.getRoundByIndex(i);

            if (null == currRound) {
                continue;
            }

            List<Player> playerList = currRound.getPlayerListCopy();

            for (Player currPlayer : playerList) {
                if (null != currPlayer &&
                    currPlayer.getCurrState().isZhuangJia()) {
                    // 添加用户 Id
                    zhuangJiaSet.add(currPlayer.getUserId());
                    break;
                }
            }

            if (zhuangJiaSet.size() < playerList.size()) {
                // 不是所有玩家都坐过庄,
                continue;
            }

            // 获取最后一个座位上的玩家
            Player lastPlayer = currRound.getPlayerBySeatIndex(playerList.size() - 1);

            if (null != lastPlayer) {
                if (lastPlayer.getCurrState().isHu() ||
                    lastPlayer.getCurrState().isZiMo()) {
                    // 如果最后一个玩家这一局还是胡牌了,
                    // 那么 1 圈不算结束...
                    continue;
                }
            }

            // 到此为止, 1 圈才算结束
            circleCount++;
            zhuangJiaSet.clear();
        }

        if (circleCount < maxCircle) {
            currRoom.setEndOfAllRound(Room.EndOfAllRoundEnum.NO);
            return false;
        } else {
            currRoom.setEndOfAllRound(Room.EndOfAllRoundEnum.YES);
            return true;
        }
    }
}
