package org.mj.bizserver.mod.club.membercenter;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberInfo;
import org.mj.bizserver.mod.club.membercenter.bizdata.Table;
import org.mj.comm.util.OutParam;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取牌桌列表
 */
interface MemberCenterBizLogic$getTableList {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MemberCenterBizLogic$getTableList.class);

    /**
     * 页面大小
     */
    int PAGE_SIZE = 10;

    /**
     * 最大牌桌数量
     */
    int MAX_NUM_OF_TABLE = 30;

    /**
     * 获取牌桌列表
     *
     * @param userId            用户 Id
     * @param clubId            亲友圈 Id
     * @param pageIndex         页面索引
     * @param out_maxNumOfTable ( 输出参数 ) 最大牌桌数量
     * @param callback          回调函数
     */
    default void getTableList_async(
        final int userId, final int clubId, final int pageIndex,
        final OutParam<Integer> out_maxNumOfTable,
        final IBizResultCallback<List<Table>> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<List<Table>>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        final BizResultWrapper<List<Table>> resultX = new BizResultWrapper<>();

        if (userId <= 0 ||
            clubId <= 0 ||
            pageIndex < 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定 Id
            clubId,
            // 异步 IO 操作
            () -> getTableList(userId, clubId, pageIndex, out_maxNumOfTable, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 获取牌桌列表
     *
     * @param userId            用户 Id
     * @param clubId            亲友圈 Id
     * @param pageIndex         页面索引
     * @param out_maxNumOfTable ( 输出参数 ) 最大牌桌数量
     * @param resultX           业务结果
     */
    default void getTableList(int userId, int clubId, int pageIndex, OutParam<Integer> out_maxNumOfTable, BizResultWrapper<List<Table>> resultX) {
        if (userId <= 0 ||
            clubId <= 0 ||
            pageIndex < 0 ||
            null == resultX) {
            return;
        }

        // 设置最大牌桌,
        // 未来可以根据代理等级提升牌桌数量
        OutParam.putVal(out_maxNumOfTable, MAX_NUM_OF_TABLE);

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 获取已经加入的亲友圈 Id 列表
            MemberInfo validMember = MemberInfoGetter.getValidMemberInfo(
                userId, clubId, redisCache
            );

            if (null == validMember) {
                // 如果玩家没有加入该亲友圈,
                LOGGER.error(
                    "用户尚未加入亲友圈, userId = {}, clubId = {}",
                    userId, clubId
                );

                // 清理 Redis 缓存
                redisCache.hdel(
                    RedisKeyDef.USER_X_PREFIX + userId,
                    RedisKeyDef.USER_JOINED_CLUB_ID_ARRAY
                );
                return;
            }

            // 获取桌号和房间 Id 字典
            Map<Integer, Integer> seqNumAndRoomIdMap = getSeqNumAndRoomIdMap(clubId, pageIndex, redisCache);

            if (seqNumAndRoomIdMap.isEmpty()) {
                return;
            }

            // 获取牌桌列表
            List<Table> tableList = getTableList(seqNumAndRoomIdMap, redisCache);
            resultX.setFinalResult(tableList);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取桌号所对应的房间 Id
     *
     * @param clubId     亲友圈 Id
     * @param pageIndex  页面索引
     * @param redisCache Redis 缓存
     * @return 字典, key = 桌号, val = 房间 Id
     */
    static private Map<Integer, Integer> getSeqNumAndRoomIdMap(int clubId, int pageIndex, final Jedis redisCache) {
        if (clubId <= 0 ||
            pageIndex < 0 ||
            null == redisCache) {
            return Collections.emptyMap();
        }

        // 主键
        final String key0 = RedisKeyDef.CLUB_X_PREFIX + clubId;
        // 桌号列表和子键
        final List<Integer> seqNumList = new ArrayList<>(PAGE_SIZE);
        final String[] key1Array = new String[PAGE_SIZE];

        for (int i = 0; i < PAGE_SIZE; i++) {
            // 根据页面索引推算桌号
            int seqNum = pageIndex * PAGE_SIZE + i;
            // 添加桌号和关键字数组
            seqNumList.add(i, seqNum);
            key1Array[i] = RedisKeyDef.CLUB_TABLE_X_PREFIX + seqNum;
        }

        List<String> strRoomIdList = redisCache.hmget(
            key0, key1Array
        );

        Map<Integer, Integer> roomIdMap = new HashMap<>();

        for (int j = 0; j < seqNumList.size(); j++) {
            // 获取房间 Id
            String strRoomId = strRoomIdList.get(j);

            if (null == strRoomId ||
                strRoomId.isEmpty()) {
                continue;
            }

            roomIdMap.put(
                seqNumList.get(j), Integer.parseInt(strRoomId)
            );
        }

        return roomIdMap;
    }

    /**
     * 获取牌桌列表
     *
     * @param seqNumAndRoomIdMap 桌号和房间 Id 字典
     * @param redisCache         Redis 缓存
     * @return 牌桌列表
     */
    static private List<Table> getTableList(Map<Integer, Integer> seqNumAndRoomIdMap, final Jedis redisCache) {
        if (null == seqNumAndRoomIdMap ||
            seqNumAndRoomIdMap.isEmpty() ||
            null == redisCache) {
            return null;
        }

        // 获取 Redis 管道
        final Pipeline pl = redisCache.pipelined();

        for (Integer roomId : seqNumAndRoomIdMap.values()) {
            if (null == roomId ||
                roomId <= 0) {
                continue;
            }

            // 通过管道方式批量获取房间详情
            pl.hget(
                RedisKeyDef.ROOM_X_PREFIX + roomId,
                RedisKeyDef.ROOM_DETAILZ
            );
        }

        // 执行批处理并取得结果
        List<Object> objList = pl.syncAndReturnAll();
        // 牌桌列表
        List<Table> tableList = new ArrayList<>(objList.size());

        for (Object currObj : objList) {
            if (!(currObj instanceof String)) {
                continue;
            }

            JSONObject jsonObj = JSONObject.parseObject((String) currObj);
            Table newTable = Table.fromJSON(jsonObj);
            tableList.add(newTable);
        }

        return tableList;
    }
}
