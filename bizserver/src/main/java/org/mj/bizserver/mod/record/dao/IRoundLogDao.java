package org.mj.bizserver.mod.record.dao;

import org.apache.ibatis.annotations.Param;
import org.mj.comm.util.MySqlXuite;

import java.util.List;

/**
 * 牌局日志 DAO
 */
@MySqlXuite.DAO
public interface IRoundLogDao {
    /**
     * 插入否则更新数据
     *
     * @param newEntity 牌局日志实体
     */
    void insertOrElseUpdate(RoundLogEntity newEntity);

    /**
     * 是否存在本周数据表?
     * 如果有, 就返回数据表名称;
     * 如果没有, 则返回空值;
     *
     * @param thisWeekTableNamePrefix 本周数据表名称前缀
     * @return 数据表名称
     */
    String existThisWeekTable(
        @Param("_thisWeekTableNamePrefix") String thisWeekTableNamePrefix
    );

    /**
     * 根据房间 UUId 获取牌局日志实体列表
     *
     * @param thisWeekTableNamePrefix 本周数据表名称前缀
     * @param roomUUId                房间 UUId
     * @return 牌局日志实体列表
     */
    List<RoundLogEntity> getEntityListByRoomUUId(
        @Param("_thisWeekTableNamePrefix") String thisWeekTableNamePrefix,
        @Param("_roomUUId") String roomUUId
    );
}
