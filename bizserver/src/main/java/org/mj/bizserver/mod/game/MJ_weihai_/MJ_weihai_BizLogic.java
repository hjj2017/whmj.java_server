package org.mj.bizserver.mod.game.MJ_weihai_;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.PubSubChannelDef;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongLiangFeng;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.comm.async.IContinueWith;
import org.mj.comm.pubsub.MyPublisher;
import org.mj.comm.util.MyTimer;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 威海麻将业务逻辑
 */
public final class MJ_weihai_BizLogic implements
    MJ_weihai_BizLogic$beginNewRound,
    MJ_weihai_BizLogic$buFeng,
    MJ_weihai_BizLogic$chi,
    MJ_weihai_BizLogic$chuPai,
    MJ_weihai_BizLogic$createRoom,
    MJ_weihai_BizLogic$dingPiao,
    MJ_weihai_BizLogic$fireAPlayer,
    MJ_weihai_BizLogic$liangFeng,
    MJ_weihai_BizLogic$gang,
    MJ_weihai_BizLogic$guo,
    MJ_weihai_BizLogic$hu,
    MJ_weihai_BizLogic$joinRoom,
    MJ_weihai_BizLogic$moPai,
    MJ_weihai_BizLogic$peng,
    MJ_weihai_BizLogic$prepare,
    MJ_weihai_BizLogic$quitRoom {

    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic.class);

    /**
     * 单例对象
     */
    static private final MJ_weihai_BizLogic _instance = new MJ_weihai_BizLogic();

    /**
     * 服务器 Id
     */
    private int _serverId;

    /**
     * 自定义发布者
     */
    private final MyPublisher _publisher = new MyPublisher();

    /**
     * 私有化类默认构造器
     */
    private MJ_weihai_BizLogic() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public MJ_weihai_BizLogic getInstance() {
        return _instance;
    }

    /**
     * 获取服务器 Id
     *
     * @return 服务器 Id
     */
    public int getServerId() {
        return _serverId;
    }

    /**
     * 设置服务器 Id
     *
     * @param serverId 服务器 Id
     */
    public void setServerId(int serverId) {
        _serverId = serverId;
    }

    /**
     * 根据房间 Id 判定是否存在房间
     *
     * @param roomId 房间 Id
     * @return true =
     */
    public boolean hasRoom(int roomId) {
        return null != RoomGroup.getByRoomId(roomId);
    }

    /**
     * 查找可以暗杠的麻将牌
     *
     * @param currPlayer 当前玩家
     * @return 可以暗杠的麻将牌
     */
    MahjongTileDef findMahjongCanAnGang(Player currPlayer) {
        if (null == currPlayer) {
            return null;
        }

        final Map<MahjongTileDef, Integer> counterMap = new HashMap<>();

        if (null != currPlayer.getMoPai()) {
            // 先算上手里的牌
            counterMap.put(currPlayer.getMoPai(), 1);
        }

        for (MahjongTileDef tCurr : currPlayer.getMahjongInHandCopy()) {
            int counter = counterMap.getOrDefault(tCurr, 0);
            counterMap.put(tCurr, ++counter);

            if (counter >= 4) {
                return tCurr;
            }
        }

        return null;
    }

    /**
     * 查找可以补风的麻将牌,
     * XXX 注意: 亮风和补风玩法是威海特色玩法
     *
     * @param currPlayer 当前玩家
     * @return 可以补风的麻将牌
     */
    MahjongTileDef findMahjongCanBuFeng(Player currPlayer) {
        if (null == currPlayer) {
            return null;
        }

        // 获取亮风种类
        final MahjongLiangFeng.KindDef kind = currPlayer.getMahjongLiangFeng().getKind();

        if (null == kind) {
            // 没有亮风则不可能提示补风
            return null;
        }

        for (MahjongTileDef currT : currPlayer.getMahjongInHandCopy()) {
            if (null == currT) {
                continue;
            }

            if (MahjongTileDef.Suit.FENG != currT.getSuit() &&
                MahjongTileDef.Suit.JIAN != currT.getSuit()) {
                // 不是东南西北中发白则不可能提示补风
                continue;
            }

            if (kind == MahjongLiangFeng.KindDef.LUAN_MAO ||
                currT.getSuit() == kind.getMahjongSuit()) {
                // 如果是乱锚,
                // 或者如果当前牌花色和亮风牌花色相同,
                // 那么当前麻将牌可以补风...
                return currT;
            }
        }

        // 获取摸牌
        final MahjongTileDef moPai = currPlayer.getMoPai();

        if (null == moPai) {
            return null;
        }

        if (moPai.getSuit() != MahjongTileDef.Suit.FENG &&
            moPai.getSuit() != MahjongTileDef.Suit.JIAN) {
            return null;
        }

        if (kind == MahjongLiangFeng.KindDef.LUAN_MAO ||
            moPai.getSuit() == kind.getMahjongSuit()) {
            // 如果是乱锚,
            // 或者如果摸牌花色和亮风牌花色相同,
            // 那么当前麻将牌可以补风...
            return moPai;
        }

        return null;
    }

    /**
     * 同步房间关键数据到 Redis,
     * 例如: 房间所在服务器 Id, 用户所在房间 Id
     *
     * @param currRoom 房间
     */
    void syncRoomKeyDataToRedis(Room currRoom) {
        if (null == currRoom) {
            return;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 计算房间关键字
            final String roomKey = RedisKeyDef.ROOM_X_PREFIX + currRoom.getRoomId();

            // 设置房间所在服务器 Id
            redisCache.hset(
                roomKey,
                RedisKeyDef.ROOM_AT_SERVER_ID,
                String.valueOf(_serverId)
            );

            // 设置房间详情
            redisCache.hset(
                roomKey,
                RedisKeyDef.ROOM_DETAILZ,
                currRoom.toJSON().toJSONString()
            );

            if (currRoom.getClubId() > 0) {
                // 如果是亲友圈内的牌桌,
                redisCache.hset(
                    RedisKeyDef.CLUB_X_PREFIX + currRoom.getClubId(),
                    RedisKeyDef.CLUB_TABLE_X_PREFIX + currRoom.getTableSeqNum(),
                    String.valueOf(currRoom.getRoomId())
                );
            }

            for (Player currPlayer : currRoom.getPlayerListCopy()) {
                if (null == currPlayer) {
                    continue;
                }

                redisCache.hset(
                    RedisKeyDef.USER_X_PREFIX + currPlayer.getUserId(),
                    RedisKeyDef.USER_AT_ROOM_ID,
                    String.valueOf(currRoom.getRoomId())
                );
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 广播亲友圈牌桌变化
     *
     * @param currRoom 当前房间
     */
    void broadcastAClubTableChanged(Room currRoom) {
        if (null == currRoom ||
            currRoom.getClubId() <= 0) {
            return;
        }

        // 创建 JSON 对象并发送消息
        JSONObject jsonObj = new JSONObject(true);
        jsonObj.put("roomId", currRoom.getRoomId());
        jsonObj.put("clubId", currRoom.getClubId());
        jsonObj.put("tableSeqNum", currRoom.getTableSeqNum());

        _publisher.publish(
            PubSubChannelDef.A_CLUB_TABLE_CHANGED,
            jsonObj.toJSONString()
        );
    }

    /**
     * 当前牌局结束
     *
     * @param currRoom  当前房间
     * @param currRound 当前牌局
     */
    void onCurrRoundEnded(final Room currRoom, final Round currRound) {
        if (null == currRoom ||
            null == currRound) {
            return;
        }

        if (RoomOverDetermine.determine(currRoom)) {
            LOGGER.info(
                "所有牌局全部结束, atRoomId = {}",
                currRoom.getRoomId()
            );

            // 移除房间
            RoomGroup.removeByRoomId(currRoom.getRoomId());
        }

        AsyncOperationProcessorSingleton.getInstance().process(
            currRoom.getRoomId(),
            () -> {
                if (0 == currRound.getRoundIndex() &&
                    !currRoom.isForcedEnd()) {
                    // 如果当前牌局是第一局,
                    // 并且当前房间不是强制结束的 ( 不是解散的 ),
                    // 就执行房卡消耗逻辑...
                    RoomCardCashier.costRoomCard(currRoom);
                }

                // 创建房间日志实体并保存
                EntityLogger.createRoomEntityLogAndSave(
                    currRoom
                );

                // 创建牌局日志实体并保存
                EntityLogger.createRoundLogEntityAndSave(
                    currRoom.getRoomUUId(),
                    currRound
                );

                // 保存回放数据到阿里 OSS
                PlaybackLogger.savePlaybackToAliOSS(
                    currRoom.getCreateTime(),
                    currRound
                );

                // 记录用户和游戏日志
                EntityLogger.createUserGameLogAndSave(currRoom);

                if (RoomOverDetermine.determine(currRoom) ||
                    currRoom.isForcedEnd()) {
                    // 如果是所有牌局都结束了,
                    // 则执行清理和释放资源的过程...
                    MJ_weihai_BizLogic.getInstance().cleanUpRedisAndFree(currRoom);
                }
            }
        );
    }

    /**
     * ( 异步方式 ) 清理 Redis 并释放资源
     *
     * @param currRoom 当前房间
     */
    public void cleanUpRedisAndFree_async(final Room currRoom) {
        cleanUpRedisAndFree_async(currRoom, null);
    }

    /**
     * ( 异步方式 ) 清理 Redis 并释放资源
     *
     * @param currRoom     当前房间
     * @param continueWith 异步操作完成之后继续执行的逻辑
     */
    public void cleanUpRedisAndFree_async(final Room currRoom, IContinueWith continueWith) {
        if (null == currRoom) {
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            currRoom.getRoomId(),
            () -> cleanUpRedisAndFree(currRoom),
            continueWith
        );
    }

    /**
     * 清理 Redis 并释放资源,
     * XXX 注意: 释放的时候不是立即执行的, 而是在调用完该函数 15 秒之后执行
     *
     * @param currRoom 当前房间
     */
    private void cleanUpRedisAndFree(final Room currRoom) {
        if (null == currRoom) {
            return;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 计算房间关键字
            final String roomKey = RedisKeyDef.ROOM_X_PREFIX + currRoom.getRoomId();

            // 从 Redis 中删除房间数据
            redisCache.del(roomKey);

            for (Player currPlayer : currRoom.getPlayerListCopy()) {
                if (null == currPlayer) {
                    continue;
                }

                redisCache.hdel(
                    RedisKeyDef.USER_X_PREFIX + currPlayer.getUserId(),
                    RedisKeyDef.USER_AT_ROOM_ID
                );
            }

            // 广播亲友圈牌桌变化
            broadcastAClubTableChanged(currRoom);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            MyTimer.schedule(() -> {
                currRoom.free();

                LOGGER.info(
                    "房间占用资源已经全部释放, roomId = {}",
                    currRoom.getRoomId()
                );
            }, 15, TimeUnit.SECONDS);
        }
    }
}
