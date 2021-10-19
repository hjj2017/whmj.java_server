package org.mj.bizserver;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.PropertyConfigurator;
import org.mj.bizserver.cluster.CurrServerReporter;
import org.mj.bizserver.cluster.SystemOfflineUserCleaner;
import org.mj.bizserver.def.PubSubChannelDef;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.def.WorkModeDef;
import org.mj.bizserver.foundation.AliIpv4LocationZervice;
import org.mj.bizserver.foundation.AliOSSZervice;
import org.mj.bizserver.foundation.AliSMSAuthZervice;
import org.mj.bizserver.foundation.ChannelHandlerFactoryImpl_0;
import org.mj.bizserver.foundation.Ukey;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.comm.pubsub.MySubscriber;
import org.mj.comm.util.DLock;
import org.mj.comm.util.MySqlXuite;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

/**
 * 登录服务器
 */
public final class BizServer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(BizServer.class);

    /**
     * 私有化类默认构造器
     */
    private BizServer() {
    }

    /**
     * 应用主函数
     *
     * @param argvArray 命令行参数数组
     */
    static public void main(String[] argvArray) {
        // 设置 log4j 属性文件
        PropertyConfigurator.configure(BizServer.class.getClassLoader().getResourceAsStream("log4j.properties"));

        // 创建命令行参数对象
        CommandLine cmdLn = createCmdLn(argvArray);

        if (null == cmdLn) {
            LOGGER.error("命令行参数错误");
            return;
        }

        LOGGER.info(
            "服务器版本号 ( ver ) = {}, 当前工作模式 ( workMode ) = {}",
            Ver.CURR,
            WorkModeDef.currWorkMode()
        );

        // 获取服务器配置
        JSONObject joBizServerConf = loadBizServerConf(
            cmdLn.getOptionValue("c")
        );

        if (joBizServerConf.containsKey("ukeyConf")) {
            JSONObject joConf = joBizServerConf.getJSONObject("ukeyConf");
            Ukey.putUkeyPassword(joConf.getString("ukeyPassword"));
            Ukey.putUkeyTTL(joConf.getLongValue("ukeyTTL"));
        }

        if (joBizServerConf.containsKey("redisXuite")) {
            // 初始化 Redis
            RedisXuite.Config conf = RedisXuite.Config.fromJSONObj(joBizServerConf);
            RedisXuite.init(conf);
            DLock._redisKeyPrefix = RedisKeyDef.DLOCK_X_PREFIX;
        }

        if (joBizServerConf.containsKey("mySqlXuite")) {
            // 初始化 MySql
            final MySqlXuite.Config conf = MySqlXuite.Config.fromJSONObj(joBizServerConf);
            MySqlXuite.init(conf, BizServer.class);
        }

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

        MJ_weihai_BizLogic.getInstance().setServerId(Integer.parseInt(
            cmdLn.getOptionValue("server_id")
        ));

        // 启动 Netty 服务器
        startUpNettyServer(cmdLn);
        // 开始上报当前服务器
        startReportCurrServer(cmdLn);
        // 开启订阅
        startUpSubscribe();
    }

    /**
     * 创建命令行对象
     *
     * @param argvArray 参数数组
     * @return 命令行对象
     */
    static private CommandLine createCmdLn(String[] argvArray) {
        // 创建参数选项
        Options op = new Options();
        // --server_id --server_name --server_job_type_set -h -p -c 选项
        op.addRequiredOption(null, "server_id", true, "服务器 Id");
        op.addRequiredOption(null, "server_name", true, "服务器名称");
        op.addRequiredOption(null, "server_job_type_set", true, "服务器工作类型集合");
        op.addRequiredOption("h", "server_host", true, "服务器主机地址");
        op.addRequiredOption("p", "server_port", true, "服务器端口号");
        op.addRequiredOption("c", "config", true, "使用配置文件");

        try {
            // 创建默认解析器
            DefaultParser dp = new DefaultParser();
            // 解析命令行参数
            return dp.parse(op, argvArray);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 加载业务服务器配置
     *
     * @param filePath 文件路径
     * @return JSON 对象
     */
    static private JSONObject loadBizServerConf(String filePath) {
        if (null == filePath ||
            filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath is null or empty");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // 获取 JSON 文本
            String jsonText = br.lines().collect(Collectors.joining());
            // 解析 JSON 对象
            return JSONObject.parseObject(jsonText);
        } catch (Exception ex) {
            // 记录错误日志
            throw new RuntimeException(ex);
        }
    }

    /**
     * 启动 Netty 服务器
     *
     * @param cmdLn 命令行参数对象
     */
    static private void startUpNettyServer(CommandLine cmdLn) {
        if (null == cmdLn) {
            return;
        }

        NettyServer.Config newConf = new NettyServer.Config();
        newConf.setServerId(Integer.parseInt(cmdLn.getOptionValue("server_id")));
        newConf.setServerName(cmdLn.getOptionValue("server_name"));
        newConf.setServerJobTypeStr(cmdLn.getOptionValue("server_job_type_set"));
        newConf.setServerHost(cmdLn.getOptionValue("h"));
        newConf.setServerPort(Integer.parseInt(cmdLn.getOptionValue("p")));
        newConf.setChannelHandlerFactory(new ChannelHandlerFactoryImpl_0());

        // 启动 Netty 服务器
        NettyServer newServer = new NettyServer(newConf);
        newServer.startUp();
    }

    /**
     * 开始上报服务器
     *
     * @param cmdLn 命令行参数对象
     */
    static private void startReportCurrServer(CommandLine cmdLn) {
        if (null == cmdLn) {
            return;
        }

        // 上报当前游戏服
        CurrServerReporter.Config newConf = new CurrServerReporter.Config();
        newConf.setTimeInterval(5000);
        newConf.setServerInfoGetter(() -> {
            CurrServerReporter.ServerInfo currInfo = new CurrServerReporter.ServerInfo();
            currInfo.setServerId(Integer.parseInt(cmdLn.getOptionValue("server_id")));
            currInfo.setServerName(cmdLn.getOptionValue("server_name"));
            currInfo.setServerJobTypeStr(cmdLn.getOptionValue("server_job_type_set"));
            currInfo.setServerHost(cmdLn.getOptionValue("h"));
            currInfo.setServerPort(Integer.parseInt(cmdLn.getOptionValue("p")));
            currInfo.setLoadCount(getLoadCount());
            return currInfo;
        });

        CurrServerReporter newReporter = new CurrServerReporter(newConf);
        newReporter.startReport();
    }

    /**
     * 开启订阅
     */
    static private void startUpSubscribe() {
        // 频道数组
        String[] chArray = {
            PubSubChannelDef.OFFLINE_USER_NOTICE,
            PubSubChannelDef.KICK_OUT_USER_NOTICE,
        };

        // 消息处理器组
        MySubscriber.MsgHandlerGroup hGroup = new MySubscriber.MsgHandlerGroup();
        hGroup.add(new SystemOfflineUserCleaner());

        // 订阅消息
        new MySubscriber().subscribe(
            chArray, hGroup
        );
    }

    /**
     * 获取负载数量
     *
     * @return 负载数量
     */
    static private int getLoadCount() {
        return org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup.getAllUserzCount();
    }
}
