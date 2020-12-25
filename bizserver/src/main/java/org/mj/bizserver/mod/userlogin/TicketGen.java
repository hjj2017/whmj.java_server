package org.mj.bizserver.mod.userlogin;

import org.mj.bizserver.def.RedisKeyDef;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * 登录票据生成器
 */
class TicketGen {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(TicketGen.class);

    /**
     * 私有化类默认构造器
     */
    private TicketGen() {
    }

    /**
     * 生成票据, 20 秒有效期
     *
     * @param userId 用户 Id
     * @return 票据
     */
    static String genTicket(int userId) {
        if (userId <= 0) {
            return null;
        }

        // 创建票据
        final String newTicket = UUID.randomUUID().toString();
        // 票据关键字
        final String redisKey = RedisKeyDef.TICKET_X_PREFIX + newTicket;

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 将票据保存到 Redis,
            // 20 秒有效期
            redisCache.set(redisKey, String.valueOf(userId));
            redisCache.pexpire(redisKey, 20000);

            return newTicket;
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return null;
    }
}
