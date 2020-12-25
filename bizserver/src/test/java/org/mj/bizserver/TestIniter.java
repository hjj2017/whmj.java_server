package org.mj.bizserver;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.PropertyConfigurator;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.mod.club.adminctrl.AdminCtrlBizLogicTest;
import org.mj.comm.util.DLock;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

/**
 * 测试初始化
 */
public final class TestIniter {
    /**
     * 私有化类默认构造器
     */
    private TestIniter() {
    }

    /**
     * 初始化
     */
    static public void init() {
        // 设置 log4j 属性文件
        PropertyConfigurator.configure(AdminCtrlBizLogicTest.class.getClassLoader().getResourceAsStream("log4j.properties"));

        // 获取服务器配置
        JSONObject joBizServerConf = loadBizServerConf(
            "../etc/bizserver_all.conf.json"
        );

        if (joBizServerConf.containsKey("redisXuite")) {
            // 初始化 Redis
            RedisXuite.Config redisXuiteConf = RedisXuite.Config.fromJSONObj(joBizServerConf);
            RedisXuite.init(redisXuiteConf);
            DLock._redisKeyPrefix = RedisKeyDef.DLOCK_X_PREFIX;
        }

        if (joBizServerConf.containsKey("mySqlXuite")) {
            // 初始化 MySql
            MySqlXuite.Config mySqlXuiteConf = MySqlXuite.Config.fromJSONObj(joBizServerConf);
            MySqlXuite.init(mySqlXuiteConf, BizServer.class);
        }
    }

    /**
     * 加载业务服务器配置
     *
     * @param filePath 文件路径
     * @return JSON 对象
     */
    static private JSONObject loadBizServerConf(final String filePath) {
        if (null == filePath ||
            filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath is null or empty");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // 获取 JSON 文本
            final String jsonText = br.lines().collect(Collectors.joining());
            // 解析 JSON 对象
            return JSONObject.parseObject(jsonText);
        } catch (Exception ex) {
            // 记录错误日志
            throw new RuntimeException(ex);
        }
    }
}
