package org.mj.bizserver.mod.club.adminctrl;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.base.AsyncOperationProcessorSingleton;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.IBizResultCallback;
import org.mj.bizserver.mod.club.adminctrl.dao.ClubEntity;
import org.mj.bizserver.mod.club.adminctrl.dao.IClubDao;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.ClubStateEnum;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberStateEnum;
import org.mj.bizserver.mod.club.membercenter.bizdata.RoleDef;
import org.mj.bizserver.mod.club.membercenter.dao.ClubMemberEntity;
import org.mj.bizserver.mod.club.membercenter.dao.IClubMemberDao;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * ( 异步方式 ) 创建亲友圈
 */
interface AdminCtrlBizLogic$createClub {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(AdminCtrlBizLogic$createClub.class);

    /**
     * ( 异步方式 ) 创建亲友圈
     *
     * @param userId   用户 Id
     * @param clubName 亲友圈名称
     * @param callback 回调函数
     */
    default void createClub_async(
        final int userId, final String clubName, final IBizResultCallback<Integer> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Integer>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        final BizResultWrapper<Integer> resultX = new BizResultWrapper<>();

        if (userId <= 0 ||
            null == clubName ||
            clubName.isEmpty()) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定 Id
            userId,
            // 异步 IO 操作
            () -> createClub(userId, clubName, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 创建亲友圈
     *
     * @param userId   用户 Id
     * @param clubName 亲友圈名称
     * @param resultX  业务结果
     */
    private void createClub(
        final int userId,
        final String clubName,
        final BizResultWrapper<Integer> resultX) {
        if (null == resultX) {
            return;
        }

        resultX.setFinalResult(-1);

        if (userId <= 0 ||
            null == clubName ||
            clubName.isEmpty()) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        // 获取用户详情
        BizResultWrapper<UserDetailz> resultA = new BizResultWrapper<>();
        UserInfoBizLogic.getInstance().getUserDetailzByUserId(userId, resultA);
        UserDetailz currUser = resultA.getFinalResult();

        if (null == currUser) {
            LOGGER.error(
                "未找到用户, userId = {}",
                userId
            );
            return;
        }

        try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
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
                    "已经加入的亲友圈数量超上限, 不能创建新的亲友圈了! userId = {}",
                    userId
                );
                return;
            }

            // 创建亲友圈实体
            final ClubEntity clubEntity = createClubEntity(currUser, clubName);

            if (null == clubEntity) {
                LOGGER.error(
                    "创建亲友圈实体为空, userId = {}, clubName = {}",
                    userId,
                    clubName
                );
                return;
            }

            final IClubDao clubDao = sessionX.getMapper(IClubDao.class);

            // 插入亲友圈实体
            clubDao.insertInto(clubEntity);

            // 获取亲友圈 Id
            final int clubId = clubEntity.getClubId();
            // 创建亲友圈成员实体
            final ClubMemberEntity memberEntity = createClubMemberEntity(currUser, clubId);

            if (null == memberEntity) {
                LOGGER.error(
                    "创建亲友圈成员实体为空, userId = {}, clubName = {}",
                    userId,
                    clubName
                );
                return;
            }

            // XXX 注意: 这里是通过 membercenter.IClubMemberDao 插入的数据
            clubMemberDao.insertInto(memberEntity);
            clubDao.updateNumOfPeople(clubId);

            // 清除用户已经加入的亲友圈 Id
            try (Jedis redisCache = RedisXuite.getRedisCache()) {
                redisCache.hdel(
                    RedisKeyDef.USER_X_PREFIX + userId,
                    RedisKeyDef.USER_JOINED_CLUB_ID_ARRAY
                );
            }

            LOGGER.info(
                "成功创建亲友圈, userId = {}, clubId = {}, clubName = {}",
                userId, clubId, clubName
            );

            resultX.setFinalResult(clubId);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 创建亲友圈实体
     *
     * @param creatorDetailz 创建人详情
     * @param clubName       亲友圈名称
     * @return 亲友圈实体
     */
    static private ClubEntity createClubEntity(
        final UserDetailz creatorDetailz, final String clubName) {
        if (null == creatorDetailz ||
            null == clubName ||
            clubName.isEmpty()) {
            return null;
        }

        final int newId = popUpClubId();

        if (newId <= 0) {
            return null;
        }

        final long currTime = System.currentTimeMillis();
        final ClubEntity newEntity = new ClubEntity();
        newEntity.setClubId(newId);
        newEntity.setClubName(clubName);
        newEntity.setCreatorId(creatorDetailz.getUserId());
        newEntity.setCreatorName(creatorDetailz.getUserName());
        newEntity.setCreatorHeadImg(creatorDetailz.getHeadImg());
        newEntity.setCreatorSex(creatorDetailz.getSex());
        newEntity.setCreateTime(currTime);
        newEntity.setCurrState(ClubStateEnum.NORMAL.getIntVal());

        return newEntity;
    }

    /**
     * 从用户 Id 泵中弹出一个亲友圈 Id!
     * XXX 注意: 亲友圈 Id 泵需要运行 devdoc/tool/gen_club_id.py 脚本,
     * 需要克隆 devdoc 代码库
     *
     * @return 用户 Id
     */
    static private int popUpClubId() {
        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 从用户 Id 泵中弹出一个亲友圈 Id
            String strClubId = redisCache.lpop(RedisKeyDef.CLUB_ID_PUMP);

            if (null == strClubId) {
                LOGGER.error("亲友圈 Id 泵失效, 弹出亲友圈 Id 为空");
                return -1;
            }

            return Integer.parseInt(strClubId);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
            return -1;
        }
    }

    /**
     * 创建亲友圈成员实体
     *
     * @param userDetailz 用户详情
     * @param clubId      亲友圈 Id
     * @return 亲友圈成员实体
     */
    static private ClubMemberEntity createClubMemberEntity(
        final UserDetailz userDetailz,
        final int clubId) {
        if (null == userDetailz ||
            clubId <= 0) {
            return null;
        }

        final long currTime = System.currentTimeMillis();
        final ClubMemberEntity newEntity = new ClubMemberEntity();
        newEntity.setUserId(userDetailz.getUserId());
        newEntity.setClubId(clubId);
        newEntity.setRole(RoleDef.SUPER_ADMIN.getIntVal());
        newEntity.setJoinTime(currTime);
        newEntity.setCurrState(MemberStateEnum.NORMAL.getIntVal());

        return newEntity;
    }
}
