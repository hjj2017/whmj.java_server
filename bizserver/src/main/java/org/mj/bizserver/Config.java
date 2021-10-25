package org.mj.bizserver;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import org.apache.commons.cli.CommandLine;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.def.WorkModeDef;
import org.mj.bizserver.foundation.AliIpv4LocationZervice;
import org.mj.bizserver.foundation.AliOSSZervice;
import org.mj.bizserver.foundation.AliSMSAuthZervice;
import org.mj.bizserver.foundation.Ukey;
import org.mj.comm.util.DLock;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 配置
 */
public final class Config {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    /**
     * DataId = MySql 配置
     */
    static private final String DATA_ID_ORG_MJ_BIZSERVER_CONF_MYSQLXUITE = "org.mj.bizserver.conf.mysqlxuite";

    /**
     * DataId = Redis 配置
     */
    static private final String DATA_ID_ORG_MJ_BIZSERVER_CONF_REDISXUITE = "org.mj.bizserver.conf.redisxuite";

    /**
     * 分组名称
     */
    static private final String GROUP_HJ_S_MEELEZ = "hj_s_meelez";

    /**
     * 执行初始化
     *
     * @param cmdLn 命令行对象
     */
    static void doInit(CommandLine cmdLn) {
        // 获取服务器配置
        ConfigService cs = createConfigService(cmdLn.getOptionValue("nacos_server_addr"));

        JSONObject joBizServerConf = null;

        if (joBizServerConf.containsKey("ukeyConf")) {
            JSONObject joConf = joBizServerConf.getJSONObject("ukeyConf");
            Ukey.putUkeyPassword(joConf.getString("ukeyPassword"));
            Ukey.putUkeyTTL(joConf.getLongValue("ukeyTTL"));
        }

        initMySqlXuite(cs);
        initRedisXuite(cs);

        if (joBizServerConf.containsKey("aliSMSAuth")) {
            // 初始化短信验证服务
            final AliSMSAuthZervice.Config conf = AliSMSAuthZervice.Config.fromJSONObj(joBizServerConf);
            AliSMSAuthZervice.getInstance().init(conf);
        }

        if (joBizServerConf.containsKey("aliIpv4Location")) {
            // 初始化阿里 IP 地址定位服务
            final AliIpv4LocationZervice.Config conf = AliIpv4LocationZervice.Config.fromJSONObj(joBizServerConf);
            AliIpv4LocationZervice.getInstance().init(conf);
        }

        if (joBizServerConf.containsKey("aliOSS")) {
            // 初始化阿里 OSS 服务
            final AliOSSZervice.Config conf = AliOSSZervice.Config.fromJSONObj(joBizServerConf);
            AliOSSZervice.getInstance().init(conf);
        }
    }

    /**
     * 创建配置服务
     *
     * @param serverAddrOfNacos Nacos 服务器地址
     * @return 配置服务
     */
    static private ConfigService createConfigService(String serverAddrOfNacos) {
        if (null == serverAddrOfNacos ||
            serverAddrOfNacos.isEmpty()) {
            throw new IllegalArgumentException("serverAddrOfNacos is null or empty");
        }

        try {
            LOGGER.info(
                "通过 Nacos 服务器 ( serverAddr = {} ) 加载配置",
                serverAddrOfNacos
            );

            Properties prop = new Properties();
            prop.put("serverAddr", serverAddrOfNacos);

            return NacosFactory.createConfigService(prop);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
            System.exit(-1);
        }

        return null;
    }

    /**
     * 初始化 MySql
     *
     * @param cs 配置服务
     */
    static private void initMySqlXuite(ConfigService cs) {
        if (null == cs) {
            return;
        }

        try {
            String strConf = cs.getConfig(
                DATA_ID_ORG_MJ_BIZSERVER_CONF_MYSQLXUITE,
                GROUP_HJ_S_MEELEZ + "." + WorkModeDef.currWorkMode(),
                500
            );

            JSONObject joConf = JSONObject.parseObject(strConf);

            if (joConf.containsKey("mySqlXuite")) {
                // 初始化 MySql
                MySqlXuite.Config mySqlConf = MySqlXuite.Config.fromJSONObj(joConf);
                MySqlXuite.init(mySqlConf, BizServer.class);
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    /**
     * 初始化 Redis
     *
     * @param cs 配置服务
     */
    static private void initRedisXuite(ConfigService cs) {
        if (null == cs) {
            return;
        }

        try {
            String strConf = cs.getConfig(
                DATA_ID_ORG_MJ_BIZSERVER_CONF_REDISXUITE,
                GROUP_HJ_S_MEELEZ + "." + WorkModeDef.currWorkMode(),
                500
            );

            JSONObject joConf = JSONObject.parseObject(strConf);

            if (joConf.containsKey("redisXuite")) {
                // 初始化 Redis
                RedisXuite.Config redisXuiteConf = RedisXuite.Config.fromJSONObj(joConf);
                RedisXuite.init(redisXuiteConf);
                DLock._redisKeyPrefix = RedisKeyDef.DLOCK_X_PREFIX;
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }
}
