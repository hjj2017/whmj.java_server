package org.mj.bizserver.mod.club.adminctrl;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.club.adminctrl.dao.IClubDao;
import org.mj.bizserver.mod.club.adminctrl.dao.IClubMemberDao;
import org.mj.bizserver.mod.club.membercenter.bizdata.RoleDef;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * ( 异步方式 ) 给亲友圈充值房卡
 */
interface AdminCtrlBizLogic$exchangeRoomCard {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(AdminCtrlBizLogic$exchangeRoomCard.class);

    /**
     * ( 异步方式 ) 给亲友圈充值房卡
     *
     * @param adminId    管理员 Id
     * @param clubId     亲友圈 Id
     * @param exRoomCard 充值房卡数量
     * @param callback   回调函数
     */
    default void exchangeRoomCard_async(
        final int adminId, final int clubId, final int exRoomCard,
        final IBizResultCallback<Boolean> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Boolean>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

        if (adminId <= 0 ||
            clubId <= 0 ||
            exRoomCard <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定 Id
            adminId,
            // 异步 IO 操作
            () -> exchangeRoomCard(adminId, clubId, exRoomCard, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 给亲友圈充值房卡
     *
     * @param adminId    管理员 Id
     * @param clubId     亲友圈 Id
     * @param exRoomCard 充值房卡数量
     * @param resultX    业务结果
     */
    default void exchangeRoomCard(
        final int adminId,
        final int clubId,
        final int exRoomCard,
        final BizResultWrapper<Boolean> resultX) {
        if (null == resultX) {
            return;
        }

        resultX.setFinalResult(false);

        if (adminId <= 0 ||
            clubId <= 0 ||
            exRoomCard <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            // 获取亲友圈成员 DAO
            final IClubMemberDao clubMemberDao = sessionX.getMapper(IClubMemberDao.class);
            // 获取用户在亲友圈中的角色
            final Integer roleInt = clubMemberDao.getRole(adminId, clubId);

            if (null == roleInt ||
                RoleDef.SUPER_ADMIN.getIntVal() != roleInt) {
                ErrorEnum.CLUB__YOU_NOT_SUPER_ADMIN.fillResultX(resultX);
                LOGGER.error(
                    "用户不是亲友圈超级管理员, adminId = {}, clubId = {}",
                    adminId, clubId
                );
                return;
            }

            // 获取用户当前房卡数量
            int currRoomCard = getUserRoomCard(adminId);

            if (currRoomCard < exRoomCard) {
                ErrorEnum.CLUB__EXCHANGE_ROOM_CARD_BUT_NOT_ENOUGH.fillResultX(resultX);
                LOGGER.error(
                    "亲友圈充值房卡失败, 房卡数量不足! adminId = {}, clubId = {}, currRoomCard = {}, exRoomCard = {}",
                    adminId, clubId, currRoomCard, exRoomCard
                );
                return;
            }

            // 修改房卡数量
            UserInfoBizLogic.getInstance().costRoomCard(adminId, exRoomCard, null);
            sessionX.getMapper(IClubDao.class).addRoomCard(adminId, clubId, exRoomCard);

            try (Jedis redisCache = RedisXuite.getRedisCache()) {
                // 清理缓存数据
                redisCache.hdel(
                    RedisKeyDef.CLUB_X_PREFIX + clubId,
                    RedisKeyDef.CLUB_DETAILZ
                );

                // 清理用户详情
                redisCache.hdel(
                    RedisKeyDef.USER_X_PREFIX + adminId,
                    RedisKeyDef.USER_DETAILZ
                );
            }

            LOGGER.info(
                "亲友圈充值房卡成功, adminId = {}, clubId = {}, exRoomCard = {}",
                adminId, clubId, exRoomCard
            );

            resultX.setFinalResult(true);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取用户房卡数量
     *
     * @param userId 用户 Id
     * @return 房卡数量
     */
    static private int getUserRoomCard(int userId) {
        if (userId <= 0) {
            return 0;
        }

        BizResultWrapper<UserDetailz> resultA = new BizResultWrapper<>();
        UserInfoBizLogic.getInstance().getUserDetailzByUserId(userId, resultA);
        UserDetailz userDetailz = resultA.getFinalResult();

        return (null == userDetailz) ? 0 : userDetailz.getRoomCard();
    }
}
