package org.mj.bizserver.mod.club.membercenter;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.club.membercenter.dao.IClubDao;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 亲友圈消耗房卡
 */
interface MemberCenterBizLogic$costRoomCard {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MemberCenterBizLogic$costRoomCard.class);

    /**
     * 消耗房卡
     *
     * @param clubId   亲友圈 Id
     * @param deltaVal 变化值
     * @param resultX  业务结果
     */
    default void costRoomCard(
        int clubId, int deltaVal, BizResultWrapper<Boolean> resultX) {
        if (clubId <= 0 ||
            deltaVal <= 0) {
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            // 消耗房卡
            final int effectRowNum = sessionX.getMapper(IClubDao.class).costRoomCard(clubId, deltaVal);

            if (null != resultX) {
                resultX.setFinalResult(effectRowNum > 0);
            }

            try (Jedis redisCache = RedisXuite.getRedisCache()) {
                redisCache.hdel(
                    RedisKeyDef.CLUB_X_PREFIX + clubId,
                    RedisKeyDef.CLUB_DETAILZ
                );
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
