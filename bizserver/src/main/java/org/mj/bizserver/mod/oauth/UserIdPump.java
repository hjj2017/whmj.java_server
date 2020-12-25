package org.mj.bizserver.mod.oauth;

import org.mj.bizserver.def.RedisKeyDef;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 用户 Id 泵
 */
final class UserIdPump {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(UserIdPump.class);

    /**
     * 私有化类默认构造器
     */
    private UserIdPump() {
    }

    /**
     * 从用户 Id 泵中弹出一个用户 Id!
     * XXX 注意: 用户 Id 泵需要运行 devdoc/tool/gen_user_id.py 脚本,
     * 需要克隆 devdoc 代码库
     *
     * @return 用户 Id
     */
    static int popUpUserId() {
        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 从用户 Id 泵中弹出一个用户 Id
            String strUserId = redisCache.lpop(RedisKeyDef.USER_ID_PUMP);

            if (null == strUserId) {
                LOGGER.error("用户 Id 泵失效, 弹出用户 Id 为空");
                return -1;
            }

            return Integer.parseInt(strUserId);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
            return -1;
        }
    }
}
