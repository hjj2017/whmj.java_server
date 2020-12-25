package org.mj.bizserver.mod.record.dao;

import org.apache.ibatis.annotations.Param;
import org.mj.comm.util.MySqlXuite;

import java.util.List;

/**
 * 房间日志 DAO
 */
@MySqlXuite.DAO
public interface IRoomLogDao {
    /**
     * 插入数据,
     * 如果已有数据就更新
     *
     * @param newEntity 房间日志实体
     */
    void insertOrElseUpdate(RoomLogEntity newEntity);

    /**
     * 是否存在本周数据表?
     * 如果有, 就返回数据表名称;
     * 如果没有, 则返回空值;
     *
     * @param thisWeekTableNamePrefix 本周数据表名称前缀
     * @return 数据表名称
     */
    String existThisWeekTable(@Param("_thisWeekTableNamePrefix") String thisWeekTableNamePrefix);

    /**
     * 根据条件获取
     *
     * @param thisWeekTableNamePrefix 本周表名称前缀
     * @param userId                  用户 Id
     * @param clubId                  亲友圈 Id
     * @param gameType0               游戏类型 0
     * @param gameType1               游戏类型 1
     * @param limitOffset             分页位置
     * @param limitCount              单页记录数量
     * @return 房间日志实体
     */
    List<RoomLogEntity> getEntityListByCond(
        @Param("_thisWeekTableNamePrefix") String thisWeekTableNamePrefix,
        @Param("_userId") int userId,
        @Param("_clubId") int clubId,
        @Param("_gameType0") int gameType0,
        @Param("_gameType1") int gameType1,
        @Param("_limitOffset") int limitOffset,
        @Param("_limitCount") int limitCount
    );

    /**
     * 获取总数量
     *
     * @param thisWeekTableNamePrefix 本周表名称前缀
     * @param userId                  用户 Id
     * @param clubId                  亲友圈 Id
     * @param gameType0               游戏类型 0
     * @param gameType1               游戏类型 1
     * @return 记录数量
     */
    Integer getTotalCountByCond(
        @Param("_thisWeekTableNamePrefix") String thisWeekTableNamePrefix,
        @Param("_userId") int userId,
        @Param("_clubId") int clubId,
        @Param("_gameType0") int gameType0,
        @Param("_gameType1") int gameType1
    );

    /**
     * 获取房间日志实体
     *
     * @param thisWeekTableNamePrefix 本周表名称前缀
     * @param roomUUId                房间 UUId
     * @return 房间日志实体
     */
    RoomLogEntity getEntityByRoomUUId(
        @Param("_thisWeekTableNamePrefix") String thisWeekTableNamePrefix,
        @Param("_roomUUId") String roomUUId
    );
}
