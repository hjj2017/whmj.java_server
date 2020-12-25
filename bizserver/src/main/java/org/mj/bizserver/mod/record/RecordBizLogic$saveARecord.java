package org.mj.bizserver.mod.record;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.mod.record.dao.IRoomLogDao;
import org.mj.bizserver.mod.record.dao.IRoundLogDao;
import org.mj.bizserver.mod.record.dao.RoomLogEntity;
import org.mj.bizserver.mod.record.dao.RoundLogEntity;
import org.mj.comm.util.MySqlXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 保存一条记录
 */
public interface RecordBizLogic$saveARecord {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(RecordBizLogic$saveARecord.class);

    /**
     * 保存一条记录
     *
     * @param roomLogEntity 房间日志实体
     */
    default void saveARecord(RoomLogEntity roomLogEntity) {
        if (null == roomLogEntity ||
            null == roomLogEntity.getRoomUUId() ||
            null == roomLogEntity.getCreateTime() ||
            roomLogEntity.getCreateTime() <= 0) {
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openLogDbSession()) {
            // 插入或更新日志
            sessionX.getMapper(IRoomLogDao.class).insertOrElseUpdate(roomLogEntity);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 保存一条记录
     *
     * @param roundLogEntity 牌局日志实体
     */
    default void saveARecord(RoundLogEntity roundLogEntity) {
        if (null == roundLogEntity ||
            null == roundLogEntity.getRoomUUId()) {
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openLogDbSession()) {
            // 插入或更新日志
            sessionX.getMapper(IRoundLogDao.class).insertOrElseUpdate(roundLogEntity);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
