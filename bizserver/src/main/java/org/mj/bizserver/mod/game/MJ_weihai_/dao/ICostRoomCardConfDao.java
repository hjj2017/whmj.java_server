package org.mj.bizserver.mod.game.MJ_weihai_.dao;

import org.apache.ibatis.annotations.Param;
import org.mj.comm.util.MySqlXuite;

import java.util.List;

/**
 * 消耗房卡配置 DAO
 */
@MySqlXuite.DAO
public interface ICostRoomCardConfDao {
    /**
     * 根据游戏类型获取消耗房卡配置列表
     *
     * @param gameType0 游戏类型 0
     * @param gameType1 游戏类型 1
     * @return 消耗房卡数量配置列表
     */
    List<CostRoomCardConfEntity> listByGameType(
        @Param("_gameType0") int gameType0,
        @Param("_gameType1") int gameType1
    );
}
