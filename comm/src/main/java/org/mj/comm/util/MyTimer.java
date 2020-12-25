package org.mj.comm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义定时器
 */
public final class MyTimer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MyTimer.class);

    /**
     * Id 生成器
     */
    static private final AtomicInteger _IdGen = new AtomicInteger(0);

    /**
     * 线程池
     */
    static private final ScheduledExecutorService _es = Executors.newScheduledThreadPool(4, (r) -> {
        Thread t = new Thread(r);
        t.setName("myTimer[ " + _IdGen.getAndIncrement() + " ]");
        return t;
    });

    /**
     * 私有化类默认构造器
     */
    private MyTimer() {
    }

    /**
     * 执行单次任务
     *
     * @param task  任务
     * @param delay 延迟时间
     * @param tu    时间单位
     * @return 定时任务预期
     */
    static public ScheduledFuture<?> schedule(Runnable task, int delay, TimeUnit tu) {
        if (null != task) {
            return _es.schedule(
                new SafeRunner(task), delay, tu
            );
        } else {
            return null;
        }
    }

    /**
     * 执行定时任务
     *
     * @param task         任务
     * @param initialDelay 第一次执行的延迟时间
     * @param delay        第一次之后每次执行间隔时间
     * @param tu           时间单位
     * @return 定时任务预期
     */
    static public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, int initialDelay, int delay, TimeUnit tu) {
        if (null != task) {
            return _es.scheduleWithFixedDelay(
                new SafeRunner(task), initialDelay, delay, tu
            );
        } else {
            return null;
        }
    }

    /**
     * 安全运行
     */
    static private class SafeRunner implements Runnable {
        /**
         * 内置运行实例
         */
        private final Runnable _innerR;

        /**
         * 类参数构造器
         *
         * @param innerR 内置运行实例
         */
        SafeRunner(Runnable innerR) {
            _innerR = innerR;
        }

        @Override
        public void run() {
            if (null == _innerR) {
                return;
            }

            try {
                // 运行
                _innerR.run();
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
