package org.mj.bizserver.mod.club.adminctrl;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.club.adminctrl.dao.IClubDao;
import org.mj.bizserver.mod.club.adminctrl.dao.IClubMemberDao;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberStateEnum;
import org.mj.bizserver.mod.club.membercenter.bizdata.RoleDef;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * ( 异步方式 ) 同意加入
 */
interface AdminCtrlBizLogic$approvalToJoin {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(AdminCtrlBizLogic$approvalToJoin.class);

    /**
     * ( 异步方式 ) 同意加入
     *
     * @param adminId  管理员 Id
     * @param memberId 成员 Id
     * @param clubId   亲友圈 Id
     * @param yesOrNo  同意或拒绝
     * @param callback 回调函数
     */
    default void approvalToJoin_async(
        final int adminId, final int memberId, final int clubId, final boolean yesOrNo,
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

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定 Id
            adminId,
            // 异步 IO 操作
            () -> approvalToJoin(adminId, memberId, clubId, yesOrNo, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 同意加入
     *
     * @param adminId  用户 Id
     * @param memberId 成员 Id
     * @param clubId   亲友圈 Id
     * @param yesOrNo  同意或拒绝
     * @param resultX  业务结果
     */
    default void approvalToJoin(
        final int adminId,
        final int memberId,
        final int clubId,
        final boolean yesOrNo,
        final BizResultWrapper<Boolean> resultX) {

        if (null == resultX) {
            return;
        }

        resultX.setFinalResult(false);

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
                ErrorEnum.CLUB__YOU_NOT_ADMIN.fillResultX(resultX);
                return;
            }

            // 当前状态
            final int currState = yesOrNo
                ? MemberStateEnum.NORMAL.getIntVal()
                : MemberStateEnum.REJECT.getIntVal();

            // 更新当前状态
            clubMemberDao.updateCurrState(
                memberId, clubId, currState
            );

            sessionX.getMapper(IClubDao.class).updateNumOfPeople(clubId);

            try (Jedis redisCache = RedisXuite.getRedisCache()) {
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

            LOGGER.info(
                "{} 用户加入亲友圈, adminId = {}, memberId = {}, clubId = {}",
                yesOrNo ? "同意" : "拒绝",
                adminId, memberId, clubId
            );

            resultX.setFinalResult(true);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
