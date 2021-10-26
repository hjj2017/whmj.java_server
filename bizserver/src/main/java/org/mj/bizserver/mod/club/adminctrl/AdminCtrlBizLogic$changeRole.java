package org.mj.bizserver.mod.club.adminctrl;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.base.AsyncOperationProcessorSingleton;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.IBizResultCallback;
import org.mj.bizserver.mod.club.adminctrl.dao.IClubMemberDao;
import org.mj.bizserver.mod.club.membercenter.bizdata.RoleDef;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * ( 异步方式 ) 修改角色
 */
interface AdminCtrlBizLogic$changeRole {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(AdminCtrlBizLogic$changeRole.class);

    /**
     * ( 异步方式 ) 修改角色
     *
     * @param adminId  管理员 Id
     * @param memberId 成员 Id
     * @param clubId   亲友圈 Id
     * @param newRole  新角色
     * @param callback 回调函数
     */
    default void changeRole_async(
        int adminId, int memberId, int clubId, RoleDef newRole, final IBizResultCallback<Boolean> callback) {
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
            () -> changeRole(adminId, memberId, clubId, newRole, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 修改角色
     *
     * @param adminId  管理员 Id
     * @param memberId 成员 Id
     * @param clubId   亲友圈 Id
     * @param newRole  角色
     * @param resultX  业务结果
     */
    default void changeRole(
        final int adminId,
        final int memberId,
        final int clubId,
        RoleDef newRole,
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

        if (null == newRole) {
            newRole = RoleDef.MEMBER;
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

            // 修改角色
            clubMemberDao.updateRole(memberId, clubId, newRole.getIntVal());

            try (Jedis redisCache = RedisXuite.getRedisCache()) {
                // 清理缓存数据
                redisCache.hdel(
                    RedisKeyDef.CLUB_X_PREFIX + clubId,
                    RedisKeyDef.CLUB_MEMBER_INFO_X_PREFIX + memberId
                );
            }

            LOGGER.info(
                "修改角色成功, adminId = {}, memberId = {}, clubId = {}, role = {}",
                adminId, memberId, clubId,
                newRole.getIntVal()
            );

            resultX.setFinalResult(true);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
