package org.mj.bizserver.mod.record.bizdata;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.mod.record.dao.RoomLogEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 战绩摘要
 */
public final class RecordSummary {
    /**
     * 房间日志实体
     */
    private final RoomLogEntity _roomLogEntity;

    /**
     * 玩家列表
     */
    private List<Player> _playerList;

    /**
     * 类参数构造器
     *
     * @param roomLogEntity 房间日志实体
     * @throws IllegalArgumentException if null == roomLogEntity
     */
    public RecordSummary(RoomLogEntity roomLogEntity) {
        if (null == roomLogEntity) {
            throw new IllegalArgumentException("entity is null");
        }

        _roomLogEntity = roomLogEntity;
    }

    /**
     * 获取游戏类型 0 整数值
     *
     * @return 游戏类型 0 整数值
     */
    public int getGameType0IntVal() {
        return _roomLogEntity.getGameType0();
    }

    /**
     * 获取游戏类型 1 整数值
     *
     * @return 游戏类型 1 整数值
     */
    public int getGameType1IntVal() {
        return _roomLogEntity.getGameType1();
    }

    /**
     * 获取房间 Id
     *
     * @return 房间 Id
     */
    public int getRoomId() {
        return _roomLogEntity.getRoomId();
    }

    /**
     * 获取房间 UUId
     *
     * @return 房间 UUId
     */
    public String getRoomUUId() {
        return _roomLogEntity.getRoomUUId();
    }

    /**
     * 获取花费房卡数量
     *
     * @return 花费房卡数量
     */
    public int getCostRoomCard() {
        return _roomLogEntity.getCostRoomCard();
    }

    /**
     * 获取实际局数
     *
     * @return 实际局数
     */
    public int getActualRoundCount() {
        return _roomLogEntity.getActualRoundCount();
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public long getCreateTime() {
        return _roomLogEntity.getCreateTime();
    }

    /**
     * 获取结束时间
     *
     * @return 结束时间
     */
    public long getOverTime() {
        return _roomLogEntity.getOverTime();
    }

    /**
     * 获取玩家列表
     *
     * @return 玩家列表
     */
    public List<Player> getPlayerList() {
        if (null != _playerList) {
            return _playerList;
        }

        // 获取所有玩家
        final JSONObject joAllPlayer = JSONObject.parseObject(_roomLogEntity.getAllPlayer());
        // 获取所有总分数
        final JSONObject joAllTotalScore = JSONObject.parseObject(_roomLogEntity.getAllTotalScore());

        if (null == joAllPlayer ||
            joAllPlayer.isEmpty() ||
            null == joAllTotalScore ||
            joAllTotalScore.isEmpty()) {
            return null;
        }

        final List<Player> playerList = new ArrayList<>(8);

        for (String strUserId : joAllPlayer.keySet()) {
            // 获取玩家 JSON 对象
            JSONObject joPlayer = joAllPlayer.getJSONObject(strUserId);

            if (null != joPlayer) {
                // 转成 Java 对象
                Player currPlayer = joPlayer.toJavaObject(Player.class);

                playerList.add(currPlayer);
                currPlayer.setTotalScore(joAllTotalScore.getIntValue(strUserId));
            }
        }

        // 按照座位索引排序
        playerList.sort(Comparator.comparingInt(Player::getSeatIndex));

        return _playerList = playerList;
    }
}
