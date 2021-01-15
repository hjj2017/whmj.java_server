package org.mj.proxyserver.nobody.router;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.mj.bizserver.def.ServerJobTypeEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 路由表, 主要是记录客户端到具体的业务服务器 Id
 */
public final class RouteTable {
    /**
     * 会话主键
     */
    static public final String SESSION_KEY_ROUTE_TABLE = "mj_route_table";

    /**
     * 已经选择的服务器字典
     */
    private final Map<ServerJobTypeEnum, SelectedServer> _selectedServerMap = new ConcurrentHashMap<>();

    /**
     * 获取关注亲友圈 Id
     */
    private final AtomicInteger _focusClubId = new AtomicInteger(-1);

    /**
     * 类默认构造器
     */
    private RouteTable() {
    }

    /**
     * 获取路由表, 如果为空就创建
     *
     * @param ctx 信道处理器上下文
     * @return 路由表
     */
    static public RouteTable getOrCreate(ChannelHandlerContext ctx) {
        if (null != ctx) {
            return getOrCreate(ctx.channel());
        } else {
            return null;
        }
    }

    /**
     * 获取路由表, 如果为空就创建
     *
     * @param ch 客户端信道
     * @return 路由表
     */
    static public RouteTable getOrCreate(Channel ch) {
        if (null == ch) {
            return null;
        }

        // 获取属性关键字
        AttributeKey<RouteTable> aKey = AttributeKey.valueOf(SESSION_KEY_ROUTE_TABLE);
        // 获取路由表
        RouteTable rt = ch.attr(aKey).get();

        if (null == rt) {
            ch.attr(aKey).setIfAbsent(new RouteTable());
        }

        return ch.attr(aKey).get();
    }

    /**
     * 根据服务器工作类型获取服务器 Id
     *
     * @param currJobType 服务器工作类型
     * @return 服务器 Id
     */
    public int getServerId(ServerJobTypeEnum currJobType) {
        if (null == currJobType) {
            return -1;
        }

        // 获取已经选择的服务器
        SelectedServer selServer = _selectedServerMap.get(currJobType);
        return (null == selServer) ? -1 : selServer.getServerId();
    }

    /**
     * 根据服务器工作类型获取版本号,
     * 也就是路由表中记录的关于当前服务器工作类型的版本号
     *
     * @param currJobType 服务器工作类型
     * @return 版本号
     */
    public long getRev(ServerJobTypeEnum currJobType) {
        if (null == currJobType) {
            return -1;
        }

        // 获取已经选择的服务器
        SelectedServer selServer = _selectedServerMap.get(currJobType);
        return (null == selServer) ? -1L : selServer.getRev();
    }

    /**
     * 设置服务器 Id
     *
     * @param currJobType 服务器工作类型
     * @param serverId    服务器 Id
     */
    public void putServerId(ServerJobTypeEnum currJobType, int serverId) {
        putServerIdAndRev(currJobType, serverId, -1L);
    }

    /**
     * 设置服务器 Id 和版本号
     *
     * @param currJobType 服务器工作类型
     * @param serverId    服务器 Id
     * @param rev         版本号
     */
    public void putServerIdAndRev(ServerJobTypeEnum currJobType, int serverId, long rev) {
        if (null == currJobType) {
            return;
        }

        _selectedServerMap.put(
            currJobType,
            new SelectedServer(serverId, rev)
        );
    }

    /**
     * 移除服务器 Id 和版本号
     *
     * @param currJobType 服务器工作类型
     */
    public void removeServerIdAndRev(ServerJobTypeEnum currJobType) {
        if (null != currJobType) {
            _selectedServerMap.remove(currJobType);
        }
    }

    /**
     * 获取关注的亲友圈 Id
     *
     * @return 亲友圈 Id
     */
    public int getFocusClubId() {
        return _focusClubId.get();
    }

    /**
     * 设置关注的亲友圈 Id
     *
     * @param val 整数值
     */
    public void putFocusClubId(int val) {
        _focusClubId.set(val);
    }

    /**
     * 已经选择的服务器
     */
    static private final class SelectedServer {
        /**
         * 服务器 Id
         */
        private final int _serverId;

        /**
         * 版本号
         */
        private final long _rev;

        /**
         * 类参数构造器
         *
         * @param serverId 服务器 Id
         * @param rev      版本号
         */
        public SelectedServer(int serverId, long rev) {
            _serverId = serverId;
            _rev = rev;
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
         * 获取版本号
         *
         * @return 版本号
         */
        public long getRev() {
            return _rev;
        }
    }
}
