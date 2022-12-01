package org.mj.proxyserver;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.foundation.Ukey;
import org.mj.comm.util.RedisXuite;

/**
 * 配置类
 */
public final class Config {
    /**
     * 私有化类默认构造器
     */
    private Config() {
    }

    static void init() {
        JSONObject joProxyServerConf;

        if (joProxyServerConf.containsKey("ukeyConf")) {
            JSONObject joConf = joProxyServerConf.getJSONObject("ukeyConf");
            Ukey.putUkeyPassword(joConf.getString("ukeyPassword"));
            Ukey.putUkeyTTL(joConf.getLongValue("ukeyTTL"));
        }

        if (joProxyServerConf.containsKey("redisXuite")) {
            RedisXuite.Config redisXuiteConf = RedisXuite.Config.fromJSONObj(joProxyServerConf);
            RedisXuite.init(redisXuiteConf);
        }
    }
}
