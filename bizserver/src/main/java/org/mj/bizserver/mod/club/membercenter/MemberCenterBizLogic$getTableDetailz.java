package org.mj.bizserver.mod.club.membercenter;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
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

/**
 * 获取牌桌详情
 */
interface MemberCenterBizLogic$getTableDetailz {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MemberCenterBizLogic$getTableDetailz.class);

    /**
     * ( 异步方式 ) 获取牌桌详情
     *
     * @param userId   用户 Id
     * @param clubId   亲友圈 Id
     * @param seqNo    牌桌序号
     * @param callback 回调函数
     */
    default void getTableDetailz_async(
        final int userId, final int clubId, final int seqNo, final IBizResultCallback<Table> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Table>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        BizResultWrapper<Table> resultX = new BizResultWrapper<>();

        if (userId <= 0 ||
            clubId <= 0 ||
            seqNo < 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定 Id
            clubId,
            // 异步 IO 操作
            () -> getTableDetailz(userId, clubId, seqNo, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 获取牌桌详情
     *
     * @param userId  用户 Id
     * @param clubId  亲友圈 Id
     * @param seqNo   牌桌序号
     * @param resultX 业务结果
     */
    default void getTableDetailz(
        int userId, int clubId, int seqNo, BizResultWrapper<Table> resultX) {
        if (userId <= 0 ||
            clubId <= 0 ||
            seqNo < 0 ||
            null == resultX) {
            return;
        }

        // ( 输出参数 ) MySql 会话
        final OutParam<SqlSession> out_mySqlSession = new OutParam<>();

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 获取我自己的成员信息
            MemberInfo validMember = MemberInfoGetter.getValidMemberInfo(
                userId, clubId, redisCache, out_mySqlSession
            );

            if (null == validMember) {
                LOGGER.error(
                    "用户不是该帮会的成员, 无法获取牌桌详情! userId = {}, clubId = {}, tableSeqNo = {}",
                    userId, clubId, seqNo
                );
                resultX.setFinalResult(null);
                return;
            }

            // 获取牌桌房间号
            String strRoomId = redisCache.hget(
                RedisKeyDef.CLUB_X_PREFIX + clubId,
                RedisKeyDef.CLUB_TABLE_X_PREFIX + seqNo
            );

            if (null == strRoomId) {
                LOGGER.error(
                    "牌桌房间号为空, clubId = {}, seqNo = {}",
                    clubId, seqNo
                );
                return;
            }

            // 获取房间详情
            String strRoomDetailz = redisCache.hget(
                RedisKeyDef.ROOM_X_PREFIX + strRoomId,
                RedisKeyDef.ROOM_DETAILZ
            );

            if (null == strRoomDetailz) {
                // 这种情况可能是因为房间已经结束
                LOGGER.error(
                    "房间详情为空, clubId = {}, seqNo = {}, roomId = {}",
                    clubId, seqNo, strRoomId
                );
                return;
            }

            // 从 JSON 对象中创建牌桌
            JSONObject jsonObj = JSONObject.parseObject(strRoomDetailz);
            Table newTable = Table.fromJSON(jsonObj);

            // 设置业务结果
            resultX.setFinalResult(newTable);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
