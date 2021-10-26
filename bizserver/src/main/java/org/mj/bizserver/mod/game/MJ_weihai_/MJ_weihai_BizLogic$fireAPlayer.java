package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.base.AsyncOperationProcessorSingleton;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.IBizResultCallback;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 踢出一个玩家
 */
interface MJ_weihai_BizLogic$fireAPlayer {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$fireAPlayer.class);

    /**
     * ( 异步方式 ) 踢出一个玩家
     *
     * @param roomOwnerId  房主用户 Id
     * @param targetUserId 目标用户 Id
     * @param callback     回调函数
     */
    default void fireAPlayer_async(
        int roomOwnerId, int targetUserId, IBizResultCallback<Boolean> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Boolean>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

        if (roomOwnerId <= 0 ||
            targetUserId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        // 获取当前房间
        final Room currRoom = RoomGroup.getByUserId(roomOwnerId);

        if (null == currRoom) {
            LOGGER.error(
                "房间不存在, roomOwnerId = {}",
                roomOwnerId
            );
            ErrorEnum.GAME__ROOM_NOT_EXIST.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        if (roomOwnerId != currRoom.getOwnerId()) {
            LOGGER.error(
                "当前玩家不是房主, 不能踢出目标玩家, roomOwnerId = {}, atRoomId = {}",
                roomOwnerId,
                currRoom.getRoomId()
            );
            ErrorEnum.GAME__IS_NOT_ROOM_OWNER.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定现成 Id
            currRoom.getRoomId(),
            // 异步 IO 操作
            () -> fireAPlayer(roomOwnerId, targetUserId, resultX),
            // 回到主线程
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 踢出一个玩家
     *
     * @param roomOwnerId  房主 Id
     * @param targetUserId 目标用户 Id
     * @param resultX      业务结果
     */
    default void fireAPlayer(int roomOwnerId, int targetUserId, BizResultWrapper<Boolean> resultX) {
        if (null == resultX) {
            return;
        }

        resultX.setFinalResult(false);

        if (roomOwnerId <= 0 ||
            targetUserId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        if (roomOwnerId == targetUserId) {
            LOGGER.error(
                "房主不能踢出自己, roomOwnerId = {}",
                roomOwnerId
            );
            return;
        }

        // 根据房主 Id 获取房间
        Room currRoom = RoomGroup.getByUserId(roomOwnerId);

        if (null == currRoom) {
            LOGGER.error(
                "房间不存在, roomOwnerId = {}",
                roomOwnerId
            );
            ErrorEnum.GAME__ROOM_NOT_EXIST.fillResultX(resultX);
            return;
        }

        if (roomOwnerId != currRoom.getOwnerId()) {
            LOGGER.error(
                "当前玩家不是房主, 不能踢出目标玩家, roomOwnerId = {}, atRoomId = {}",
                roomOwnerId,
                currRoom.getRoomId()
            );
            ErrorEnum.GAME__IS_NOT_ROOM_OWNER.fillResultX(resultX);
            return;
        }

        if (currRoom.isOfficialStarted()) {
            LOGGER.error(
                "游戏已经正式开始, 不能踢出其他用户! roomOwnerId = {}, atRoomId = {}",
                roomOwnerId,
                currRoom.getRoomId()
            );
            ErrorEnum.GAME__IS_OFFICIAL_STARTED.fillResultX(resultX);
            return;
        }

        // 根据用户 Id 移除玩家
        Player targetPlayer = currRoom.removePlayerByUserId(targetUserId, true);

        if (null == targetPlayer) {
            LOGGER.error(
                "目标用户不在当前房间中, targetUserId = {}, atRoomId = {}",
                targetUserId,
                currRoom.getRoomId()
            );
            return;
        }

        LOGGER.info(
            "目标用户被踢出房间, roomOwnerId = {}, targetUserId = {}, atRoomId = {}",
            roomOwnerId, targetPlayer, currRoom.getRoomId()
        );

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            redisCache.hdel(
                RedisKeyDef.USER_X_PREFIX + targetUserId,
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
