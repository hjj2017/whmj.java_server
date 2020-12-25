package org.mj.bizserver.mod.club.membercenter;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberStateEnum;
import org.mj.bizserver.mod.club.membercenter.dao.IClubDao;
import org.mj.bizserver.mod.club.membercenter.dao.IClubMemberDao;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * ( 异步方式 ) 退出亲友圈
 */
interface MemberCenterBizLogic$quitClub {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MemberCenterBizLogic$quitClub.class);

    /**
     * ( 异步方式 ) 退出亲友圈
     *
     * @param userId 用户 Id
     * @param clubId 亲友圈 Id
     */
    default void quitClub_async(
        final int userId,
        final int clubId,
        final IBizResultCallback<Boolean> callback) {

        // 确保回调函数不为空
        final IBizResultCallback<Boolean>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

        if (clubId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定 Id
            clubId,
            // 异步 IO 操作
            () -> resultX.setFinalResult(quitClub(userId, clubId)),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 退出亲友圈
     *
     * @param userId 用户 Id
     * @param clubId 亲友圈 Id
     * @return true = 退出成功, false = 退出失败
     */
    private boolean quitClub(final int userId, final int clubId) {
        if (userId <= 0 ||
            clubId <= 0) {
            return false;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache();
             SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            // 获取亲友圈成员 DAO
            sessionX.getMapper(IClubMemberDao.class).changeCurrState(
                userId, clubId,
                MemberStateEnum.QUIT.getIntVal()
            );

            // 更新亲友圈人数
            sessionX.getMapper(IClubDao.class).updateNumOfPeople(clubId);

            // 清理 Redis 缓存
            redisCache.hdel(
                RedisKeyDef.CLUB_X_PREFIX + clubId,
                RedisKeyDef.CLUB_MEMBER_ID_ARRAY,
                RedisKeyDef.CLUB_MEMBER_INFO_X_PREFIX + userId
            );

            redisCache.hdel(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_JOINED_CLUB_ID_ARRAY
            );

            return true;
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return false;
    }
}
