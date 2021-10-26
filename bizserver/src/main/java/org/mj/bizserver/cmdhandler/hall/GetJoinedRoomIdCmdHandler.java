package org.mj.bizserver.cmdhandler.hall;

import org.mj.bizserver.allmsg.HallServerProtocol;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.base.AsyncOperationProcessorSingleton;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 获取已经加入的房间 Id
 * XXX 注意: 该指令处理器将运行在大厅服务器进程中
 */
public class GetJoinedRoomIdCmdHandler implements ICmdHandler<MyCmdHandlerContext, HallServerProtocol.GetJoinedRoomIdCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GetJoinedRoomIdCmdHandler.class);

    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        HallServerProtocol.GetJoinedRoomIdCmd cmdObj) {

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定 Id
            ctx.getFromUserId(),
            // 执行异步操作
            () -> buildResultMsgAndSend(
                ctx, // 信道处理器上下文
                getUserAtRoomId(ctx.getFromUserId())
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx          信道处理器上下文
     * @param userAtRoomId 用户所在房间 Id
     */
    static private void buildResultMsgAndSend(MyCmdHandlerContext ctx, int userAtRoomId) {
        if (null == ctx) {
            return;
        }

        // 构建结果对象
        HallServerProtocol.GetJoinedRoomIdResult.Builder b = HallServerProtocol.GetJoinedRoomIdResult.newBuilder();
        b.setRoomId(userAtRoomId);

        HallServerProtocol.GetJoinedRoomIdResult r = b.build();

        ctx.writeAndFlush(r);
    }

    /**
     * 获取用户所在房间 Id
     *
     * @param userId 用户 Id
     * @return 所在房间 Id
     */
    static private int getUserAtRoomId(int userId) {
        if (userId <= 0) {
            return -1;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 获取用户所在房间 Id
            String userAtRoomId = redisCache.hget(
                RedisKeyDef.USER_X_PREFIX + userId, RedisKeyDef.USER_AT_ROOM_ID
            );

            int atRoomId = -1;

            if (null != userAtRoomId) {
                atRoomId = Integer.parseInt(userAtRoomId);
            }

            if (redisCache.hexists(
                RedisKeyDef.ROOM_X_PREFIX + atRoomId,
                RedisKeyDef.ROOM_AT_SERVER_ID)) {
                return atRoomId;
            }

            // 如果房间 Id 已经不存在,
            // 则清理 Redis 缓存
            redisCache.hdel(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_AT_ROOM_ID
            );

            redisCache.del(RedisKeyDef.ROOM_X_PREFIX + atRoomId);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return -1;
    }
}
