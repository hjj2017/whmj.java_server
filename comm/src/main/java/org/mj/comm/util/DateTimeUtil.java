package org.mj.comm.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 日期时间实用工具类
 */
public final class DateTimeUtil {
    /**
     * 一分钟
     */
    static public final long ONE_MINUTE = 60 * 1000;

    /**
     * 一小时
     */
    static public final long ONE_HOUR = 60 * ONE_MINUTE;

    /**
     * 一天
     */
    static public final long ONE_DAY = 24 * ONE_HOUR;

    /**
     * 一周
     */
    static public final long ONE_WEEK = 7 * ONE_DAY;

    /**
     * 私有化类默认构造器
     */
    private DateTimeUtil() {
    }

    /**
     * 获取日期时间字符串
     *
     * @param ms     毫秒数
     * @param format 格式字符串
     * @return 日期时间字符串
     */
    static public String getDateTimeStr(long ms, String format) {
        if (ms <= 0 ||
            null == format) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(ms);
    }

    /**
     * 根据毫秒时间获取周一日历, 也就是指定时间戳所在周的周一
     *
     * @param ms 毫秒时间
     * @return 周一日期, 例如: "20200713"
     */
    static public String getMondayDateStr(long ms) {
        Calendar cal = getMondayCalendar(ms);
        return getDateTimeStr(cal.getTimeInMillis(), "yyyyMMdd");
    }

    /**
     * 根据毫秒时间获取周一日历, 也就是指定时间戳所在周的周一
     *
     * @param ms 毫秒时间
     * @return 日历
     */
    static public Calendar getMondayCalendar(long ms) {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置周一为第一天
        cal.setTimeInMillis(ms);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 本周一
        return cal;
    }
}
