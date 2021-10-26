package org.mj.proxyserver;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.PropertyConfigurator;
import org.mj.bizserver.def.PubSubChannelDef;
import org.mj.bizserver.def.WorkModeDef;
import org.mj.bizserver.base.Ukey;
import org.mj.comm.pubsub.MySubscriber;
import org.mj.comm.util.RedisXuite;
import org.mj.proxyserver.cluster.AClubTableChangedListener;
import org.mj.proxyserver.cluster.ConnectionTransferWatcher;
import org.mj.proxyserver.cluster.KickOutUserWatcher;
import org.mj.proxyserver.cluster.NewServerFinder;
import org.mj.proxyserver.foundation.ChannelHandlerFactoryImpl_1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

/**
 * 网关服务器
 */
public final class ProxyServer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ProxyServer.class);

    /**
     * 服务器 Id
     */
    static private String _Id;

    /**
     * 私有化类默认构造器
     */
    private ProxyServer() {
    }

    /**
     * 获取服务器 Id
     *
     * @return 服务器 Id
     */
    static public String getId() {
        return _Id;
    }

    /**
     * 应用主函数
     *
     * @param argvArray 命令行参数数组
     */
    static public void main(String[] argvArray) {
        // 设置 log4j 属性文件
        PropertyConfigurator.configure(ProxyServer.class.getClassLoader().getResourceAsStream("log4j.properties"));

        // 创建命令行参数对象
        CommandLine cmdLn = createCmdLn(argvArray);

        if (null == cmdLn) {
            LOGGER.error("命令行参数为空");
            return;
        }

        // 设置服务器 Id
        _Id = cmdLn.getOptionValue("server_id");

        LOGGER.info(
            "服务器版本号 ( ver ) = {}, 当前工作模式 ( workMode ) = {}",
            Ver.CURR,
            WorkModeDef.currWorkMode()
        );

        // 获取代理服务器配置
        JSONObject joProxyServerConf = createProxyServerConf(
            cmdLn.getOptionValue("c")
        );

        if (joProxyServerConf.containsKey("ukeyConf")) {
            JSONObject joConf = joProxyServerConf.getJSONObject("ukeyConf");
            Ukey.putUkeyPassword(joConf.getString("ukeyPassword"));
            Ukey.putUkeyTTL(joConf.getLongValue("ukeyTTL"));
        }

        if (joProxyServerConf.containsKey("redisXuite")) {
            RedisXuite.Config redisXuiteConf = RedisXuite.Config.fromJSONObj(joProxyServerConf);
            RedisXuite.init(redisXuiteConf);
        }

        // 启动 Netty 服务器
        startUpNettyServer(cmdLn);
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
        // --server_id --server_name -h -p -c 选项
        op.addRequiredOption(null, "server_id", true, "服务器 Id");
        op.addRequiredOption(null, "server_name", true, "服务器名称");
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
     * 获取代理服务器配置
     *
     * @param filePath 文件路径
     * @return JSON 对象
     */
    static private JSONObject createProxyServerConf(String filePath) {
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
        newConf.setServerJobTypeSet(null);
        newConf.setServerHost(cmdLn.getOptionValue("h"));
        newConf.setServerPort(Integer.parseInt(cmdLn.getOptionValue("p")));
        newConf.setChannelHandlerFactory(new ChannelHandlerFactoryImpl_1());

        // 启动 Netty 服务器
        NettyServer newServer = new NettyServer(newConf);
        newServer.startUp();
    }

    /**
     * 开启订阅
     */
    static private void startUpSubscribe() {
        // 频道数组
        String[] chArray = {
            PubSubChannelDef.A_CLUB_TABLE_CHANGED,
            PubSubChannelDef.CONNECTION_TRANSFER_NOTICE,
            PubSubChannelDef.KICK_OUT_USER_NOTICE,
            PubSubChannelDef.NEW_SERVER_COME_IN,
        };

        // 消息处理器组
        MySubscriber.MsgHandlerGroup hGroup = new MySubscriber.MsgHandlerGroup();
        hGroup.add(NewServerFinder.getInstance());
        hGroup.add(new AClubTableChangedListener());
        hGroup.add(new ConnectionTransferWatcher());
        hGroup.add(new KickOutUserWatcher());

        // 订阅消息
        new MySubscriber().subscribe(
            chArray, hGroup
        );
    }
}
