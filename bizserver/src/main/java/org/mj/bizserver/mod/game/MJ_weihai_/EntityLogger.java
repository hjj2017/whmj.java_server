package org.mj.bizserver.mod.game.MJ_weihai_;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.GameType1Enum;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.StateTable;
import org.mj.bizserver.mod.record.RecordBizLogic;
import org.mj.bizserver.mod.record.dao.RoomLogEntity;
import org.mj.bizserver.mod.record.dao.RoundLogEntity;
import org.mj.bizserver.mod.stat.StatBizLogic;
import org.mj.bizserver.mod.stat.dao.UserGameLogEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体日志
 */
final class EntityLogger {
    /**
     * 私有化类
     */
    private EntityLogger() {
    }

    /**
     * 创建房间实体日志并保存
     *
     * @param currRoom 当前房间
     */
    static void createRoomEntityLogAndSave(Room currRoom) {
        if (null == currRoom) {
            return;
        }

        final RoomLogEntity entity = new RoomLogEntity();
        entity.setRoomUUId(currRoom.getRoomUUId());
        entity.setRoomId(currRoom.getRoomId());
        entity.setClubId(currRoom.getClubId());
        entity.setCreateTime(currRoom.getCreateTime());

        // 将结束时间默认为当前时间
        // 因为在每一次牌局结束时都会更新房间日志
        entity.setOverTime(System.currentTimeMillis());

        entity.setOwnerId(currRoom.getOwnerId());
        entity.setGameType0(GameType1Enum.MJ_weihai_.getGameType0().getIntVal());
        entity.setGameType1(GameType1Enum.MJ_weihai_.getIntVal());
        entity.setRuleSetting(JSONObject.toJSONString(
            currRoom.getRuleSetting().getInnerMap()
        ));

        final JSONObject joAllPlayer = new JSONObject();
        final JSONObject joTotalScore = new JSONObject();

        for (Player currPlayer : currRoom.getPlayerListCopy()) {
            if (null == currPlayer) {
                continue;
            }

            final String strUserId = String.valueOf(currPlayer.getUserId());

            // 创建玩家信息
            JSONObject joPlayer = new JSONObject(true);
            joPlayer.put("userId", currPlayer.getUserId());
            joPlayer.put("userName", currPlayer.getUserName());
            joPlayer.put("headImg", currPlayer.getHeadImg());
            joPlayer.put("sex", currPlayer.getSex());
            joPlayer.put("seatIndex", currPlayer.getSeatIndex());
            joAllPlayer.put(strUserId, joPlayer);

            // 创建总分记录
            joTotalScore.put(strUserId, currPlayer.getTotalScore());

            //  设置用户 Id, 方便之后的查询
            entity.putUserIdX(
                currPlayer.getSeatIndex(),
                currPlayer.getUserId()
            );
        }

        entity.setAllPlayer(joAllPlayer.toJSONString());
        entity.setAllTotalScore(joTotalScore.toJSONString());
        entity.setCostRoomCard(currRoom.getCostRoomCard());
        entity.setActualRoundCount(currRoom.getEndedRoundCount());

        // 保存一条记录
        RecordBizLogic.getInstance().saveARecord(entity);
    }

    /**
     * 创建牌局实体日志并保存
     *
     * @param roomUUId  房间 UUId
     * @param currRound 当前牌局
     */
    static void createRoundLogEntityAndSave(String roomUUId, Round currRound) {
        if (null == roomUUId ||
            null == currRound) {
            return;
        }

        final RoundLogEntity entity = new RoundLogEntity();
        entity.setRoomUUId(roomUUId);
        entity.setRoundIndex(currRound.getRoundIndex());
        entity.setCreateTime(currRound.getCreateTime());

        final JSONObject joAllPlayer = new JSONObject();
        final JSONObject joAllCurrScore = new JSONObject();

        for (Player currPlayer : currRound.getPlayerListCopy()) {
            if (null == currPlayer) {
                continue;
            }

            final String strUserId = String.valueOf(currPlayer.getUserId());
            final StateTable currState = currPlayer.getCurrState();

            // 创建玩家信息
            JSONObject joPlayer = new JSONObject(true);
            joPlayer.put("userId", currPlayer.getUserId());
            joPlayer.put("userName", currPlayer.getUserName());
            joPlayer.put("headImg", currPlayer.getHeadImg());
            joPlayer.put("sex", currPlayer.getSex());
            joPlayer.put("seatIndex", currPlayer.getSeatIndex());
            joPlayer.put("zhuangFlag", currState.isZhuangJia());
            joPlayer.put("ziMo", currState.isZiMo());
            joPlayer.put("hu", currState.isHu());
            joPlayer.put("dianPao", currState.isDianPao());
            joAllPlayer.put(strUserId, joPlayer);

            joAllCurrScore.put(
                String.valueOf(currPlayer.getUserId()),
                currPlayer.getCurrScore()
            );

            entity.putUserIdX(
                currPlayer.getSeatIndex(),
                currPlayer.getUserId()
            );
        }

        entity.setAllPlayer(joAllPlayer.toJSONString());
        entity.setAllCurrScore(joAllCurrScore.toJSONString());

        // 保存一条记录
        RecordBizLogic.getInstance().saveARecord(entity);
    }

    /**
     * 创建用户游戏日志并保存
     *
     * @param currRoom 当前房间
     */
    static void createUserGameLogAndSave(Room currRoom) {
        if (null == currRoom) {
            return;
        }

        // 日志实体列表
        List<UserGameLogEntity> logEntityList = new ArrayList<>();

        for (Player currPlayer : currRoom.getPlayerListCopy()) {
            if (null == currPlayer) {
                continue;
            }

            UserGameLogEntity logEntity = new UserGameLogEntity();
            logEntity.setUserId(currPlayer.getUserId());
            logEntity.setClubId(currRoom.getClubId());
            logEntity.setRoomId(currRoom.getRoomId());
            logEntity.setRoomUUId(currRoom.getRoomUUId());
            logEntity.setGameType0(currRoom.getGameType0().getIntVal());
            logEntity.setGameType1(currRoom.getGameType1().getIntVal());
            logEntity.setCreateTime(currRoom.getCreateTime());
            logEntity.setTotalScore(currPlayer.getTotalScore());
            logEntity.setIsWinner(currPlayer.isBigWinner() ? 1 : 0);

            logEntityList.add(logEntity);
        }

        // 保存日志实体列表
        StatBizLogic.getInstance().saveLogEntityList(logEntityList);
    }
}
