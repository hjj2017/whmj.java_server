package org.mj.bizserver.mod.club.membercenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.club.membercenter.bizdata.JoinedClub;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberStateEnum;
import org.mj.bizserver.mod.club.membercenter.dao.ClubEntity;
import org.mj.bizserver.mod.club.membercenter.dao.IClubDao;
import org.mj.bizserver.mod.club.membercenter.dao.IClubMemberDao;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.OutParam;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 获取已经加入的亲友圈列表
 */
interface MemberCenterBizLogic$getJoinedClubList {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MemberCenterBizLogic$getJoinedClubList.class);

    /**
     * ( 异步方式 ) 获取已经加入的亲友圈列表
     *
     * @param userId   用户 Id
     * @param callback 回调函数
     */
    default void getJoinedClubList_async(
        final int userId,
        final IBizResultCallback<List<JoinedClub>> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<List<JoinedClub>>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        final BizResultWrapper<List<JoinedClub>> resultX = new BizResultWrapper<>();

        if (userId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定 Id
            userId,
            // 异步 IO 操作
            () -> resultX.setFinalResult(getJoinedClubList(userId)),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 获取已经加入的亲友圈列表
     *
     * @param userId 用户 Id
     * @return 已经加入的亲友圈列表
     */
    private List<JoinedClub> getJoinedClubList(final int userId) {
        if (userId <= 0) {
            return Collections.emptyList();
        }

        // ( 输出参数 ) MySql 会话
        final OutParam<SqlSession> out_mySqlSession = new OutParam<>();

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 获取已经加入的亲友圈 Id 列表
            final List<Integer> joinedClubIdList = getJoinedClubIdList(userId, redisCache, out_mySqlSession);

            if (null == joinedClubIdList ||
                joinedClubIdList.isEmpty()) {
                return Collections.emptyList();
            }

            // 获取已经加入的亲友圈列表
            return getJoinedClubList(joinedClubIdList, redisCache, out_mySqlSession);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            // 关闭 MySql 连接
            if (null != out_mySqlSession.getVal()) {
                out_mySqlSession
                    .getVal().close();
            }
        }

        return Collections.emptyList();
    }

    /**
     * 获取已经加入的亲友圈 Id 列表
     *
     * @param userId           用户 Id
     * @param redisCache       Redis 缓存
     * @param out_mySqlSession ( 输出参数 ) MySql 会话
     * @return 已经加入的亲友圈 Id 列表
     */
    static private List<Integer> getJoinedClubIdList(
        final int userId, final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        if (userId <= 0 ||
            null == redisCache ||
            null == out_mySqlSession) {
            return Collections.emptyList();
        }

        final String redisKey = RedisKeyDef.USER_X_PREFIX + userId;

        String strClubIdArray = redisCache.hget(
            redisKey, RedisKeyDef.USER_JOINED_CLUB_ID_ARRAY
        );

        List<Integer> clubIdList = JSONArray.parseArray(strClubIdArray, Integer.class);

        if (null != clubIdList) {
            return clubIdList;
        }

        // 获取 MySql 数据库会话
        SqlSession sessionX = out_mySqlSession.getVal();

        if (null == sessionX) {
            sessionX = MySqlXuite.openGameDbSession();
            out_mySqlSession.setVal(sessionX);
        }

        // 从 MySql 中获取亲友圈 Id 列表
        clubIdList = sessionX.getMapper(IClubMemberDao.class).getClubIdList(
            userId,
            MemberStateEnum.NORMAL.getIntVal()
        );

        if (null == clubIdList) {
            clubIdList = Collections.emptyList();
        }

        redisCache.hset(
            redisKey, RedisKeyDef.USER_JOINED_CLUB_ID_ARRAY,
            JSON.toJSONString(clubIdList)
        );

        return clubIdList;
    }

    /**
     * 获取亲友圈列表
     *
     * @param clubIdList       已经加入的亲友圈 Id 列表
     * @param redisCache       Redis 缓存
     * @param out_mySqlSession ( 输出参数 ) MySql 会话
     * @return 已经加入的亲友圈列表
     */
    static private List<JoinedClub> getJoinedClubList(
        final List<Integer> clubIdList, final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        if (null == clubIdList ||
            clubIdList.isEmpty() ||
            null == redisCache ||
            null == out_mySqlSession) {
            return Collections.emptyList();
        }

        // 已经加入的亲友圈列表
        final List<JoinedClub> joinedClubList = new ArrayList<>(clubIdList.size());

        for (Integer clubId : clubIdList) {
            if (null == clubId ||
                clubId <= 0) {
                continue;
            }

            // 获取已经加入的亲友圈
            final JoinedClub currClub = getJoinedClubById(clubId, redisCache, out_mySqlSession);

            if (null == currClub) {
                LOGGER.error(
                    "未找到亲友圈, clubId = {}",
                    clubId
                );
                continue;
            }

            // 设置游戏中和等待数量
            currClub.setNumOfGaming(MemberCenterBizLogic$getClubDetailz.getNumOfX(
                clubId, redisCache, RedisKeyDef.CLUB_NUM_OF_GAMING
            ));
            currClub.setNumOfWaiting(MemberCenterBizLogic$getClubDetailz.getNumOfX(
                clubId, redisCache, RedisKeyDef.CLUB_NUM_OF_WAITING
            ));

            joinedClubList.add(currClub);
        }

        return joinedClubList;
    }

    /**
     * 根据 Id 获取已经加入的亲友圈
     *
     * @param clubId           亲友圈 Id
     * @param redisCache       Redis 缓存
     * @param out_mySqlSession ( 输出参数 ) MySql 会话
     * @return 已经加入的亲友圈
     */
    static private JoinedClub getJoinedClubById(
        final int clubId, final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        if (clubId <= 0 ||
            null == redisCache) {
            return null;
        }

        // 获取亲友圈实体
        final ClubEntity clubEntity = getClubEntityById(clubId, redisCache, out_mySqlSession);
        // 已经加入的亲友圈
        final JoinedClub joinedClub = JoinedClub.fromEntity(clubEntity);

        if (null == clubEntity ||
            null == joinedClub) {
            return null;
        }

        // 获取用户详情
        BizResultWrapper<UserDetailz> resultX = new BizResultWrapper<>();
        UserInfoBizLogic.getInstance().getUserDetailzByUserId(clubEntity.getCreatorId(), resultX);
        UserDetailz userDetailz = resultX.getFinalResult();

        if (null == userDetailz) {
            LOGGER.error(
                "未找到用户详情, userId = {}",
                clubEntity.getCreatorId()
            );
            return null;
        }

        joinedClub.setCreatorName(userDetailz.getUserName());
        joinedClub.setCreatorSex(userDetailz.getSex());
        joinedClub.setCreatorHeadImg(userDetailz.getHeadImg());

        return joinedClub;
    }

    /**
     * 根据 Id 获取亲友圈实体
     *
     * @param clubId           亲友圈 Id
     * @param redisCache       Redis 缓存
     * @param out_mySqlSession ( 输出参数 ) MySql 会话
     * @return 亲友圈实体
     */
    static private ClubEntity getClubEntityById(
        final int clubId, final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        if (clubId <= 0 ||
            null == redisCache) {
            return null;
        }

        // Redis 关键字
        final String redisKey = RedisKeyDef.CLUB_X_PREFIX + clubId;
        // 获取实体字符串
        final String strClubEntity = redisCache.hget(
            redisKey,
            RedisKeyDef.CLUB_DETAILZ
        );

        if (null != strClubEntity) {
            return JSONObject.parseObject(
                strClubEntity,
                ClubEntity.class
            );
        }

        // 获取 MySql 数据库会话
        SqlSession sessionX = out_mySqlSession.getVal();

        if (null == sessionX) {
            sessionX = MySqlXuite.openGameDbSession();
            out_mySqlSession.setVal(sessionX);
        }

        // 获取亲友圈实体
        final ClubEntity clubEntity = sessionX.getMapper(IClubDao.class).getByClubId(clubId);

        if (null != clubEntity) {
            redisCache.hset(
                redisKey, RedisKeyDef.CLUB_DETAILZ,
                JSON.toJSONString(clubEntity)
            );
        }

        return clubEntity;
    }
}
