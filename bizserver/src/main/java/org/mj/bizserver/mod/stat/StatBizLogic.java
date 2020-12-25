package org.mj.bizserver.mod.stat;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.mod.stat.dao.IUserGameLogDao;
import org.mj.bizserver.mod.stat.dao.UserGameLogEntity;
import org.mj.comm.util.MySqlXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 统计业务逻辑
 */
public final class StatBizLogic {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(StatBizLogic.class);

    /**
     * 单例对象
     */
    static private final StatBizLogic _instance = new StatBizLogic();

    /**
     * 私有化类默认构造器
     */
    private StatBizLogic() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public StatBizLogic getInstance() {
        return _instance;
    }

    /**
     * 保存日志实体列表
     *
     * @param logEntityList 日志实体列表
     */
    public void saveLogEntityList(List<UserGameLogEntity> logEntityList) {
        if (null == logEntityList ||
            logEntityList.size() <= 0) {
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openLogDbSession()) {
            // 获取 DAO 并执行插入操作
            IUserGameLogDao daoX = sessionX.getMapper(IUserGameLogDao.class);
            logEntityList.forEach(daoX::insertOrElseUpdate);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
