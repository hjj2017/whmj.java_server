package org.mj.comm.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * MD5 实用工具类
 */
public final class MD5Util {
    /**
     * 私有化类默认构造器
     */
    private MD5Util() {
    }

    /**
     * 加密字符串
     *
     * @param origStr 原始字符串
     * @return MD5 字符串
     */
    static public String encrypt(String origStr) {
        if (null == origStr) {
            return null;
        }

        try {
            byte[] byteArray = origStr.getBytes(StandardCharsets.UTF_8);
            byteArray = MessageDigest.getInstance("md5")
                .digest(byteArray);

            StringBuilder sb = new StringBuilder(new BigInteger(1, byteArray).toString(16));

            for (int i = 0; i < 32 - sb.length(); i++) {
                sb.insert(0, "0");
            }

            return sb.toString();
        } catch (Exception ex) {
            // 抛出异常
            throw new RuntimeException("加密失败");
        }
    }
}
