package org.mj.bizserver.mod.record;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.base.AsyncOperationProcessorSingleton;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.IBizResultCallback;
import org.mj.bizserver.mod.record.bizdata.RecordDetailz;
import org.mj.bizserver.mod.record.dao.IRoomLogDao;
import org.mj.bizserver.mod.record.dao.IRoundLogDao;
import org.mj.bizserver.mod.record.dao.RoomLogEntity;
import org.mj.bizserver.mod.record.dao.RoundLogEntity;
import org.mj.comm.util.DateTimeUtil;
import org.mj.comm.util.MySqlXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ( 异步方式 ) 获取战绩详情
 */
interface RecordBizLogic$getRecordDetailz_async {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(RecordBizLogic$getRecordDetailz_async.class);

    /**
     * ( 异步方式 ) 获取战绩详情
     *
     * @param roomUUId 房间 UUId
     * @param callback 回调函数
     */
    default void getRecordDetailz_async(
        final String roomUUId, final IBizResultCallback<RecordDetailz> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<RecordDetailz>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        final BizResultWrapper<RecordDetailz> resultX = new BizResultWrapper<>();

        if (null == roomUUId ||
            roomUUId.isEmpty()) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定线程 Id
            roomUUId.charAt(roomUUId.length() - 1),
            // 异步 IO 操作
            () -> getRecordDetailz(roomUUId, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 获取战绩详情
     *
     * @param roomUUId 房间 UUId
     * @param resultX  业务结果
     */
    private void getRecordDetailz(
        final String roomUUId, final BizResultWrapper<RecordDetailz> resultX) {

        if (null == roomUUId ||
            roomUUId.isEmpty()) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openLogDbSession()) {
            // 获取 DAO
            final IRoundLogDao daoX = sessionX.getMapper(IRoundLogDao.class);

            // 获取本周数据表名称前缀
            final String thisWeekTableNamePrefix = DateTimeUtil.getMondayDateStr(System.currentTimeMillis());

            if (null == daoX.existThisWeekTable(thisWeekTableNamePrefix)) {
                LOGGER.error(
                    "本周数据表不存在, thisWeekTableNamePrefix = {}",
                    thisWeekTableNamePrefix
                );
                return;
            }

            // 获取牌局日志实体列表
            final List<RoundLogEntity>
                roundLogEntityList = daoX.getEntityListByRoomUUId(
                    thisWeekTableNamePrefix, roomUUId
            );

            if (null == roundLogEntityList ||
                roundLogEntityList.size() <= 0) {
                return;
            }

            final IRoomLogDao daoY = sessionX.getMapper(IRoomLogDao.class);
            final RoomLogEntity roomLogEntity = daoY.getEntityByRoomUUId(thisWeekTableNamePrefix, roomUUId);

            resultX.setFinalResult(new RecordDetailz(
                roomLogEntity,
                roundLogEntityList
            ));
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
