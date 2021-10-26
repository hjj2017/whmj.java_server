package org.mj.bizserver.mod.userinfo;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.userinfo.dao.IUserDao;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 消耗房卡
 */
interface UserInfoBizLogic$costRoomCard {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(UserInfoBizLogic$costRoomCard.class);

    /**
     * 消耗房卡
     *
     * @param userId   用户 Id
     * @param deltaVal 变化值
     * @param resultX  业务结果
     */
    default void costRoomCard(int userId, int deltaVal, BizResultWrapper<Boolean> resultX) {
        if (userId <= 0 ||
            deltaVal <= 0) {
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            // 消耗房卡
            final int effectRowNum = sessionX.getMapper(IUserDao.class).costRoomCard(userId, deltaVal);

            if (null != resultX) {
                resultX.setFinalResult(effectRowNum > 0);
            }

            try (Jedis redisCache = RedisXuite.getRedisCache()) {
                redisCache.hdel(
                    RedisKeyDef.USER_X_PREFIX + userId,
                    RedisKeyDef.USER_DETAILZ
                );
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
