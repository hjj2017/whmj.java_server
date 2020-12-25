package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.def.RedisKeyDef;
import org.mj.comm.util.DLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 房间 Id 生成器
 */
final class RoomIdGen {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RoomIdGen.class);

    /**
     * 随即对象
     */
    static private final Random RAND = new Random();

    /**
     * 私有化类默认构造器
     */
    private RoomIdGen() {
    }

    /**
     * 生成房间 Id
     *
     * @return 房间 Id
     */
    static int newId() {
        for (int i = 0; i < 8; i++) {
            // 如果房间 Id 发生重复,
            // 则重新试一次!
            // 但是最多就只能尝试 8 次...
            //
            // 随机一个新的 Id
            int newId = 100000 + RAND.nextInt(899999);

            try (DLock newLocker = DLock.newLock(RedisKeyDef.ROOM_X_PREFIX + newId)) {
                // 首先尝试加异步锁,
                if (null == newLocker ||
                    !newLocker.tryLock(5000)) {
                    // 如果加锁失败,
                    continue;
                }

                return newId;
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        return -1;
    }
}
