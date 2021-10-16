package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加入房间
 */
interface MJ_weihai_BizLogic$joinRoom {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$joinRoom.class);

    /**
     * ( 异步方式 ) 加入房间
     *
     * @param userId   用户 Id
     * @param roomId   房间 Id
     * @param callback 回调函数
     */
    default void joinRoom_async(int userId, int roomId, IBizResultCallback<Room> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Room>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        final BizResultWrapper<Room> resultX = new BizResultWrapper<>();

        if (userId <= 0 ||
            roomId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定线程 Id
            roomId,
            // 异步 IO 操作
            () -> joinRoom(userId, roomId, resultX),
            // 回到主线程
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 加入房间
     *
     * @param userId  用户 Id
     * @param roomId  房间 Id
     * @param resultX 结果对象
     */
    default void joinRoom(int userId, int roomId, BizResultWrapper<Room> resultX) {
        if (userId <= 0 ||
            roomId <= 0) {
            return;
        }

        if (null == resultX) {
            resultX = new BizResultWrapper<>();
        }

        // 获取已经加入的房间
        final Room alreadyJoinedRoom = RoomGroup.getByUserId(userId);

        if (null != alreadyJoinedRoom) {
            if (alreadyJoinedRoom.getRoomId() == roomId) {
                // 用户已加入房间
                resultX.setFinalResult(alreadyJoinedRoom);
            } else {
                // 已加入其他房间
                LOGGER.error(
                    "用户已加入其他房间, userId = {}, alreadyJoinedRoomId = {}, expectRoomId = {}",
                    userId,
                    alreadyJoinedRoom.getRoomId(),
                    roomId
                );
                ErrorEnum.GAME__OTHER_ROOM_HAS_BEEN_JOINED.fillResultX(resultX);
            }

            return;
        }

        // 期望房间
        final Room expectRoom = RoomGroup.getByRoomId(roomId);

        if (null == expectRoom) {
            LOGGER.error(
                "房间不存在, userId = {}, atRoomId = {}",
                userId, roomId
            );
            ErrorEnum.GAME__ROOM_NOT_EXIST.fillResultX(resultX);
            return;
        }

        if (expectRoom.isDeniedUserId(userId)) {
            LOGGER.error(
                "房主已拒绝您加入房间, userId = {}, atRoomId = {}",
                userId, roomId
            );
            ErrorEnum.GAME__ROOM_CURR_USER_IS_DENIED.fillResultX(resultX);
            return;
        }

        // 获取当前玩家
        Player currPlayer = expectRoom.getPlayerByUserId(userId);

        if (null != currPlayer) {
            // 如果玩家已经加入房间,
            resultX.setFinalResult(expectRoom);
            return;
        }

        if (!RoomCardCashier.hasEnoughRoomCard(
            userId, expectRoom)) {
            LOGGER.error(
                "无法加入房间, 房卡数量验证失败! userId = {}, roomId = {}, clubId = {}, ++( 请排查: 1、用户房卡数量; 2、亲友圈房卡数量; 3、用户是否加入了该亲友圈 )++",
                userId, expectRoom.getRoomId(), expectRoom.getClubId()
            );
            ErrorEnum.GAME__ROOM_CARD_NOT_ENOUGH.fillResultX(resultX);
            return;
        }

        // 创建玩家
        currPlayer = createPlayer(userId);

        if (null == currPlayer) {
            LOGGER.error(
                "创建玩家失败, userId = {}",
                userId
            );
            ErrorEnum.INTERNAL_SERVER_ERROR.fillResultX(resultX);
            return;
        }

        if (-1 == expectRoom.playerSitDown(currPlayer)) {
            // 如果找了一圈没有找到空位,
            // 则说明房间已经坐满
            LOGGER.error(
                "房间已坐满, userId = {}, roomId = {}",
                currPlayer.getUserId(),
                expectRoom.getRoomId()
            );
            ErrorEnum.GAME__THE_ROOM_IS_FULL.fillResultX(resultX);
            return;
        }

        LOGGER.info(
            "用户落座, userId = {}, roomId = {}, seatIndex = {}",
            currPlayer.getUserId(),
            expectRoom.getRoomId(),
            currPlayer.getSeatIndex()
        );
        resultX.setFinalResult(expectRoom);

        // 设置房主标志
        currPlayer.setRoomOwner(
            expectRoom.getOwnerId() == currPlayer.getUserId()
        );

        // 同步房间数据
        MJ_weihai_BizLogic.getInstance().syncRoomKeyDataToRedis(expectRoom);
        MJ_weihai_BizLogic.getInstance().broadcastAClubTableChanged(expectRoom);
    }

    /**
     * 创建玩家对象
     *
     * @param userId 用户 Id
     * @return 玩家对象
     */
    private Player createPlayer(int userId) {
        if (userId <= 0) {
            return null;
        }

        BizResultWrapper<UserDetailz> resultX = new BizResultWrapper<>();

        // 获取用户详情
        UserInfoBizLogic.getInstance().getUserDetailzByUserId(
            userId, resultX
        );

        UserDetailz userDetailz = resultX.getFinalResult();

        if (null == userDetailz) {
            return null;
        }

        // 创建玩家对象
        Player newPlayer = new Player(userId);
        newPlayer.setUserName(userDetailz.getUserName());
        newPlayer.setHeadImg(userDetailz.getHeadImg());
        newPlayer.setSex(userDetailz.getSex());
        newPlayer.setClientIpAddr(userDetailz.getLastLoginIp());

        return newPlayer;
    }
}
