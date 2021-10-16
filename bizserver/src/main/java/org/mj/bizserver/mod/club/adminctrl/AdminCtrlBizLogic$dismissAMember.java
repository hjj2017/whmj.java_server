package org.mj.bizserver.mod.club.adminctrl;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.club.adminctrl.dao.IClubDao;
import org.mj.bizserver.mod.club.adminctrl.dao.IClubMemberDao;
import org.mj.bizserver.mod.club.membercenter.bizdata.ClubStateEnum;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberStateEnum;
import org.mj.bizserver.mod.club.membercenter.bizdata.RoleDef;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * ( 异步方式 ) 开除一个成员,
 * XXX 注意: 当开除最后一个成员时, 亲友圈自动解散
 */
interface AdminCtrlBizLogic$dismissAMember {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(AdminCtrlBizLogic$dismissAMember.class);

    /**
     * ( 异步方式 ) 开除一个成员
     *
     * @param adminId  管理员 Id
     * @param memberId 成员 Id
     * @param clubId   亲友圈 Id
     * @param callback 回调函数
     */
    default void dismissAMember_async(
        final int adminId, final int memberId, final int clubId,
        final IBizResultCallback<Boolean> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Boolean>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

        if (adminId <= 0 ||
            memberId <= 0 ||
            clubId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定 Id
            adminId,
            // 异步 IO 操作
            () -> dismissAMember(adminId, memberId, clubId, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 开除一个成员
     *
     * @param adminId  管理员 Id
     * @param memberId 成员 Id
     * @param clubId   亲友圈 Id
     * @param resultX  业务结果
     */
    default void dismissAMember(
        final int adminId,
        final int memberId,
        final int clubId,
        final BizResultWrapper<Boolean> resultX) {

        if (null == resultX) {
            return;
        }

        resultX.setFinalResult(true);

        if (adminId <= 0 ||
            memberId <= 0 ||
            clubId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            // 获取亲友圈成员 DAO
            final IClubMemberDao clubMemberDao = sessionX.getMapper(IClubMemberDao.class);
            // 获取用户在亲友圈中的角色
            final int roleInt = clubMemberDao.getRole(adminId, clubId);

            if (RoleDef.SUPER_ADMIN.getIntVal() != roleInt &&
                RoleDef.ADMIN.getIntVal() != roleInt) {
                LOGGER.error(
                    "用户不是亲友圈管理员, adminId = {}, clubId = {}",
                    adminId, clubId
                );
                return;
            }

            if (memberId == adminId) {
                // 如果是自己开除自己,
                // 那么如果亲友圈里已经没有其他成员,
                // 则可以执行...
                int memberCount = clubMemberDao.getMemberCountByState(
                    clubId, MemberStateEnum.NORMAL.getIntVal(), MemberStateEnum.WAITING_FOR_REVIEW.getIntVal()
                );

                if (memberCount > 1) {
                    LOGGER.error(
                        "还有其他未开除的成员, 现在还不能开除自己! clubId = {}, adminId = {}",
                        clubId, adminId
                    );
                    return;
                }
            }

            clubMemberDao.updateCurrState(
                memberId, clubId, MemberStateEnum.DISMISS.getIntVal()
            );

            final IClubDao clubDao = sessionX.getMapper(IClubDao.class);
            clubDao.updateNumOfPeople(clubId);

            LOGGER.error(
                "亲友圈成员被开除, adminId = {}, memberId = {}, clubId = {}",
                adminId, memberId, clubId
            );

            try (Jedis redisCache = RedisXuite.getRedisCache()) {
                // 获取成员数量
                int memberCount = clubMemberDao.getMemberCountByState(
                    clubId, MemberStateEnum.NORMAL.getIntVal(), MemberStateEnum.WAITING_FOR_REVIEW.getIntVal()
                );

                if (memberCount <= 0) {
                    LOGGER.error(
                        "亲友圈全部成员都被开除, 亲友圈解散! clubId = {}",
                        clubId
                    );

                    // 标记已解散状态
                    clubDao.updateCurrState(
                        clubId, ClubStateEnum.DISSOLVED.getIntVal()
                    );

                    // 删除 Redis 缓存
                    redisCache.del(RedisKeyDef.CLUB_X_PREFIX + clubId);
                    return;
                }

                // 清理缓存数据
                redisCache.hdel(
                    RedisKeyDef.CLUB_X_PREFIX + clubId,
                    RedisKeyDef.CLUB_DETAILZ,
                    RedisKeyDef.CLUB_MEMBER_ID_ARRAY,
                    RedisKeyDef.CLUB_MEMBER_INFO_X_PREFIX + memberId
                );

                // 清理用户已经加入的亲友圈 Id 数组
                redisCache.hdel(
                    RedisKeyDef.USER_X_PREFIX + memberId,
                    RedisKeyDef.USER_JOINED_CLUB_ID_ARRAY
                );
            }

            resultX.setFinalResult(true);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
