package org.mj.bizserver.mod.club.membercenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.club.membercenter.bizdata.ClubDetailz;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberInfo;
import org.mj.bizserver.mod.club.membercenter.dao.ClubEntity;
import org.mj.bizserver.mod.club.membercenter.dao.IClubDao;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.OutParam;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * ( 异步方式 ) 获取亲友圈详情
 */
interface MemberCenterBizLogic$getClubDetailz {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MemberCenterBizLogic$getClubDetailz.class);

    /**
     * 获取亲友圈详情
     *
     * @param userId   用户 Id
     * @param clubId   亲友圈 Id
     * @param callback 回调函数
     */
    default void getClubDetailz_async(
        final int userId, final int clubId, final IBizResultCallback<ClubDetailz> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<ClubDetailz>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        final BizResultWrapper<ClubDetailz> resultX = new BizResultWrapper<>();

        if (userId <= 0 ||
            clubId <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定 Id
            userId,
            // 异步 IO 操作
            () -> getClubDetailz(userId, clubId, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 获取亲友圈详情
     *
     * @param userId  用户 Id
     * @param clubId  亲友圈 Id
     * @param resultX 业务结果
     */
    default void getClubDetailz(
        final int userId, final int clubId, BizResultWrapper<ClubDetailz> resultX) {
        if (userId <= 0 ||
            clubId <= 0 ||
            null == resultX) {
            return;
        }

        // ( 输出参数 ) MySql 会话
        final OutParam<SqlSession> out_mySqlSession = new OutParam<>();

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 获取已经加入的亲友圈 Id 列表
            MemberInfo validMember = MemberInfoGetter.getValidMemberInfo(
                userId, clubId, redisCache, out_mySqlSession
            );

            if (null == validMember) {
                // 如果玩家没有加入该亲友圈,
                LOGGER.error(
                    "用户尚未加入亲友圈, userId = {}, clubId = {}",
                    userId, clubId
                );
                return;
            }

            // 获取亲友圈详情
            ClubDetailz clubDetailz = getClubDetailzById(clubId, redisCache, out_mySqlSession);

            if (null != clubDetailz) {
                // 设置我在亲友圈中的角色
                clubDetailz.setMyRole(validMember.getRole());

                // 设置游戏中和等待数量
                clubDetailz.setNumOfGaming(getNumOfX(
                    clubId, redisCache, RedisKeyDef.CLUB_NUM_OF_GAMING
                ));
                clubDetailz.setNumOfWaiting(getNumOfX(
                    clubId, redisCache, RedisKeyDef.CLUB_NUM_OF_WAITING
                ));
            }

            resultX.setFinalResult(clubDetailz);
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
    }

    /**
     * 根据 Id 获取亲友圈详情
     *
     * @param clubId           亲友圈 Id
     * @param redisCache       Redis 缓存
     * @param out_mySqlSession ( 输出参数 ) MySql 会话
     * @return 亲友圈详情
     */
    static ClubDetailz getClubDetailzById(
        int clubId, final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        if (clubId <= 0 ||
            null == redisCache ||
            null == out_mySqlSession) {
            return null;
        }

        // 从数据库中获取亲友圈实体
        final ClubEntity clubEntity = getClubEntityById(clubId, redisCache, out_mySqlSession);
        // 亲友圈详情
        final ClubDetailz clubDetailz = ClubDetailz.fromEntity(clubEntity);

        if (null == clubEntity ||
            null == clubDetailz) {
            LOGGER.error(
                "亲友圈实体为空, clubId = {}",
                clubId
            );
            return null;
        }

        // 获取用户详情
        BizResultWrapper<UserDetailz> resultA = new BizResultWrapper<>();
        UserInfoBizLogic.getInstance().getUserDetailzByUserId(clubEntity.getCreatorId(), resultA);
        UserDetailz userDetailz = resultA.getFinalResult();

        if (null == userDetailz) {
            LOGGER.error(
                "未找到用户详情, clubId = {}, userId = {}",
                clubId,
                clubEntity.getCreatorId()
            );
            return null;
        }

        clubDetailz.setCreatorName(userDetailz.getUserName());
        clubDetailz.setCreatorSex(userDetailz.getSex());
        clubDetailz.setCreatorHeadImg(userDetailz.getHeadImg());

        return clubDetailz;
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
        int clubId, final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        if (clubId <= 0 ||
            null == redisCache ||
            null == out_mySqlSession) {
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

        // 从数据库中获取亲友圈实体
        final ClubEntity clubEntity = sessionX.getMapper(IClubDao.class).getByClubId(clubId);

        if (null != clubEntity) {
            redisCache.hset(
                redisKey, RedisKeyDef.CLUB_DETAILZ,
                JSON.toJSONString(clubEntity)
            );
        }

        return clubEntity;
    }

    /**
     * 获取数值
     *
     * @param clubId     亲友圈 Id
     * @param redisCache Redis 缓存
     * @param subKey     子键
     * @return 数值
     */
    static int getNumOfX(
        final int clubId, final Jedis redisCache, final String subKey) {
        if (clubId <= 0 ||
            null == redisCache ||
            null == subKey) {
            return -1;
        }

        // Redis 关键字
        final String redisKey = RedisKeyDef.CLUB_X_PREFIX + clubId;
        // 获取字符串值
        final String strNum = redisCache.hget(redisKey, subKey);

        if (null == strNum) {
            return -1;
        } else {
            return Integer.parseInt(strNum);
        }
    }
}
