package org.mj.bizserver.mod.record;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.GameType0Enum;
import org.mj.bizserver.def.GameType1Enum;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.record.bizdata.RecordSummary;
import org.mj.bizserver.mod.record.dao.IRoomLogDao;
import org.mj.bizserver.mod.record.dao.RoomLogEntity;
import org.mj.comm.util.DateTimeUtil;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.OutParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ( 异步方式 ) 获取战绩列表
 */
interface RecordBizLogic$getRecordList_async {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(RecordBizLogic$getRecordList_async.class);

    /**
     * ( 异步方式 ) 获取战绩列表
     *
     * @param userId         用户 Id
     * @param clubId         亲友圈 Id
     * @param gameType0      游戏类型 0
     * @param gameType1      游戏类型 1
     * @param pageIndex      页面索引
     * @param pageSize       页面大小 ( 记录个数 )
     * @param out_totalCount ( 输出参数 ) 总数量
     * @param callback       回调函数
     */
    default void getRecordList_async(
        int userId, int clubId, GameType0Enum gameType0, GameType1Enum gameType1, int pageIndex, int pageSize,
        OutParam<Integer> out_totalCount,
        IBizResultCallback<List<RecordSummary>> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<List<RecordSummary>>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        final BizResultWrapper<List<RecordSummary>> resultX = new BizResultWrapper<>();

        if ((userId <= 0 && clubId <= 0) ||
            pageIndex < 0 ||
            pageSize <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定线程 Id
            userId > 0 ? userId : clubId,
            // 异步 IO 操作
            () -> getRecordList(userId, clubId, gameType0, gameType1, pageIndex, pageSize, out_totalCount, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 获取战绩列表
     *
     * @param userId         用户 Id
     * @param clubId         亲友圈 Id
     * @param gameType0      游戏类型 0
     * @param gameType1      游戏类型 1
     * @param pageIndex      页面索引
     * @param pageSize       页面大小 ( 记录个数 )
     * @param out_totalCount ( 输出参数 ) 总数量
     * @param resultX        业务结果
     */
    private void getRecordList(
        int userId, int clubId, GameType0Enum gameType0, GameType1Enum gameType1, int pageIndex, int pageSize,
        OutParam<Integer> out_totalCount,
        BizResultWrapper<List<RecordSummary>> resultX) {

        if ((userId <= 0 && clubId <= 0) ||
            pageIndex < 0 ||
            pageSize <= 0 ||
            null == resultX) {
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openLogDbSession()) {
            // 获取房间日志 DAO
            IRoomLogDao daoX = sessionX.getMapper(IRoomLogDao.class);

            // 获取本周数据表名称前缀
            final String thisWeekTableNamePrefix = DateTimeUtil.getMondayDateStr(System.currentTimeMillis());

            if (null == daoX.existThisWeekTable(thisWeekTableNamePrefix)) {
                LOGGER.error(
                    "本周数据表不存在, thisWeekTableNamePrefix = {}",
                    thisWeekTableNamePrefix
                );
                return;
            }

            // 获取游戏类型整数值
            final int gameType0IntVal = (null == gameType0) ? -1 : gameType0.getIntVal();
            final int gameType1IntVal = (null == gameType1) ? -1 : gameType1.getIntVal();
            // 分页位置
            final int limitOffset = pageIndex * pageSize;

            // 获取实体列表
            List<RoomLogEntity> entityList = daoX.getEntityListByCond(
                thisWeekTableNamePrefix,
                userId, clubId, gameType0IntVal, gameType1IntVal, limitOffset, pageSize
            );

            if (null != entityList &&
                entityList.size() > 0) {
                // 战绩摘要列表
                final List<RecordSummary> recordSummaryList = new ArrayList<>();

                for (RoomLogEntity entity : entityList) {
                    if (null != entity) {
                        recordSummaryList.add(new RecordSummary(entity));
                    }
                }

                resultX.setFinalResult(recordSummaryList);
            }

            // 总数
            int totalCount = Objects.requireNonNullElse(
                daoX.getTotalCountByCond(
                    thisWeekTableNamePrefix,
                    userId, clubId, gameType0IntVal, gameType1IntVal
                ),
                0 // 默认总数 = 0
            );

            // 设置输出参数
            OutParam.putVal(out_totalCount, totalCount);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
