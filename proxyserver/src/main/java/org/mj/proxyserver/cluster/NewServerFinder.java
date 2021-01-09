package org.mj.proxyserver.cluster;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.PubSubChannelDef;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.comm.NettyClient;
import org.mj.comm.pubsub.MySubscriber;
import org.mj.comm.util.RedisXuite;
import org.mj.proxyserver.ProxyServer;
import org.mj.proxyserver.foundation.ChannelHandlerFactoryImpl_0;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 新服务器发现者
 */
public class NewServerFinder implements MySubscriber.IMsgHandler {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(NewServerFinder.class);

    /**
     * 单例对象
     */
    static private final NewServerFinder _instance = new NewServerFinder();

    /**
     * 服务器资料字典, key = serverId
     */
    private final Map<Integer, ServerProfile> _spMap = new ConcurrentHashMap<>();

    /**
     * 版本号
     */
    private final AtomicLong _rev = new AtomicLong(0L);

    /**
     * 排序的服务器资料列表
     */
    private List<ServerProfile> _sortedSpList = null;

    /**
     * 私有化类默认构造器
     */
    private NewServerFinder() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public NewServerFinder getInstance() {
        return _instance;
    }

    @Override
    public void handle(String ch, String strMsg) {
        if (!PubSubChannelDef.NEW_SERVER_COME_IN.equals(ch) ||
            null == strMsg ||
            strMsg.isEmpty()) {
            return;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 获取服务器 Id
            final int newServerId = Integer.parseInt(strMsg);
            // 从 Redis 中获取服务器信息
            final String redisKey = RedisKeyDef.SERVER_X_PREFIX + newServerId;
            String strServerInfo = redisCache.get(redisKey);

            if (null == strServerInfo) {
                LOGGER.error(
                    "未发现新服务器数据, serverId = {}",
                    newServerId
                );
                return;
            }

            // 解析 JSON 对象
            JSONObject joServerInfo = JSONObject.parseObject(strServerInfo);

            // 获取服务器资料
            ServerProfile sp = _spMap.get(newServerId);

            if (null == sp) {
                // 记录日志
                LOGGER.info(
                    "发现并添加新服务器, serverId = {}",
                    newServerId
                );

                _spMap.putIfAbsent(newServerId, new ServerProfile());
                _sortedSpList = null;
                sp = _spMap.get(newServerId);
            }

            // 更新负载数量
            sp.setLoadCount(joServerInfo.getIntValue("loadCount"));

            if (null == sp.getClientConn() ||
                !sp.getClientConn().isReady()) {
                // 连接到新服务器
                sp.setClientConn(connToNewServer(joServerInfo));
            }

            if (null == sp.getClientConn()) {
                LOGGER.error(
                    "连接服务器失败, serverId = {}",
                    newServerId
                );
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 连接新服务器
     *
     * @param joServerInfo 服务器信息
     * @return Nett 客户端
     */
    private NettyClient connToNewServer(JSONObject joServerInfo) {
        if (null == joServerInfo) {
            return null;
        }

        NettyClient.Config clientConf = NettyClient.Config.fromJSONObj(joServerInfo);
        clientConf.setChannelHandlerFactory(new ChannelHandlerFactoryImpl_0());
        clientConf.setCloseCallback((closeClient) -> {
            if (null == closeClient) {
                return;
            }

            // 获取服务器 Id
            final int serverId = closeClient.getServerId();
            // 如果断线就删除
            ServerProfile sp = _spMap.remove(serverId);
            _sortedSpList = null;

            if (null != sp) {
                sp.setClientConn(null);
            }

            // 更新版本号,
            // 版本号 = 当前时间戳
            _rev.set(System.currentTimeMillis());
        });

        NettyClient serverConn = new NettyClient(clientConf);
        serverConn.putExtraInfo("proxyServerId", String.valueOf(ProxyServer.getId()));
        serverConn.conn();

        if (!serverConn.isReady()) {
            return null;
        }

        // 更新版本号,
        // 版本号 = 当前时间戳
        _rev.set(System.currentTimeMillis());
        return serverConn;
    }

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    public long getRev() {
        return _rev.get();
    }

    /**
     * 获取所有服务器
     *
     * @return 服务器资料列表
     */
    public List<ServerProfile> getServerALL() {
        if (null == _sortedSpList) {
            _sortedSpList = new ArrayList<>(_spMap.values());
            _sortedSpList.sort(Comparator.comparingInt(ServerProfile::getServerId));
        }

        return _sortedSpList;
    }

    /**
     * 根据 Id 获取服务器门面
     *
     * @param serverId 服务器 Id
     * @return 服务器门面
     */
    public ServerProfile getServerById(int serverId) {
        return _spMap.get(serverId);
    }

    /**
     * 服务器资料,
     * 包括客户端连接和负载数量
     */
    public static class ServerProfile {
        /**
         * 客户端连接
         */
        private NettyClient _clientConn;

        /**
         * 负载数量
         */
        private int _loadCount;

        /**
         * 获取客户端连接
         *
         * @return 客户端连接
         */
        public NettyClient getClientConn() {
            return _clientConn;
        }

        /**
         * 设置客户端连接
         *
         * @param val 对象值
         */
        public void setClientConn(NettyClient val) {
            _clientConn = val;
        }

        /**
         * 获取负载数量
         *
         * @return 负载数量
         */
        public int getLoadCount() {
            return _loadCount;
        }

        /**
         * 设置负载数量
         *
         * @param val 整数值
         */
        public void setLoadCount(int val) {
            _loadCount = val;
        }

        /**
         * 获取服务器 Id
         *
         * @return 服务器 Id
         */
        public int getServerId() {
            if (null == _clientConn) {
                return -1;
            } else {
                return _clientConn.getServerId();
            }
        }

        /**
         * 获取服务器工作类型集合
         *
         * @return 服务器工作类型集合
         */
        public Set<String> getServerJobTypeSet() {
            if (null == _clientConn) {
                return Collections.emptySet();
            } else {
                return _clientConn.getServerJobTypeSet();
            }
        }
    }
}
