package org.mj.bizserver.mod.club.membercenter.dao;

import org.apache.ibatis.annotations.Param;
import org.mj.comm.util.MySqlXuite;

/**
 * 亲友圈 DAO
 */
@MySqlXuite.DAO
public interface IClubDao {
    /**
     * 根据 Id 获取亲友圈实体
     *
     * @param clubId 亲友圈 Id
     * @return 亲友圈实体
     */
    ClubEntity getByClubId(@Param("_clubId") int clubId);

    /**
     * 更新亲友圈人数
     *
     * @param clubId 亲友圈 Id
     */
    void updateNumOfPeople(@Param("_clubId") int clubId);

    /**
     * 消耗房卡
     *
     * @param clubId   亲友圈 Id
     * @param deltaVal 变化值
     * @return 返回影响行数
     */
    int costRoomCard(
        @Param("_clubId") int clubId,
        @Param("_deltaVal") int deltaVal
    );
}
