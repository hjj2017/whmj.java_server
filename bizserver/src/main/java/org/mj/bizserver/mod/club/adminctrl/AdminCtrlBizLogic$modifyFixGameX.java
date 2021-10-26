package org.mj.bizserver.mod.club.adminctrl;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.base.AsyncOperationProcessorSingleton;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.IBizResultCallback;
import org.mj.bizserver.mod.club.adminctrl.dao.IClubDao;
import org.mj.bizserver.mod.club.adminctrl.dao.IClubMemberDao;
import org.mj.bizserver.mod.club.membercenter.bizdata.FixGameX;
import org.mj.bizserver.mod.club.membercenter.bizdata.RoleDef;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * ( 异步方式 ) 更新固定玩法
 */
interface AdminCtrlBizLogic$modifyFixGameX {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(AdminCtrlBizLogic$modifyFixGameX.class);

    /**
     * ( 异步方式 ) 更新固定玩法
     *
     * @param adminId  管理员 Id
     * @param clubId   亲友圈 Id
     * @param fixGameX 固定玩法
     * @param callback 回调函数
     */
    default void modifyFixGameX_async(
        final int adminId,
        final int clubId,
        final FixGameX fixGameX,
        final IBizResultCallback<Boolean> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Boolean>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

        if (adminId <= 0 ||
            clubId <= 0 ||
            null == fixGameX) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定 Id
            adminId,
            // 异步 IO 操作
            () -> modifyFixGameX(adminId, clubId, fixGameX, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 修改固定玩法
     *
     * @param adminId  管理员 Id
     * @param clubId   亲友圈 Id
     * @param fixGameX 固定玩法
     * @param resultX  业务结果
     */
    default void modifyFixGameX(
        final int adminId,
        final int clubId,
        final FixGameX fixGameX,
        final BizResultWrapper<Boolean> resultX) {
        if (null == resultX) {
            return;
        }

        if (adminId <= 0 ||
            clubId <= 0 ||
            null == fixGameX) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            // 获取亲友圈成员 DAO
            final IClubMemberDao clubMemberDao = sessionX.getMapper(IClubMemberDao.class);
            // 获取用户在亲友圈中的角色
            final int roleInt = clubMemberDao.getRole(adminId, clubId);

            if (RoleDef.SUPER_ADMIN.getIntVal() != roleInt) {
                LOGGER.error(
                    "用户不是亲友圈管理员, adminId = {}, clubId = {}",
                    adminId, clubId
                );
                return;
            }

            final int index = fixGameX.getIndex();
            final String jsonStr = fixGameX.toJSONStr();

            // 更新固定玩法
            sessionX.getMapper(IClubDao.class).updateFixGameX(
                clubId, index, jsonStr
            );

            try (Jedis redisCache = RedisXuite.getRedisCache()) {
                // 清理缓存数据
                redisCache.hdel(
                    RedisKeyDef.CLUB_X_PREFIX + clubId,
                    RedisKeyDef.CLUB_DETAILZ
                );
            }

            LOGGER.info(
                "已修改固定玩法, adminId = {}, clubId = {}, index = {}, fixGame = {}",
                adminId, clubId, index, jsonStr
            );

            resultX.setFinalResult(true);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
