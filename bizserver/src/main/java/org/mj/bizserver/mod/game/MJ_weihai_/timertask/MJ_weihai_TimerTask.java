package org.mj.bizserver.mod.game.MJ_weihai_.timertask;

import org.mj.bizserver.base.MainThreadProcessorSingleton;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.comm.util.MyTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 定时任务
 */
public class MJ_weihai_TimerTask {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_TimerTask.class);

    /**
     * 单例对象
     */
    static private final MJ_weihai_TimerTask _instance = new MJ_weihai_TimerTask();

    /**
     * 最大时间差
     */
    static private final long MAX_TIME_DIFF = 3000;

    /**
     * 是否已经启动
     */
    private final AtomicBoolean _started = new AtomicBoolean(false);

    /**
     * 私有化类默认构造器
     */
    private MJ_weihai_TimerTask() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public MJ_weihai_TimerTask getInstance() {
        return _instance;
    }

    /**
     * 启动
     */
    public void startUp() {
        if (_started.compareAndSet(false, true)) {
            MyTimer.scheduleWithFixedDelay(this::doHeartbeat, 1, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * 执行心跳过程
     */
    private void doHeartbeat() {
        MainThreadProcessorSingleton.getInstance().process(() -> {
            // 获取所有房间
            final Collection<Room> roomColl = RoomGroup.getAllRoom();

            if (roomColl.size() <= 0) {
                return;
            }

            // 获取当前时间
            final long currTime = System.currentTimeMillis();

            for (Room currRoom : roomColl) {
                if (null == currRoom) {
                    continue;
                }

                // 获取定时任务
                ITimerTask currTask = currRoom.getTimerTask();

                if (null == currTask) {
                    continue;
                }

                if (currTask.getRunAtTime() <= currTime) {
                    // 清空定时任务
                    currRoom.setTimerTask(null);

                    if (Math.abs(currTime - currTask.getRunAtTime()) >= MAX_TIME_DIFF) {
                        LOGGER.warn(
                            "时间间隔过大, 忽略执行! taskClazz = {}, atRoomId = {}",
                            currTask.getClass().getSimpleName(),
                            currRoom.getRoomId()
                        );
                        continue;
                    }

                    // 执行定时任务
                    currTask.doTask();
                }
            }
        });
    }
}
