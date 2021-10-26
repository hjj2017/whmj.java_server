package org.mj.bizserver.mod.userinfo;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.base.AsyncOperationProcessorSingleton;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.IBizResultCallback;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.bizserver.mod.userinfo.dao.IUserDao;
import org.mj.bizserver.mod.userinfo.dao.UserEntity;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 根据用户 Id 获取用户详情
 */
interface UserInfoBizLogic$getUserDetailzByUserId {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(UserInfoBizLogic$getUserDetailzByUserId.class);

    /**
     * ( 异步方式 ) 根据用户 Id 获取用户详情
     *
     * @param userId   用户 Id
     * @param callback 回调函数
     */
    default void getUserDetailzByUserId_async(int userId, IBizResultCallback<UserDetailz> callback) {
        final IBizResultCallback<UserDetailz>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        final BizResultWrapper<UserDetailz> resultX = new BizResultWrapper<>();

        if (userId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定线程 Id
            userId,
            // 异步 IO 操作
            () -> getUserDetailzByUserId(userId, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 根据用户 Id 获取用户详情
     *
     * @param userId  用户 Id
     * @param resultX 业务结果
     */
    default void getUserDetailzByUserId(int userId, BizResultWrapper<UserDetailz> resultX) {
        if (userId <= 0 ||
            null == resultX) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 获取用户详情字符串
            String jsonStr = redisCache.hget(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_DETAILZ
            );

            if (null != jsonStr) {
                UserDetailz userDetailz = UserDetailz.fromEntity(UserEntity.fromJSONStr(jsonStr));

                if (null != userDetailz) {
                    resultX.setFinalResult(userDetailz);
                    return;
                }
            }

            try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
                // 获取用户实体
                final UserEntity userEntity = sessionX.getMapper(IUserDao.class).getEntityByUserId(userId);

                if (null == userEntity) {
                    LOGGER.error("用户不存在, userId = {}", userId);
                    resultX.setFinalResult(null);
                    return;
                }

                redisCache.hset(
                    RedisKeyDef.USER_X_PREFIX + userId,
                    RedisKeyDef.USER_DETAILZ,
                    JSONObject.toJSONString(userEntity)
                );

                resultX.setFinalResult(UserDetailz.fromEntity(userEntity));
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
            ErrorEnum.INTERNAL_SERVER_ERROR.fillResultX(resultX);
        }
    }
}
