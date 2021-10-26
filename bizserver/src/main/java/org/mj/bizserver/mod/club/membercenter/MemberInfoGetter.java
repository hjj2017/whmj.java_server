package org.mj.bizserver.mod.club.membercenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberInfo;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberStateEnum;
import org.mj.bizserver.mod.club.membercenter.dao.ClubMemberEntity;
import org.mj.bizserver.mod.club.membercenter.dao.IClubMemberDao;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.OutParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 获取亲友圈成员信息获得者
 */
class MemberInfoGetter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MemberInfoGetter.class);

    /**
     * 私有化类默认构造器
     */
    private MemberInfoGetter() {
    }

    /**
     * 获取有效的亲友圈成员信息
     *
     * @param userId     用户 Id
     * @param clubId     亲友圈 Id
     * @param redisCache ( 所使用的 ) Redis 缓存
     * @return 成员信息
     */
    static MemberInfo getValidMemberInfo(
        final int userId, final int clubId, final Jedis redisCache) {

        // ( 输出参数 ) MySql 会话
        final OutParam<SqlSession>
            out_mySqlSession = new OutParam<>();

        try {
            return getValidMemberInfo(
                userId, clubId, redisCache, out_mySqlSession
            );
        } finally {
            if (null != out_mySqlSession.getVal()) {
                out_mySqlSession
                    .getVal().close();
            }
        }
    }

    /**
     * 获取有效的亲友圈成员信息
     *
     * @param userId           用户 Id
     * @param clubId           亲友圈 Id
     * @param redisCache       ( 所使用的 ) Redis 缓存
     * @param out_mySqlSession ( 输出参数 ) MySql 会话
     * @return 成员信息
     */
    static MemberInfo getValidMemberInfo(
        final int userId, final int clubId, final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        // 获取亲友圈成员信息
        MemberInfo currMember = getMemberInfo(userId, clubId, redisCache, out_mySqlSession);

        if (null == currMember ||
            currMember.getCurrState() != MemberStateEnum.NORMAL) {
            // 如果不是正常状态,
            // 清理用户已经加入的亲友圈 Id 数组
            redisCache.hdel(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_JOINED_CLUB_ID_ARRAY
            );

            return null;
        }

        return currMember;
    }

    /**
     * 获取亲友圈成员信息
     *
     * @param userId           用户 Id
     * @param clubId           亲友圈 Id
     * @param redisCache       ( 所使用的 ) Redis 缓存
     * @param out_mySqlSession ( 输出参数 ) MySql 会话
     * @return 成员信息
     */
    static MemberInfo getMemberInfo(
        final int userId, final int clubId, final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        if (userId <= 0 ||
            clubId <= 0 ||
            null == redisCache ||
            null == out_mySqlSession) {
            return null;
        }

        // 获取亲友圈实体
        final ClubMemberEntity memberEntity = getMemberEntity(userId, clubId, redisCache, out_mySqlSession);
        // 亲友圈成员信息
        final MemberInfo memberInfo = MemberInfo.fromEntity(memberEntity);

        if (null == memberEntity ||
            null == memberInfo) {
            // 清理用户已经加入的亲友圈 Id 数组
            redisCache.hdel(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_JOINED_CLUB_ID_ARRAY
            );

            return null;
        }

        // 获取用户详情
        BizResultWrapper<UserDetailz> resultX = new BizResultWrapper<>();
        UserInfoBizLogic.getInstance().getUserDetailzByUserId(memberEntity.getUserId(), resultX);
        UserDetailz userDetailz = resultX.getFinalResult();

        if (null == userDetailz) {
            LOGGER.error(
                "用户详情为空, userId = {}",
                userId
            );
            return null;
        }

        memberInfo.setUserName(userDetailz.getUserName());
        memberInfo.setHeadImg(userDetailz.getHeadImg());
        memberInfo.setSex(userDetailz.getSex());
        memberInfo.setLastLoginTime(userDetailz.getLastLoginTime());

        return memberInfo;
    }

    /**
     * 获取亲友圈成员实体
     *
     * @param userId           用户 Id
     * @param clubId           亲友圈 Id
     * @param redisCache       ( 所使用的 ) Redis 缓存
     * @param out_mySqlSession ( 输出参数 ) MySql 会话
     * @return 成员信息
     */
    static ClubMemberEntity getMemberEntity(
        final int userId, final int clubId, final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        if (userId <= 0 ||
            clubId <= 0 ||
            null == redisCache ||
            null == out_mySqlSession) {
            return null;
        }

        // Redis 关键字
        final String redisKey0 = RedisKeyDef.CLUB_X_PREFIX + clubId;
        final String redisKey1 = RedisKeyDef.CLUB_MEMBER_INFO_X_PREFIX + userId;
        // 获取实体字符串
        final String strClubEntity = redisCache.hget(
            redisKey0, redisKey1
        );

        if (null != strClubEntity) {
            return JSONObject.parseObject(
                strClubEntity,
                ClubMemberEntity.class
            );
        }

        // 获取 MySql 数据库会话
        SqlSession sessionX = out_mySqlSession.getVal();

        if (null == sessionX) {
            sessionX = MySqlXuite.openGameDbSession();
            out_mySqlSession.setVal(sessionX);
        }

        // 获取亲友圈实体
        final ClubMemberEntity memberEntity = sessionX.getMapper(IClubMemberDao.class).getClubMemberEntity(userId, clubId);

        if (null != memberEntity) {
            redisCache.hset(
                redisKey0, redisKey1,
                JSON.toJSONString(memberEntity)
            );
        } else {
            // 如果实体为空,
            // 则清理 Redis 缓存...
            redisCache.hdel(
                redisKey0, redisKey1
            );
        }

        return memberEntity;
    }
}
