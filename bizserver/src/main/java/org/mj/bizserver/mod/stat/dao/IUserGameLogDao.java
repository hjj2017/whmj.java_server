package org.mj.bizserver.mod.stat.dao;

import org.mj.comm.util.MySqlXuite;

/**
 * 用户游戏日志 DAO
 */
@MySqlXuite.DAO
public interface IUserGameLogDao {
    /**
     * 插入实体对象
     *
     * @param entity 实体对象
     */
    void insertOrElseUpdate(UserGameLogEntity entity);
}
