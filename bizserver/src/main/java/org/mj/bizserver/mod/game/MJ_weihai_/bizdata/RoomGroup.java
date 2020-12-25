package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 房间分组
 */
public final class RoomGroup {
    /**
     * 房间字典
     */
    static private final Map<Integer, Room> _roomMap = new ConcurrentHashMap<>();

    /**
     * 私有化类默认构造器
     */
    private RoomGroup() {
    }

    /**
     * 添加房间
     *
     * @param newRoom 新房间
     */
    static public void add(Room newRoom) {
        if (null == newRoom) {
            return;
        }

        _roomMap.putIfAbsent(newRoom.getRoomId(), newRoom);
    }

    /**
     * 根据房间 Id 获取房间
     *
     * @param roomId 房间 Id
     * @return 房间
     */
    static public Room getByRoomId(int roomId) {
        return _roomMap.getOrDefault(roomId, null);
    }

    /**
     * 根据用户 Id 获取房间
     *
     * @param userId 用户 Id
     * @return 房间
     */
    static public Room getByUserId(int userId) {
        for (Room currRoom : _roomMap.values()) {
            // 获取当前房间
            if (null != currRoom &&
                null != currRoom.getPlayerByUserId(userId)) {
                return currRoom;
            }
        }

        return null;
    }

    /**
     * 获取所有房间
     *
     * @return 房间集合
     */
    static public Collection<Room> getAllRoom() {
        return _roomMap.values();
    }

    /**
     * 根据房间 Id 移除房间
     *
     * @param roomId 房间 Id
     */
    static public void removeByRoomId(int roomId) {
        _roomMap.remove(roomId);
    }

    /**
     * 获取用户数量
     *
     * @return 用户数量
     */
    static public int getAllUserzCount() {
        int sum = 0;

        for (Room currRoom : _roomMap.values()) {
            if (null == currRoom) {
                continue;
            }

            sum += currRoom.getPlayerListCopy().size();
        }

        return sum;
    }
}
