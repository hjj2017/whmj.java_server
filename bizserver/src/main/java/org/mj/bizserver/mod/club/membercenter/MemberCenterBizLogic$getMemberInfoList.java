package org.mj.bizserver.mod.club.membercenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberInfo;
import org.mj.bizserver.mod.club.membercenter.bizdata.MemberStateEnum;
import org.mj.bizserver.mod.club.membercenter.dao.IClubMemberDao;
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
 * 获取成员列表
 */
interface MemberCenterBizLogic$getMemberInfoList {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MemberCenterBizLogic$getMemberInfoList.class);

    /**
     * ( 异步方式 ) 获取亲友圈成员信息列表
     *
     * @param clubId         亲友圈 Id
     * @param pageIndex      页面索引
     * @param pageSize       页面大小
     * @param out_totalCount ( 输出参数 ) 总数量
     * @param callback       回调函数
     */
    default void getMemberInfoList_async(
        final int userId,
        final int clubId,
        final int pageIndex,
        final int pageSize,
        final OutParam<Integer> out_totalCount,
        final IBizResultCallback<List<MemberInfo>> callback) {

        // 确保回调函数不为空
        final IBizResultCallback<List<MemberInfo>>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 创建业务结果
        final BizResultWrapper<List<MemberInfo>> resultX = new BizResultWrapper<>();

        if (userId <= 0 ||
            clubId <= 0 ||
            pageIndex < 0 ||
            pageSize <= 0) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定 Id
            clubId,
            // 异步 IO 操作
            () -> getMemberInfoList(userId, clubId, pageIndex, pageSize, out_totalCount, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 获取亲友圈成员信息列表
     *
     * @param userId         用户 Id
     * @param clubId         亲友圈 Id
     * @param pageIndex      页面索引
     * @param pageSize       页面大小
     * @param out_totalCount 总记录数
     * @param resultX        业务结果
     */
    default void getMemberInfoList(
        final int userId, final int clubId, final int pageIndex, final int pageSize,
        final OutParam<Integer> out_totalCount,
        final BizResultWrapper<List<MemberInfo>> resultX) {
        if (userId <= 0 ||
            clubId <= 0 ||
            pageIndex < 0 ||
            pageSize <= 0 ||
            null == out_totalCount ||
            null == resultX) {
            return;
        }

        // ( 输出参数 ) MySql 会话
        final OutParam<SqlSession> out_mySqlSession = new OutParam<>();

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 获取我自己的成员信息
            MemberInfo validMember = MemberInfoGetter.getValidMemberInfo(
                userId, clubId, redisCache, out_mySqlSession
            );

            if (null == validMember) {
                LOGGER.error(
                    "用户不是该帮会的成员, 无法获取成员列表! userId = {}, clubId = {}",
                    userId, clubId
                );
                return;
            }

            // 获取成员 Id 列表
            List<Integer> userIdList = getMemberIdList(clubId, redisCache, out_mySqlSession);

            if (null == userIdList ||
                userIdList.isEmpty()) {
                OutParam.putVal(out_totalCount, 0);
                return;
            }

            OutParam.putVal(out_totalCount, userIdList.size());

            // 执行分页
            userIdList = userIdList.subList(
                pageIndex * pageSize,
                Math.min((pageIndex + 1) * pageSize, userIdList.size())
            );

            // 获取亲友圈成员信息列表
            resultX.setFinalResult(getMemberInfoList(
                clubId, userIdList, redisCache, out_mySqlSession
            ));
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
     * 获取 ( 亲友圈 ) 成员 Id 列表
     *
     * @param clubId           亲友圈 Id
     * @param redisCache       Redis 缓存
     * @param out_mySqlSession ( 输出参数 ) MySql 会话
     * @return (亲友圈) 成员 Id 列表
     */
    static private List<Integer> getMemberIdList(
        final int clubId, final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        if (clubId <= 0 ||
            null == redisCache ||
            null == out_mySqlSession) {
            return Collections.emptyList();
        }

        final String redisKey = RedisKeyDef.CLUB_X_PREFIX + clubId;

        String strMemberIdArray = redisCache.hget(
            redisKey, RedisKeyDef.CLUB_MEMBER_ID_ARRAY
        );

        List<Integer> memberIdList = JSONArray.parseArray(strMemberIdArray, Integer.class);

        if (null != memberIdList) {
            return memberIdList;
        }

        // 获取 MySql 数据库会话
        SqlSession sessionX = out_mySqlSession.getVal();

        if (null == sessionX) {
            sessionX = MySqlXuite.openGameDbSession();
            out_mySqlSession.setVal(sessionX);
        }

        // 从 MySql 中获取亲友圈 Id 列表
        memberIdList = sessionX.getMapper(IClubMemberDao.class).getUserIdList(
            clubId,
            MemberStateEnum.NORMAL.getIntVal(),
            MemberStateEnum.WAITING_FOR_REVIEW.getIntVal()
        );

        if (null == memberIdList) {
            memberIdList = Collections.emptyList();
        }

        redisCache.hset(
            redisKey, RedisKeyDef.CLUB_MEMBER_ID_ARRAY,
            JSON.toJSONString(memberIdList)
        );

        return memberIdList;
    }

    /**
     * 获取成员信息列表
     *
     * @param clubId           亲友圈 Id
     * @param userIdList       用户 Id 列表
     * @param redisCache       ( 所使用的 ) Redis 缓存
     * @param out_mySqlSession ( 输出参数 ) MySql 会话
     * @return 成员信息列表
     */
    static private List<MemberInfo> getMemberInfoList(
        final int clubId,
        final List<Integer> userIdList,
        final Jedis redisCache, final OutParam<SqlSession> out_mySqlSession) {
        if (clubId <= 0 ||
            null == userIdList ||
            userIdList.isEmpty() ||
            null == redisCache ||
            null == out_mySqlSession) {
            return Collections.emptyList();
        }

        // ( 亲友圈 ) 成员信息列表
        final List<MemberInfo> memberInfoList = new ArrayList<>();

        for (Integer userId : userIdList) {
            if (null == userId ||
                userId <= 0) {
                continue;
            }

            // 获取已经加入的亲友圈
            final MemberInfo currMember = MemberInfoGetter.getMemberInfo(
                userId, clubId, redisCache, out_mySqlSession
            );

            if (null == currMember) {
                LOGGER.error(
                    "未找到亲友圈成员, clubId = {}, userId = {}",
                    clubId, userId
                );
                continue;
            }

            memberInfoList.add(currMember);
        }

        return memberInfoList;
    }
}
