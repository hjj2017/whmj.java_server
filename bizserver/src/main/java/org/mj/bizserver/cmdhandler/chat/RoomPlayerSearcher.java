package org.mj.bizserver.cmdhandler.chat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间玩家搜寻者
 */
class RoomPlayerSearcher {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RoomPlayerSearcher.class);

    /**
     * 私有化类默认构造器
     */
    private RoomPlayerSearcher() {
    }

    /**
     * 根据用户 Id 搜寻同在一个房间内的其他玩家,
     * XXX 注意: 因为是借助 Redis 来实现的, 所需应当放在异步现成里来执行...
     *
     * @param fromUserId 来自用户 Id
     * @return 搜寻结果列表
     */
    static public List<SearchResult> searchOtherzByUserId(int fromUserId) {
        if (fromUserId <= 0) {
            return null;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 获取用户所在房间 Id
            String strRoomId = redisCache.hget(
                RedisKeyDef.USER_X_PREFIX + fromUserId,
                RedisKeyDef.USER_AT_ROOM_ID
            );

            if (null == strRoomId ||
                strRoomId.isEmpty()) {
                return null;
            }

            // 获取房间详情
            String strRoomDetailz = redisCache.hget(
                RedisKeyDef.ROOM_X_PREFIX + strRoomId,
                RedisKeyDef.ROOM_DETAILZ
            );

            if (null == strRoomDetailz ||
                strRoomDetailz.isEmpty() ||
                !strRoomDetailz.startsWith("{")) {
                return null;
            }

            JSONObject joCurrRoom = JSONObject.parseObject(strRoomDetailz);

            if (null == joCurrRoom ||
                joCurrRoom.isEmpty()) {
                LOGGER.error(
                    "未找到游戏房间, userId = {}",
                    fromUserId
                );
                return null;
            }

            // 获取玩家数组
            final JSONArray jaPlayerArray = joCurrRoom.getJSONArray("playerArray");

            List<SearchResult> resultList = null;

            for (int i = 0; i < jaPlayerArray.size(); i++) {
                // 获取当前玩家
                JSONObject joCurrPlayer = jaPlayerArray.getJSONObject(i);

                if (null == joCurrPlayer ||
                    joCurrPlayer.isEmpty()) {
                    continue;
                }

                // 获取令一个用户 Id
                int otherUserId = joCurrPlayer.getIntValue("userId");

                if (otherUserId == fromUserId) {
                    continue;
                }

                List<String> valList = redisCache.hmget(
                    RedisKeyDef.USER_X_PREFIX + otherUserId,
                    RedisKeyDef.USER_AT_PROXY_SERVER_ID,
                    RedisKeyDef.USER_REMOTE_SESSION_ID
                );

                // 获取代理服务器 Id 和远程会话 Id
                int otherProxyServerId = Integer.parseInt(valList.get(0));
                int otherRemoteSessionId = Integer.parseInt(valList.get(1));

                if (null == resultList) {
                    resultList = new ArrayList<>();
                }

                resultList.add(new SearchResult(
                    otherUserId,
                    otherProxyServerId,
                    otherRemoteSessionId
                ));
            }

            return resultList;
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 搜寻结果
     */
    static public class SearchResult {
        /**
         * 用户 Id
         */
        private final int _userId;

        /**
         * 所在代理服务器 Id
         */
        private final int _atProxyServerId;

        /**
         * 远程会话 Id
         */
        private final int _remoteSessionId;

        /**
         * 私有化类参数构造器
         *
         * @param userId          用户 Id
         * @param atProxyServerId 所在代理服务器 Id
         * @param remoteSessionId 远程会话 Id
         */
        private SearchResult(int userId, int atProxyServerId, int remoteSessionId) {
            _userId = userId;
            _atProxyServerId = atProxyServerId;
            _remoteSessionId = remoteSessionId;
        }

        /**
         * 获取用户 Id
         *
         * @return 用户 Id
         */
        public int getUserId() {
            return _userId;
        }

        /**
         * 获取所在代理服务器 Id
         *
         * @return 所在代理服务器 Id
         */
        public int getAtProxyServerId() {
            return _atProxyServerId;
        }

        /**
         * 获取远程会话 Id
         *
         * @return 远程会话 Id
         */
        public int getRemoteSessionId() {
            return _remoteSessionId;
        }
    }
}
