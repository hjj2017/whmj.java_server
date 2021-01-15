package org.mj.proxyserver.nobody.router;

import org.mj.bizserver.def.ServerJobTypeEnum;
import org.mj.comm.NettyClient;
import org.mj.comm.util.OutParam;
import org.mj.proxyserver.cluster.NewServerFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 服务器选择者
 */
final class ServerSelector {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ServerSelector.class);

    /**
     * 随机对象
     */
    static private final Random RAND = new Random();

    /**
     * 类默认构造器
     */
    private ServerSelector() {
    }

    /**
     * 根据服务器工作类型随机选择一个服务器连接 ( Netty 客户端 )
     *
     * @param newServerFinder 新服务器发现者
     * @param expectJobType   期望的服务器工作类型
     * @return Netty 客户端
     */
    static public NettyClient randomAServerConnByServerJobType(
        final NewServerFinder newServerFinder, final ServerJobTypeEnum expectJobType) {
        return randomAServerConnByServerJobType(
            newServerFinder, expectJobType, null
        );
    }

    /**
     * 根据服务器工作类型随机选择一个服务器连接 ( Netty 客户端 )
     *
     * @param newServerFinder 新服务器发现者
     * @param expectJobType   期望的服务器工作类型
     * @param out_rev         ( 输出参数 ) 版本号
     * @return Netty 客户端
     */
    static public NettyClient randomAServerConnByServerJobType(
        final NewServerFinder newServerFinder, final ServerJobTypeEnum expectJobType,
        OutParam<Long> out_rev) {
        if (null == expectJobType) {
            return null;
        }

        // 创建一个临时列表
        List<NewServerFinder.ServerProfile> tempList = null;

        for (NewServerFinder.ServerProfile sp : newServerFinder.getServerALL()) {
            if (null == sp ||
                null == sp.getClientConn() ||
                !sp.getClientConn().isReady()) {
                continue;
            }

            if (sp.getServerJobTypeSet().contains(expectJobType.name())) {
                if (null == tempList) {
                    tempList = new ArrayList<>();
                }

                tempList.add(sp);
            }
        }

        if (null == tempList ||
            tempList.isEmpty()) {
            LOGGER.error(
                "服务器列表为空, expectJobType = {}",
                expectJobType
            );
            return null;
        }

        // 选择一个服务器资料
        int selectedIndex = RAND.nextInt(tempList.size());
        NewServerFinder.ServerProfile selectedSp = tempList.get(selectedIndex);

        // 设置版本号
        OutParam.putVal(out_rev, newServerFinder.getRev());
        return selectedSp.getClientConn();
    }

    /**
     * 根据服务器工作类型选择一个 ( 最合适的 ) 服务器连接 ( Netty 客户端 )
     *
     * @param newServerFinder 新服务器发现者
     * @param expectJobType   期望的服务器工作类型
     * @return Netty 客户端
     */
    static public NettyClient selectServerConnByServerJobType(
        final NewServerFinder newServerFinder, final ServerJobTypeEnum expectJobType) {
        return selectServerConnByServerJobType(
            newServerFinder, expectJobType, null
        );
    }

    /**
     * 根据服务器工作类型选择一个 ( 最合适的 ) 服务器连接 ( Netty 客户端 )
     *
     * @param newServerFinder 新服务器发现者
     * @param expectJobType   期望的服务器工作类型
     * @param out_rev         ( 输出参数 ) 版本号
     * @return Netty 客户端
     */
    static public NettyClient selectServerConnByServerJobType(
        final NewServerFinder newServerFinder, final ServerJobTypeEnum expectJobType,
        OutParam<Long> out_rev) {
        if (null == expectJobType) {
            return null;
        }

        // 选择一个服务器资料
        NewServerFinder.ServerProfile selectedSp = null;
        int minLoadCount = Integer.MAX_VALUE;

        for (NewServerFinder.ServerProfile sp : newServerFinder.getServerALL()) {
            if (null == sp ||
                null == sp.getClientConn() ||
                !sp.getClientConn().isReady()) {
                continue;
            }

            if (!sp.getClientConn().getServerJobTypeSet().contains(expectJobType.name())) {
                continue;
            }

            if (minLoadCount > sp.getLoadCount()) {
                minLoadCount = sp.getLoadCount();
                selectedSp = sp;
            }
        }

        if (null == selectedSp) {
            LOGGER.error(
                "未找到合适的服务器资料, expectJobType = {}",
                expectJobType
            );
            return null;
        }

        // 设置版本号
        OutParam.putVal(out_rev, newServerFinder.getRev());
        return selectedSp.getClientConn();
    }

    /**
     * 根据服务器 Id 获取服务器连接
     *
     * @param serverId 服务器 Id
     * @return 服务器连接
     * @see #getServerConnByServerId(NewServerFinder, int, OutParam)
     */
    static public NettyClient getServerConnByServerId(
        final NewServerFinder newServerFinder, final int serverId) {
        return getServerConnByServerId(
            newServerFinder, serverId, null
        );
    }

    /**
     * 根据服务器 Id 获取服务器连接
     *
     * @param serverId 服务器 Id
     * @param out_rev  ( 输出参数 ) 版本号
     * @return 服务器连接
     */
    static public NettyClient getServerConnByServerId(
        final NewServerFinder newServerFinder, final int serverId,
        OutParam<Long> out_rev) {
        // 获取服务器资料
        NewServerFinder.ServerProfile sp = newServerFinder.getServerById(serverId);

        if (null == sp ||
            null == sp.getClientConn() ||
            !sp.getClientConn().isReady()) {
            return null;
        }

        OutParam.putVal(out_rev, newServerFinder.getRev());
        return sp.getClientConn();
    }
}
