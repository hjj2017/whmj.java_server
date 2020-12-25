package org.mj.bizserver.mod.club.adminctrl.dao;

import org.apache.ibatis.annotations.Param;
import org.mj.comm.util.MySqlXuite;

/**
 * 亲友圈 DAO
 */
@MySqlXuite.DAO
public interface IClubDao {
    /**
     * 插入新的亲友圈实体
     *
     * @param newEntity 新的亲友圈实体
     */
    void insertInto(ClubEntity newEntity);

    /**
     * 更新亲友圈人数
     *
     * @param clubId 亲友圈 Id
     */
    void updateNumOfPeople(@Param("_clubId") int clubId);

    /**
     * 修改固定玩法
     *
     * @param clubId  亲友圈 Id
     * @param index   索引
     * @param fixGame 固定玩法
     */
    void updateFixGameX(
        @Param("_clubId") int clubId,
        @Param("_index") int index,
        @Param("_fixGame") String fixGame
    );

    /**
     * 添加房卡数量
     *
     * @param userId     用户 Id
     * @param clubId     亲友圈 Id
     * @param exRoomCard 充值房卡数量
     */
    void addRoomCard(
        @Param("_userId") int userId,
        @Param("_clubId") int clubId,
        @Param("_exRoomCard") int exRoomCard
    );

    /**
     * 更新当前状态
     *
     * @param clubId    亲友圈 Id
     * @param currState 当前状态
     */
    void updateCurrState(
        @Param("_clubId") int clubId,
        @Param("_currState") int currState
    );
}
