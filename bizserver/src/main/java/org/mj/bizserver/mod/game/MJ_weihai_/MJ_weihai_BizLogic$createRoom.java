package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.IBizResultCallback;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.ClubDetailz;
import org.mj.bizserver.mod.club.membercenter.bizdata.FixGameX;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RuleSetting;
import org.mj.comm.util.DLock;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建房间
 */
interface MJ_weihai_BizLogic$createRoom {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$createRoom.class);

    /**
     * 空亲友圈 Id
     */
    int EMPTY_CLUB_ID = -1;

    /**
     * 空亲友圈牌桌 Id
     */
    int EMPTY_TABLE_SEQ_NUM = -1;

    /**
     * ( 异步方式 ) 创建房间
     *
     * @param userId   用户 Id
     * @param ruleMap  规则字典
     * @param callback 回调函数
     * @see #createRoom_async(int, int, int, Map, IBizResultCallback)
     */
    default void createRoom_async(
        int userId, Map<Integer, Integer> ruleMap, IBizResultCallback<Integer> callback) {
        createRoom_async(
            userId,
            EMPTY_CLUB_ID,
            EMPTY_TABLE_SEQ_NUM,
            ruleMap,
            callback
        );
    }

    /**
     * ( 异步方式 ) 创建亲友圈房间 ( 亲友圈牌桌 )
     *
     * @param userId        用户 Id
     * @param clubId        亲友圈 Id
     * @param tableSeqNum   牌桌序号
     * @param usingFixGameX 使用固定玩法索引
     * @param amendRuleMap  修正规则字典
     * @param callback      回调函数
     */
    default void createRoom_async(
        int userId, int clubId, int tableSeqNum, int usingFixGameX,
        Map<Integer, Integer> amendRuleMap,
        IBizResultCallback<Integer> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Integer>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        final BizResultWrapper<Integer> resultX = new BizResultWrapper<>();

        if (userId <= 0 ||
            clubId <= 0 ||
            tableSeqNum < 0 ||
            usingFixGameX < 0) {
            // 参数错误
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定线程 Id
            userId,
            // 异步 IO 操作
            () -> {
                // 获取亲友圈详情
                final BizResultWrapper<ClubDetailz> resultA = new BizResultWrapper<>();
                MemberCenterBizLogic.getInstance().getClubDetailz(
                    userId, clubId, resultA
                );

                if (0 != resultA.getErrorCode() ||
                    null == resultA.getFinalResult()) {
                    LOGGER.error(
                        "亲友圈详情为空, userId = {}, clubId = {}",
                        userId, clubId
                    );
                    return;
                }

                // 混合游戏规则
                final Map<Integer, Integer> newRuleMap = mixGameRule(
                    resultA.getFinalResult(),
                    usingFixGameX,
                    amendRuleMap
                );

                try (DLock newLocker = tryGetNewLocker(userId)) {
                    if (null == newLocker) {
                        return;
                    }

                    createRoom(
                        userId, clubId, tableSeqNum, newRuleMap, resultX
                    );
                }
            },

            // 回到主线程
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 混合游戏规则
     *
     * @param currClub      当前亲友圈
     * @param usingFixGameX 使用固定玩法索引
     * @param amendRuleMap  修正规则字典
     * @return 混合后的游戏规则
     */
    static private Map<Integer, Integer> mixGameRule(
        ClubDetailz currClub,
        int usingFixGameX,
        Map<Integer, Integer> amendRuleMap) {
        if (null == currClub ||
            usingFixGameX < 0) {
            return amendRuleMap;
        }

        // 获取固定玩法列表
        final List<FixGameX> fixGameXList = currClub.getFixGameXList();

        if (null == fixGameXList ||
            usingFixGameX >= fixGameXList.size()) {
            return amendRuleMap;
        }

        // 获取固定玩法
        final FixGameX fixGameX = fixGameXList.get(usingFixGameX);

        if (null == fixGameX ||
            null == fixGameX.getRuleMap()) {
            return amendRuleMap;
        }

        Map<Integer, Integer> mixRuleMap = new HashMap<>();
        mixRuleMap.putAll(fixGameX.getRuleMap());
        mixRuleMap.putAll(amendRuleMap);

        return mixRuleMap;
    }

    /**
     * ( 异步方式 ) 创建房间
     *
     * @param userId      用户 Id
     * @param clubId      亲友圈 Id
     * @param tableSeqNum 亲友圈牌桌序号
     * @param ruleMap     规则字典
     * @param callback    回调函数
     */
    default void createRoom_async(
        int userId, int clubId, int tableSeqNum,
        Map<Integer, Integer> ruleMap,
        IBizResultCallback<Integer> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Integer>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        // 业务结果
        final BizResultWrapper<Integer> resultX = new BizResultWrapper<>();

        if (userId <= 0 ||
            null == ruleMap ||
            ruleMap.size() <= 0) {
            // 参数错误
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            // 绑定线程 Id
            userId,
            // 异步 IO 操作
            () -> {
                try (DLock newLocker = tryGetNewLocker(userId)) {
                    if (null != newLocker) {
                        createRoom(userId, clubId, tableSeqNum, ruleMap, resultX);
                    }
                }
            },

            // 回到主线程
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 尝试获取新的分布式锁
     *
     * @param userId 用户 Id
     * @return 分布式锁
     */
    static private DLock tryGetNewLocker(int userId) {
        if (userId <= 0) {
            return null;
        }

        // 创建新的分布式锁
        final DLock newLocker = DLock.newLock("create_room?user_id=" + userId);

        if (null == newLocker ||
            !newLocker.tryLock(5 * 1000)) {
            LOGGER.error(
                "不能创建房间, 分布式锁加锁失败, userId = {}",
                userId
            );
            return null;
        }

        return newLocker;
    }

    /**
     * 创建房间
     *
     * @param userId      用户 Id
     * @param clubId      亲友圈 Id
     * @param tableSeqNum 亲友圈牌桌序号
     * @param ruleMap     规则字典
     * @param resultX     结果对象
     */
    private void createRoom(
        int userId, int clubId, int tableSeqNum,
        Map<Integer, Integer> ruleMap,
        BizResultWrapper<Integer> resultX) {
        if (userId <= 0 ||
            null == ruleMap ||
            ruleMap.isEmpty()) {
            return;
        }

        if (null == resultX) {
            resultX = new BizResultWrapper<>();
        }

        //
        // 检查用户不在 ( 已有的 ) 房间中?
        if (!checkUserHasNotRoom(userId)) {
            Room oldRoom = RoomGroup.getByUserId(userId);
            resultX.setFinalResult((null == oldRoom) ? -1 : oldRoom.getRoomId());
            return;
        }

        // 规则设置
        final RuleSetting ruleSetting = new RuleSetting(ruleMap);

        if (!ruleSetting.validate()) {
            LOGGER.error(
                "规则验证失败, userId = {}",
                userId
            );
            ErrorEnum.GAME__ROOM_RULE_SETTING_HAS_ERROR.fillResultX(resultX);
            return;
        }

        if (!RoomCardCashier.hasEnoughRoomCard(userId, ruleSetting, userId, clubId)) {
            LOGGER.error(
                "房卡数量不足, userId = {}, clubId = {}",
                userId, clubId
            );
            ErrorEnum.GAME__ROOM_CARD_NOT_ENOUGH.fillResultX(resultX);
            return;
        }

        // 生成新 Id
        final int newRoomId = RoomIdGen.newId();

        if (newRoomId <= 0) {
            ErrorEnum.GAME__GEN_ROOM_ID_FAIL.fillResultX(resultX);
            LOGGER.error(
                "生成房间 Id 失败, userId = {}",
                userId
            );
            return;
        }

        // 创建房间
        final Room newRoom = new Room(newRoomId, ruleSetting, userId);
        newRoom.setCreateTime(System.currentTimeMillis());
        newRoom.setClubId(clubId);
        newRoom.setTableSeqNum(tableSeqNum);

        // 添加到房间组
        RoomGroup.add(newRoom);

        LOGGER.info(
            "创建房间完成, userId = {}, roomId = {}, clubId = {}",
            userId, newRoomId, clubId
        );

        // 在此直接加入房间
        MJ_weihai_BizLogic.getInstance().joinRoom(
            userId, newRoomId, null
        );

        // 同步房间关键数据
        MJ_weihai_BizLogic.getInstance().syncRoomKeyDataToRedis(newRoom);

        resultX.setFinalResult(newRoomId);
    }

    /**
     * 检查用户不在 ( 已有的 ) 房间中
     *
     * @param userId 用户 Id
     * @return true = 不在房间中, false = 已加入其他房间
     */
    private boolean checkUserHasNotRoom(int userId) {
        if (userId <= 0) {
            // 不合法的用户 Id 是没法做出判断的,
            return false;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            //
            // 需要借助 Redis 做跨服检查,
            // 例如:
            // 当前游戏服务器是 game_server_4001, 用户没有加入任何房间.
            // 但是在另一个游戏服务器 game_server_4002 中用户已加入房间房间,
            // 那么在当前游戏服务器中就不能创建房间!
            //
            // 获取用户所在房间 Id
            String strUserAtRoomId = redisCache.hget(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_AT_ROOM_ID
            );

            if (null != strUserAtRoomId) {
                // 获取房间所在服务器 Id
                String strRoomAtServerId = redisCache.hget(
                    RedisKeyDef.ROOM_X_PREFIX + strUserAtRoomId,
                    RedisKeyDef.ROOM_AT_SERVER_ID
                );

                if (null != strRoomAtServerId &&
                    !String.valueOf(MJ_weihai_BizLogic.getInstance().getServerId()).equals(strRoomAtServerId)) {
                    // 如果房间所在服务器 Id 与当前服务器 Id 不同,
                    // 这说明用户已加入房间,
                    // 而这个房间不在当前服务器中...
                    // 这种情况需要 proxyServer 去协调,
                    // gameServer 本身无能为力!
                    LOGGER.error(
                        "用户已加入房间, userId = {}, roomId = {}, roomAtServerId = {}, XXX 注意: 该房间不在当前服务器中! currServerId = {}",
                        userId,
                        strUserAtRoomId,
                        strRoomAtServerId,
                        MJ_weihai_BizLogic.getInstance().getServerId()
                    );
                    return false;
                }
            }

            // 从本机内存中获取房间
            final Room oldRoom = RoomGroup.getByUserId(userId);

            if (null != oldRoom) {
                LOGGER.warn(
                    "用户已有房间, userId = {}, atRoomId = {}",
                    userId,
                    oldRoom.getRoomId()
                );
                MJ_weihai_BizLogic.getInstance().syncRoomKeyDataToRedis(oldRoom);
                return false;
            }

            // 如果房间为空,
            // 也要同时清理掉 Redis 缓存中的信息
            redisCache.hdel(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_AT_ROOM_ID
            );
            redisCache.hdel(
                RedisKeyDef.ROOM_X_PREFIX + strUserAtRoomId,
                RedisKeyDef.ROOM_AT_SERVER_ID
            );

            return true;
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return false;
    }
}
