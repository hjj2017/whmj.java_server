package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.base.AliIpv4LocationZervice;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 测量地理距离指令处理器
 */
public class MeasureGeoDistanceCmdHandler implements ICmdHandler<MyCmdHandlerContext, MJ_weihai_Protocol.MeasureGeoDistanceCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MeasureGeoDistanceCmdHandler.class);

    /**
     * 单位千米
     */
    static private final float UNIT_OF_KM = 111.0f;

    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        MJ_weihai_Protocol.MeasureGeoDistanceCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        // 获取当前房间
        final Room currRoom = RoomGroup.getByUserId(ctx.getFromUserId());

        if (null == currRoom) {
            return;
        }

        // 尝试修复地理位置, 之后构建消息并发送...
        tryFixGeoLocation(currRoom, (dummy) -> {
            buildMsgAndSend(ctx, currRoom);
            return null;
        });
    }

    /**
     * 尝试修复地理位置,
     * 也就是说: 如果客户端没有上报地理位置坐标, 那么就根据 IP 地址查询地理位置
     *
     * @param currRoom 当前房间
     * @param callback 回调函数
     */
    static private void tryFixGeoLocation(
        final Room currRoom, final Function<Void, Void> callback) {
        if (null == currRoom ||
            null == callback) {
            return;
        }

        // 获取玩家列表
        final List<Player> playerList = currRoom.getPlayerListCopy();

        if (null == playerList ||
            playerList.isEmpty()) {
            callback.apply(null);
            return;
        }

        // IP 地址集合
        Set<String> ipAddrSet = null;

        for (Player currPlayer : playerList) {
            if (null == currPlayer) {
                continue;
            }

            if (0 != currPlayer.getGeoLocation().getLatitude() &&
                0 != currPlayer.getGeoLocation().getLongitude()) {
                // 如果玩家已有经度和纬度,
                // 则无需修复...
                continue;
            }

            if (null == currPlayer.getClientIpAddr() ||
                "127.0.0.1".equals(currPlayer.getClientIpAddr())) {
                // 本机地址就不要查询了...
                continue;
            }

            if (null == ipAddrSet) {
                ipAddrSet = new HashSet<>();
            }

            // 否则就需要修复
            ipAddrSet.add(
                currPlayer.getClientIpAddr()
            );
        }

        if (null == ipAddrSet ||
            ipAddrSet.isEmpty()) {
            // 如果没有任何玩家需要修复,
            // 则直接退出...
            callback.apply(null);
            return;
        }

        AliIpv4LocationZervice.getInstance().queryGeoLocation_async(
            ipAddrSet, (resultX) -> {
                final Map<String, AliIpv4LocationZervice.GeoLocation> ipAndGeoLocationMap = resultX.getFinalResult();

                if (null == ipAndGeoLocationMap ||
                    ipAndGeoLocationMap.isEmpty()) {
                    LOGGER.error(
                        "查询地理位置结果为空, atRoomId = {}",
                        currRoom.getRoomId()
                    );
                    callback.apply(null);
                    return;
                }

                for (Player currPlayer : playerList) {
                    if (null == currPlayer) {
                        continue;
                    }

                    if (0 != currPlayer.getGeoLocation().getLatitude() &&
                        0 != currPlayer.getGeoLocation().getLongitude()) {
                        continue;
                    }

                    // 获取地理位置
                    AliIpv4LocationZervice.GeoLocation geoLocation = ipAndGeoLocationMap.get(currPlayer.getClientIpAddr());

                    if (null == geoLocation) {
                        continue;
                    }

                    LOGGER.error(
                        "根据 IP 地址找到地理位置, userId = {}, clientIpAddr = {}, 经度 = {}, 纬度 = {}",
                        currPlayer.getUserId(),
                        currPlayer.getClientIpAddr(),
                        geoLocation.getLongitude(),
                        geoLocation.getLatitude()
                    );

                    currPlayer.getGeoLocation()
                        .setLatitude(geoLocation.getLatitude())
                        .setLongitude(geoLocation.getLongitude());
                }

                callback.apply(null);
            }
        );
    }

    /**
     * 构建消息并发送
     *
     * @param ctx      客户端信道处理器上下文
     * @param currRoom 当前房间
     */
    static private void buildMsgAndSend(
        final MyCmdHandlerContext ctx, final Room currRoom) {

        if (null == ctx ||
            null == currRoom) {
            return;
        }

        MJ_weihai_Protocol.MeasureGeoDistanceResult.Builder
            b0 = MJ_weihai_Protocol.MeasureGeoDistanceResult.newBuilder();

        // 获取玩家列表
        final List<Player> playerList = currRoom.getPlayerListCopy();

        for (int i = 0; i < playerList.size() - 1; i++) {
            // 获取玩家 1
            Player p0 = playerList.get(i);

            for (int j = i + 1; j < playerList.size(); j++) {
                // 获取玩家 2
                Player p1 = playerList.get(j);
                // 测量地理距离
                float d = measureGeoDistance(p0, p1);

                LOGGER.info(
                    "测量地理距离, atRoomId = {}, p0.userId = {}, p1.userId = {}, d = {}",
                    currRoom.getRoomId(),
                    p0.getUserId(),
                    p1.getUserId(),
                    d // 地理距离, 单位 = 千米
                );

                MJ_weihai_Protocol.MeasureGeoDistanceResult.GeoDistanceItem.Builder
                    b1 = MJ_weihai_Protocol.MeasureGeoDistanceResult.GeoDistanceItem.newBuilder();

                b1.setUserIdA(p0.getUserId());
                b1.setUserIdB(p1.getUserId());
                b1.setDistance(d);
                b1.setSameIpAddr(p0.getClientIpAddr().equals(p1.getClientIpAddr()));

                // 添加地理距离条目
                b0.addGeoDistanceItem(b1);
            }
        }

        ctx.writeAndFlush(b0.build());
    }

    /**
     * 测量距离
     *
     * @param p0 玩家 1
     * @param p1 玩家 2
     * @return 地理距离, 单位 = 米
     */
    static private float measureGeoDistance(final Player p0, final Player p1) {
        if (null == p0 ||
            null == p1) {
            return -1;
        }

        if (0 == p0.getGeoLocation().getLatitude() ||
            0 == p0.getGeoLocation().getLongitude() ||
            0 == p1.getGeoLocation().getLatitude() ||
            0 == p1.getGeoLocation().getLongitude()) {
            return -1;
        }

        float dX = p0.getGeoLocation().getLongitude() - p1.getGeoLocation().getLongitude();
        float dY = p0.getGeoLocation().getLatitude() - p1.getGeoLocation().getLatitude();

        dX = Math.abs(dX);
        dY = Math.abs(dY);

        float w = Math.max(dX, dY);
        float h = Math.max(dX, dY);

        return (w + h / 2) * UNIT_OF_KM;
    }
}
