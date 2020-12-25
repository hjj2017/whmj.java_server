package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.SettlementResult;

import java.util.List;

/**
 * 房间结算广播
 */
public final class RoomSettlementPostman {
    /**
     * 私有化类默认构造器
     */
    private RoomSettlementPostman() {
    }

    /**
     * 根据当前房间投递房间结算
     *
     * @param currRoom 当前房间
     */
    static public void post(Room currRoom) {
        if (null == currRoom) {
            return;
        }

        // 创建构建者
        MJ_weihai_Protocol.RoomSettlementBroadcast.Builder
            b0 = MJ_weihai_Protocol.RoomSettlementBroadcast.newBuilder();

        // 获取玩家列表
        List<Player> playerList = currRoom.getPlayerListCopy();

        for (Player currPlayer : playerList) {
            if (null == currPlayer) {
                continue;
            }

            // 获取结算结果
            final SettlementResult stmtResult = currPlayer.getSettlementResult();

            // 创建构建者
            MJ_weihai_Protocol.RoomSettlementBroadcast.SettlementItem.Builder
                b1 = MJ_weihai_Protocol.RoomSettlementBroadcast.SettlementItem.newBuilder();

            b1.setUserId(currPlayer.getUserId())
                .setSeatIndex(currPlayer.getSeatIndex())
                .setRoomOwnerFlag(currPlayer.isRoomOwner())
                .setZuoZhuangTimez(stmtResult.getZuoZhuangTimez())
                .setHuPaiTimez(stmtResult.getHuPaiTimez())
                .setZiMoTimez(stmtResult.getZiMoTimez())
                .setDianPaoTimez(stmtResult.getDianPaoTimez())
                .setTotalScore(currPlayer.getTotalScore())
                .setBigWinner(currPlayer.isBigWinner());

            b0.addSettlementItem(b1);
        }

        // 广播房间结算
        MJ_weihai_Protocol.RoomSettlementBroadcast r = b0.build();
        GameBroadcaster.broadcast(currRoom, r);

        // 房间结束,
        // 将用户移出广播列表
        for (Player currPlayer : currRoom.getPlayerListCopy()) {
            if (null != currPlayer) {
                GameBroadcaster.removeByUserId(
                    currPlayer.getUserId()
                );
            }
        }
    }
}
