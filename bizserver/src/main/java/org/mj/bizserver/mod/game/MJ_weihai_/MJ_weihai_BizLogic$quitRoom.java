package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 退出房间
 */
interface MJ_weihai_BizLogic$quitRoom {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$quitRoom.class);

    /**
     * ( 异步方式 ) 退出房间
     *
     * @param userId   用户 Id
     * @param callback 回调函数
     */
    default void quitRoom_async(int userId, IBizResultCallback<Boolean> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Boolean>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

        if (userId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        // 获取当前房间
        final Room currRoom = RoomGroup.getByUserId(userId);

        if (null == currRoom) {
            LOGGER.error(
                "房间不存在, userId = {}",
                userId
            );
            ErrorEnum.GAME__ROOM_NOT_EXIST.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        if (userId == currRoom.getOwnerId()) {
            LOGGER.error(
                "当前玩家是房主, 不能退出房间! ++( 房主只能解散房间 )++ userId = {}, atRoomId = {}",
                userId,
                currRoom.getRoomId()
            );
            ErrorEnum.GAME__ROOM_OWNER_CAN_NOT_QUIT.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定现成 Id
            currRoom.getRoomId(),
            // 异步 IO 操作
            () -> quitRoom(userId, resultX),
            // 回到主线程
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 退出房间
     *
     * @param userId  用户 Id
     * @param resultX 业务结果
     */
    default void quitRoom(int userId, BizResultWrapper<Boolean> resultX) {
        if (null == resultX) {
            return;
        }

        resultX.setFinalResult(false);

        if (userId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        // 根据房主 Id 获取房间
        Room currRoom = RoomGroup.getByUserId(userId);

        if (null == currRoom) {
            LOGGER.error(
                "房间不存在, userId = {}",
                userId
            );
            ErrorEnum.GAME__ROOM_NOT_EXIST.fillResultX(resultX);
            return;
        }

        if (userId == currRoom.getOwnerId()) {
            LOGGER.error(
                "当前玩家是房主, 不能退出房间! ++( 房主只能解散房间 )++ userId = {}, atRoomId = {}",
                userId,
                currRoom.getRoomId()
            );
            ErrorEnum.GAME__ROOM_OWNER_CAN_NOT_QUIT.fillResultX(resultX);
            return;
        }

        // 根据用户 Id 移除玩家
        Player targetPlayer = currRoom.removePlayerByUserId(userId, false);

        if (null == targetPlayer) {
            LOGGER.error(
                "用户不在当前房间中, userId = {}, atRoomId = {}",
                userId,
                currRoom.getRoomId()
            );
            ErrorEnum.GAME__NOT_AT_THE_ROOM.fillResultX(resultX);
            return;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            redisCache.hdel(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_AT_ROOM_ID
            );
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        MJ_weihai_BizLogic.getInstance().syncRoomKeyDataToRedis(currRoom);
        MJ_weihai_BizLogic.getInstance().broadcastAClubTableChanged(currRoom);

        resultX.setFinalResult(true);
    }
}
