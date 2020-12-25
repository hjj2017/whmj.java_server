package org.mj.bizserver.mod.club.adminctrl.dao;

import org.apache.ibatis.annotations.Param;
import org.mj.comm.util.MySqlXuite;

/**
 * 亲友圈成员 DAO
 */
@MySqlXuite.DAO
public interface IClubMemberDao {
    /**
     * 获取用户所在亲友圈的角色
     *
     * @param userId 用户 Id
     * @param clubId 亲友圈 Id
     * @return 角色数值
     */
    Integer getRole(
        @Param("_userId") int userId,
        @Param("_clubId") int clubId
    );

    /**
     * 修改当前状态
     *
     * @param userId   用户 Id
     * @param clubId   亲友圈 Id
     * @param newState 新状态
     */
    void updateCurrState(
        @Param("_userId") int userId,
        @Param("_clubId") int clubId,
        @Param("_newState") int newState
    );

    /**
     * 修改角色
     *
     * @param userId  用户 Id
     * @param clubId  亲友圈 Id
     * @param newRole 新角色
     */
    void updateRole(
        @Param("_userId") int userId,
        @Param("_clubId") int clubId,
        @Param("_newRole") int newRole
    );

    /**
     * 根据状态统计成员数量
     *
     * @param clubId     亲友圈 Id
     * @param stateArray 状态数组
     * @return 成员数量
     */
    int getMemberCountByState(
        @Param("_clubId") int clubId,
        @Param("_stateArray") int... stateArray
    );
}
