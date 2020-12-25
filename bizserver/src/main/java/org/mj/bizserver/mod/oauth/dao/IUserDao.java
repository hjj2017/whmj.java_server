package org.mj.bizserver.mod.oauth.dao;

import org.apache.ibatis.annotations.Param;
import org.mj.comm.util.MySqlXuite;

/**
 * 用户 DAO
 */
@MySqlXuite.DAO
public interface IUserDao {
    /**
     * 根据列 X 获取用户 Id
     *
     * @param columnName 列 X
     * @param columnVal  列数值
     * @return 用户 Id
     */
    UserEntity getEntityByColumnX(
        @Param("_columnName") String columnName,
        @Param("_columnVal") String columnVal
    );

    /**
     * 添加或更新用户实体
     *
     * @param newEntity 新实体
     */
    void insertOrUpdate(UserEntity newEntity);

    /**
     * 根据用户 Id 更新列
     *
     * @param userId     用户 Id
     * @param columnName 列名称
     * @param columnVal  列数值
     */
    void updateColumnXByUserId(
        @Param("_userId") int userId,
        @Param("_columnName") String columnName,
        @Param("_columnVal") String columnVal
    );
}
