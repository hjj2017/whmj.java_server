package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 上报地理位置指令处理器
 */
public class ReportGeoLocationCmdHandler implements ICmdHandler<MJ_weihai_Protocol.ReportGeoLocationCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ReportGeoLocationCmdHandler.class);

    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        MJ_weihai_Protocol.ReportGeoLocationCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        // 获取当前房间
        final Room currRoom = RoomGroup.getByUserId(fromUserId);

        if (null == currRoom) {
            return;
        }

        // 获取当前玩家
        final Player currPlayer = currRoom.getPlayerByUserId(
            fromUserId
        );

        if (null == currPlayer) {
            return;
        }

        LOGGER.info(
            "上报地理位置, userId = {}, atRoomId = {}, 经度 = {}, 纬度 = {}, 海拔 = {}, ipAddr = {}",
            fromUserId,
            currRoom.getRoomId(),
            cmdObj.getLongitude(),
            cmdObj.getLatitude(),
            cmdObj.getAltitude(),
            cmdObj.getClientIpAddr()
        );

        if (0 == cmdObj.getLatitude() ||
            0 == cmdObj.getLongitude()) {
            // 只设置 IP 地址
            currPlayer.getGeoLocation()
                .setClientIpAddr(cmdObj.getClientIpAddr());
        } else {
            // 设置 IP 地址和地理位置
            currPlayer.getGeoLocation()
                .setClientIpAddr(cmdObj.getClientIpAddr())
                .setLatitude(cmdObj.getLatitude())
                .setLongitude(cmdObj.getLongitude())
                .setAltitude(cmdObj.getAltitude());
        }
    }
}
