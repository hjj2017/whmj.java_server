package org.mj.bizserver.cmdhandler.hall;

import org.mj.bizserver.allmsg.HallServerProtocol;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 加入房间指令处理器
 * <p>
 * XXX 注意: 该指令处理器将运行在游戏服务器进程中,
 * 因为 ProxyServer 在接到创建房间指令之后做了一个跳转操作!
 * 直接将 JoinRoomCmd 转发给游戏服务器...
 * 虽然看上去还是大厅的代码,
 * 但真正的运行进程是在游戏服务器上!
 */
public final class JoinRoomCmdHandler implements ICmdHandler<MyCmdHandlerContext, HallServerProtocol.JoinRoomCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(JoinRoomCmdHandler.class);

    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        HallServerProtocol.JoinRoomCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        // 获取房间 Id
        final int roomId = cmdObj.getRoomId();

        if (MJ_weihai_BizLogic.getInstance().hasRoom(roomId)) {
            // 威海麻将加入房间
            JoinRoomCmdHandler_MJ_weihai_.handle(ctx, cmdObj);
            return;
        }

        LOGGER.error(
            "无法加入的房间 Id, 房间不存在! userId = {}, roomId = {}",
            ctx.getFromUserId(), roomId
        );

        ctx.errorAndFlush(
            ErrorEnum.GAME__ROOM_NOT_EXIST.getErrorCode(),
            ErrorEnum.GAME__ROOM_NOT_EXIST.getErrorMsg()
        );

        // 发送一个空 Id 消息,
        // 客户端会通过这个消息撤掉加载窗口
        ctx.writeAndFlush(HallServerProtocol.JoinRoomResult.newBuilder()
            .setRoomId(-1)
            .setGameType0(-1)
            .setGameType1(-1)
            .build()
        );

        // （ 异步方式 ） 清理用户所在房间
        clearUserAtRoom_async(ctx.getFromUserId(), roomId);
    }

    /**
     * （ 异步方式 ） 清理用户所在房间
     *
     * @param userId   用户 Id
     * @param atRoomId 所在房间 Id
     */
    static private void clearUserAtRoom_async(int userId, int atRoomId) {
        if (userId <= 0 ||
            atRoomId <= 0) {
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定 Id
            userId,
            // 执行异步操作
            () -> {
                try (Jedis redisCache = RedisXuite.getRedisCache()) {
                    redisCache.hdel(
                        RedisKeyDef.USER_X_PREFIX + userId,
                        RedisKeyDef.USER_AT_ROOM_ID
                    );

                    redisCache.del(RedisKeyDef.ROOM_X_PREFIX + atRoomId);
                } catch (Exception ex) {
                    // 记录错误日志
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        );
    }
}
