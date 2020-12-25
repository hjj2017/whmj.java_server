package org.mj.bizserver.mod.club.membercenter.dao;

import org.apache.ibatis.annotations.Param;
import org.mj.comm.util.MySqlXuite;

import java.util.List;

/**
 * 亲友圈成员 DAO
 */
@MySqlXuite.DAO
public interface IClubMemberDao {
    /**
     * 根据用户 Id 和状态获取亲友圈 Id 列表
     *
     * @param userId     用户 Id
     * @param stateArray 状态数组
     * @return 亲友圈 Id 列表
     */
    List<Integer> getClubIdList(
        @Param("_userId") int userId,
        @Param("_stateArray") int... stateArray
    );

    /**
     * 根据亲友圈 Id 获取用户 Id 列表
     *
     * @param clubId     亲友圈 Id
     * @param stateArray 状态数组
     * @return 用户 Id 列表
     */
    List<Integer> getUserIdList(
        @Param("_clubId") int clubId,
        @Param("_stateArray") int... stateArray
    );

    /**
     * 根据用户 Id 和亲友圈 Id 获取亲友圈成员实体
     *
     * @param userId 用户 Id
     * @param clubId 亲友圈 Id
     * @return 亲友圈成员实体
     */
    ClubMemberEntity getClubMemberEntity(
        @Param("_userId") int userId, @Param("_clubId") int clubId
    );

    /**
     * 插入亲友圈成员实体
     *
     * @param newEntity 亲友圈成员实体
     */
    void insertInto(ClubMemberEntity newEntity);

    /**
     * 修改当前状态
     *
     * @param userId    用户 Id
     * @param clubId    亲友圈 Id
     * @param currState 当前状态
     */
    void changeCurrState(
        @Param("_userId") int userId,
        @Param("_clubId") int clubId,
        @Param("_currState") int currState
    );
}
