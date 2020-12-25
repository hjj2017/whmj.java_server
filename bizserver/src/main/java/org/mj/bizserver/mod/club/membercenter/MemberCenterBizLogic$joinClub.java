package org.mj.bizserver.mod.club.membercenter;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.club.membercenter.bizdata.ClubDetailz;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberInfo;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberStateEnum;
import org.mj.bizserver.mod.club.membercenter.bizdata.RoleDef;
import org.mj.bizserver.mod.club.membercenter.dao.ClubMemberEntity;
import org.mj.bizserver.mod.club.membercenter.dao.IClubMemberDao;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.OutParam;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * ( 异步方式 ) 加入亲友圈
 */
interface MemberCenterBizLogic$joinClub {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MemberCenterBizLogic$joinClub.class);

    /**
     * ( 异步方式 ) 加入亲友圈
     *
     * @param userId   用户 Id
     * @param clubId   亲友圈 Id
     * @param callback 回调函数
     */
    default void joinClub_async(
        final int userId,
        final int clubId,
        final IBizResultCallback<Boolean> callback) {

        // 确保回调函数不为空
        final IBizResultCallback<Boolean>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

        if (userId <= 0 ||
            clubId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定 Id
            clubId,
            // 异步 IO 操作
            () -> joinClub(userId, clubId, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 加入亲友圈
     *
     * @param userId  用户 Id
     * @param clubId  亲友圈 Id
     * @param resultX 业务结果
     */
    default void joinClub(
        final int userId, final int clubId, BizResultWrapper<Boolean> resultX) {
        if (userId <= 0 ||
            clubId <= 0 ||
            null == resultX) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        // 默认返回结果 = false
        resultX.setFinalResult(false);
        // ( 输出参数 ) MySql 会话
        final OutParam<SqlSession> out_mySqlSession = new OutParam<>();

        try (Jedis redisCache = RedisXuite.getRedisCache();
             SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            // 设置输出参数
            out_mySqlSession.setVal(sessionX);
            // 获取当前亲友圈
            ClubDetailz currClub = MemberCenterBizLogic$getClubDetailz.getClubDetailzById(
                clubId, redisCache, out_mySqlSession
            );

            if (null == currClub) {
                LOGGER.error(
                    "亲友圈不存在, clubId = {}",
                    clubId
                );
                ErrorEnum.CLUB__THE_CLUB_NOT_EXIST.fillResultX(resultX);
                return;
            }

            // 获取已经加入的亲友圈 Id 列表
            MemberInfo validMember = MemberInfoGetter.getValidMemberInfo(
                userId, clubId, redisCache, out_mySqlSession
            );

            if (null != validMember) {
                // 如果已经加入亲友圈,
                LOGGER.warn(
                    "已经加入该亲友圈, userId = {}, clubId = {}",
                    userId, clubId
                );
                ErrorEnum.CLUB__ALREADY_JOINED.fillResultX(resultX);
                return;
            }

            // 获取用户详情
            BizResultWrapper<UserDetailz> resultB = new BizResultWrapper<>();
            UserInfoBizLogic.getInstance().getUserDetailzByUserId(userId, resultB);
            UserDetailz currUser = resultB.getFinalResult();

            if (null == currUser) {
                LOGGER.error(
                    "未找到用户详情, userId = {}",
                    userId
                );
                return;
            }

            // 获取亲友圈成员 DAO
            final IClubMemberDao clubMemberDao = sessionX.getMapper(IClubMemberDao.class);

            // 获取已经加入的亲友圈 Id 列表
            List<Integer> joinedClubIdList = clubMemberDao.getClubIdList(
                userId,
                MemberStateEnum.NORMAL.getIntVal(),
                MemberStateEnum.WAITING_FOR_REVIEW.getIntVal()
            );

            if (null != joinedClubIdList &&
                joinedClubIdList.size() >= MemberCenterBizLogic.getInstance().getMaxCanJoinedClubCount()) {
                LOGGER.error(
                    "已经加入的亲友圈数量超上限, 不能加入新的亲友圈了! userId = {}",
                    userId
                );
                ErrorEnum.CLUB__JOINED_CLUB_TOO_MANY.fillResultX(resultX);
                return;
            }

            // 获取已有的亲友圈成员
            ClubMemberEntity clubMemberEntity = clubMemberDao.getClubMemberEntity(clubId, userId);

            if (null != clubMemberEntity) {
                if (clubMemberEntity.getCurrState() == MemberStateEnum.DISMISS.getIntVal() ||
                    clubMemberEntity.getCurrState() == MemberStateEnum.REJECT.getIntVal()) {
                    LOGGER.error(
                        "请求被拒绝, 玩家不能加入亲友圈! userId = {}, clubId = {}",
                        userId, clubId
                    );
                    ErrorEnum.CLUB__DECLINE.fillResultX(resultX);
                    return;
                }
            }

            // 创建亲友圈成员实体
            clubMemberEntity = createClubMemberEntity(currUser, clubId);

            if (null == clubMemberEntity) {
                LOGGER.error(
                    "创建亲友圈成员实体为空, userId = {}, clubId = {}",
                    userId, clubId
                );
                return;
            }

            // 插入亲友圈成员实体
            clubMemberDao.insertInto(clubMemberEntity);

            // 清理用户已经加入的亲友 Id 数组
            redisCache.hdel(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_JOINED_CLUB_ID_ARRAY
            );

            // 清理亲友圈成员 Id 数组
            redisCache.hdel(
                RedisKeyDef.CLUB_X_PREFIX + clubId,
                RedisKeyDef.CLUB_MEMBER_ID_ARRAY,
                RedisKeyDef.CLUB_MEMBER_INFO_X_PREFIX + userId
            );

            resultX.setFinalResult(true);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            if (null != out_mySqlSession.getVal()) {
                out_mySqlSession
                    .getVal().close();
            }
        }
    }

    /**
     * 创建亲友圈成员实体
     *
     * @param userDetailz 用户详情
     * @param clubId      亲友圈 Id
     * @return 亲友圈成员实体
     */
    static private ClubMemberEntity createClubMemberEntity(final UserDetailz userDetailz, final int clubId) {
        if (clubId <= 0 ||
            null == userDetailz) {
            return null;
        }

        // 获取当前时间
        final long currTime = System.currentTimeMillis();

        ClubMemberEntity newEntity = new ClubMemberEntity();
        newEntity.setUserId(userDetailz.getUserId());
        newEntity.setClubId(clubId);
        newEntity.setRole(RoleDef.MEMBER.getIntVal());
        newEntity.setJoinTime(currTime);
        newEntity.setCurrState(MemberStateEnum.WAITING_FOR_REVIEW.getIntVal());

        return newEntity;
    }
}
