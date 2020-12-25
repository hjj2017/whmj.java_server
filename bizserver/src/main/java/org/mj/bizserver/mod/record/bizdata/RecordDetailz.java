package org.mj.bizserver.mod.record.bizdata;

import org.mj.bizserver.mod.record.dao.RoomLogEntity;
import org.mj.bizserver.mod.record.dao.RoundLogEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 战绩详情
 */
public final class RecordDetailz {
    /**
     * 房间日志实体
     */
    private final RoomLogEntity _roomLogEntity;

    /**
     * 牌局日志实体列表
     */
    private final List<RoundLogEntity> _roundLogEntityList;

    /**
     * 牌局列表
     */
    private List<ARound> _roundList;

    /**
     * 类参数构造器
     *
     * @param roomLogEntity      房间日志实体
     * @param roundLogEntityList 牌局日志实体列表
     * @throws IllegalArgumentException if null == roomLogEntity || null == roundLogEntityList
     */
    public RecordDetailz(
        RoomLogEntity roomLogEntity,
        List<RoundLogEntity> roundLogEntityList) {
        if (null == roomLogEntity ||
            null == roundLogEntityList) {
            throw new IllegalArgumentException("roomLogEntity or roundLogEntityList is null");
        }

        _roomLogEntity = roomLogEntity;
        _roundLogEntityList = roundLogEntityList;
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
     * 获取消费房卡数量
     *
     * @return 消费房卡数量
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
     * 获取 ( 房间 ) 创建时间
     *
     * @return ( 房间 ) 创建时间
     */
    public long getCreateTime() {
        return _roomLogEntity.getCreateTime();
    }

    /**
     * 获取牌局列表
     *
     * @return 牌局列表
     */
    public List<ARound> getRoundList() {
        if (null != _roundList) {
            return _roundList;
        }

        final List<ARound> roundList = new ArrayList<>();

        for (RoundLogEntity roundLogEntity : _roundLogEntityList) {
            if (null != roundLogEntity) {
                roundList.add(new ARound(roundLogEntity));
            }
        }

        return _roundList = roundList;
    }
}
