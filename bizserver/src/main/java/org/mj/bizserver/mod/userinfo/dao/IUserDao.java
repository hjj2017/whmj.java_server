package org.mj.bizserver.mod.userinfo.dao;

import org.apache.ibatis.annotations.Param;
import org.mj.comm.util.MySqlXuite;

/**
 * 用户 DAO
 */
@MySqlXuite.DAO
public interface IUserDao {
    /**
     * 根据用户 Id 获取用户实体
     *
     * @param userId 用户 Id
     * @return 用户实体
     */
    UserEntity getEntityByUserId(@Param("_userId") int userId);

    /**
     * 消耗房卡
     *
     * @param userId   用户 Id
     * @param deltaVal 变化值
     * @return 返回影响行数
     */
    int costRoomCard(
        @Param("_userId") int userId,
        @Param("_deltaVal") int deltaVal
    );
}
