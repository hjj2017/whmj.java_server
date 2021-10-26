package org.mj.bizserver.base;

import org.mj.comm.util.MD5Util;
import org.mj.comm.util.OutParam;

import java.text.MessageFormat;

/**
 * 数字证书
 */
public final class Ukey {
    /**
     * Ukey 密码
     */
    static private String _ukeyPassword = "root";

    /**
     * Ukey 存活时间
     */
    static private long _ukeyTTL = 36000000L;

    /**
     * 私有化默认构造器
     */
    private Ukey() {
    }

    /**
     * 设置 Ukey 密码
     *
     * @param val 字符串值
     */
    static public void putUkeyPassword(String val) {
        _ukeyPassword = val;
    }

    /**
     * 设置 Ukey 存活时间
     *
     * @param val 整数值
     */
    static public void putUkeyTTL(long val) {
        _ukeyTTL = val;
    }

    /**
     * 生成 Ukey 字符串
     *
     * @param userId           用户 Id
     * @param out_ukeyExpireAt ( 输出参数 ) Ukey 过期时间
     * @return Ukey 字符串
     */
    static public String genUkeyStr(int userId, OutParam<Long> out_ukeyExpireAt) {
        // 获取当前时间戳并计算过期时间
        final long nowTime = System.currentTimeMillis();
        final long ukeyExpireAt = nowTime + _ukeyTTL;

        // 设置输出参数
        OutParam.putVal(
            out_ukeyExpireAt, ukeyExpireAt
        );

        String origStr = MessageFormat.format(
            "userId={0}&ukeyExpireAt={1}&ukeyPassword={2}",
            String.valueOf(userId),
            String.valueOf(ukeyExpireAt),
            _ukeyPassword
        );

        return MD5Util.encrypt(origStr);
    }

    /**
     * 验证 Ukey 是否有效
     *
     * @param userId       用户 Id
     * @param ukeyStr      Ukey 字符串
     * @param ukeyExpireAt Ukey 过期时间
     * @return true = 有效, false = 无效
     */
    static public boolean verify(int userId, String ukeyStr, long ukeyExpireAt) {
        if (ukeyExpireAt <= System.currentTimeMillis()) {
            // 如果 Ukey 已经过期
            return false;
        }

        String origStr = MessageFormat.format(
            "userId={0}&ukeyExpireAt={1}&ukeyPassword={2}",
            String.valueOf(userId),
            String.valueOf(ukeyExpireAt),
            _ukeyPassword
        );

        return MD5Util.encrypt(origStr).equals(ukeyStr);
    }
}
