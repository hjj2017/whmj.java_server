package org.mj.comm.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MySql 会话工厂类
 */
public final class MySqlXuite {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MySqlXuite.class);

    /**
     * MySQL 会话工厂字典
     */
    static private final Map<JobType, SqlSessionFactory> _sqlSessionFactoryMap = new ConcurrentHashMap<>();

    /**
     * 私有化类默认构造器
     */
    private MySqlXuite() {
    }

    /**
     * 初始化
     *
     * @param usingConf          使用配置
     * @param scanClazzAtPackage 扫描类所在包
     */
    static public void init(
        Config usingConf, Class<?> scanClazzAtPackage) {
        if (null == scanClazzAtPackage) {
            throw new IllegalArgumentException("scanClazzAtPackage is null");
        }

        init(usingConf, scanClazzAtPackage.getPackage());
    }

    /**
     * 初始化
     *
     * @param usingConf   使用配置
     * @param scanPackage 扫描包
     */
    static public void init(
        Config usingConf,
        Package scanPackage) {
        init(usingConf, scanPackage.getName());
    }

    /**
     * 初始化
     *
     * @param usingConf       使用配置
     * @param scanPackageName 扫描包名称
     */
    static public void init(Config usingConf, String scanPackageName) {
        if (null == usingConf ||
            usingConf._itemMap.isEmpty()) {
            throw new IllegalArgumentException("usingConf 配置无效");
        }

        for (Map.Entry<String, Config.Item> entry : usingConf._itemMap.entrySet()) {
            if (null == entry.getKey() ||
                null == entry.getValue()) {
                return;
            }

            // 获取职位类型
            final JobType jt = JobType.valueOf(entry.getKey());
            // 获取配置项目
            final Config.Item confItem = entry.getValue();

            // 找到 DAO 类
            Set<Class<?>> daoClazzSet = PackageUtil.listClazz(
                scanPackageName,
                true,
                (clazz) -> null != clazz && null != clazz.getAnnotation(DAO.class)
            );

            try {
                // MySql 会话工厂
                final SqlSessionFactory ssf = new SqlSessionFactoryBuilder().build(
                    Resources.getResourceAsStream("mybatis.config.xml"),
                    confItem.buildPropMap()
                );

                // 获取工厂配置
                Configuration mybatisConf = ssf.getConfiguration();

                for (Class<?> daoClazz : daoClazzSet) {
                    if (null != daoClazz) {
                        // 挨个注册 DAO 接口
                        mybatisConf.addMapper(daoClazz);
                    }
                }

                // 测试数据库会话
                try (SqlSession testSession = ssf.openSession()) {
                    testSession.getConnection()
                        .prepareStatement("SELECT -1")
                        .execute();

                    LOGGER.info(
                        "MySql 数据库连接成功!, serverAddr = {}:{}, db = {}, userName = {}",
                        confItem._serverHost,
                        confItem._serverPort,
                        confItem._db,
                        confItem._userName
                    );

                    _sqlSessionFactoryMap.put(jt, ssf);
                }
            } catch (Exception ex) {
                // 抛出运行时异常
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 开启游戏数据库会话
     *
     * @return MySql 会话
     */
    static public SqlSession openGameDbSession() {
        // Sql 会话工厂
        SqlSessionFactory ssf = _sqlSessionFactoryMap.get(JobType.gameDb);

        if (null == ssf) {
            throw new RuntimeException("gameDb 配置未初始化");
        }

        return ssf.openSession(true);
    }

    /**
     * 开启日志数据库会话
     *
     * @return MySql 会话
     */
    static public SqlSession openLogDbSession() {
        // Sql 会话工厂
        SqlSessionFactory ssf = _sqlSessionFactoryMap.get(JobType.logDb);

        if (null == ssf) {
            throw new RuntimeException("logDb 配置未初始化");
        }

        return ssf.openSession(true);
    }

    /**
     * 职位类型
     */
    private enum JobType {
        /**
         * 游戏数据库
         */
        gameDb,

        /**
         * 日志数据库
         */
        logDb,
    }

    /**
     * 配置
     */
    static public class Config {
        /**
         * 配置项字典
         */
        public Map<String, Item> _itemMap = null;

        /**
         * 从 JSON 对象中创建配置项
         *
         * @param jsonObj JSON 对象
         * @return 配置项
         */
        static public Config fromJSONObj(JSONObject jsonObj) {
            if (null == jsonObj) {
                return null;
            }

            // MySQL 套件配置
            JSONObject joMySqlXuite = jsonObj.getJSONObject("mySqlXuite");

            if (null == joMySqlXuite) {
                return null;
            }

            Config newConf = new Config();
            newConf._itemMap = new ConcurrentHashMap<>();

            for (JobType jt : JobType.values()) {
                // 获取配置项
                JSONObject joItem = joMySqlXuite.getJSONObject(jt.name());
                // 从 JSON 对象中创建配置项
                Item newItem = Item.fromJSONObj(joItem);

                if (null != newItem) {
                    newConf._itemMap.put(jt.name(), newItem);
                }
            }

            return newConf;
        }

        /**
         * 配置项
         */
        static class Item {
            /**
             * MySQL 服务器主机地址
             */
            @JSONField(name = "serverHost")
            public String _serverHost = null;

            /**
             * MySQL 服务器端口号
             */
            @JSONField(name = "serverPort")
            public int _serverPort = -1;

            /**
             * MySQL 数据库
             */
            @JSONField(name = "db")
            public String _db = null;

            /**
             * 用户名称
             */
            @JSONField(name = "userName")
            public String _userName = null;

            /**
             * 密码
             */
            @JSONField(name = "password")
            public String _password = null;

            /**
             * 连接池初始大小
             */
            @JSONField(name = "initialSize")
            public int _initialSize = 1;

            /**
             * 最小空闲连接数
             */
            @JSONField(name = "minIdle")
            public int _minIdle = 1;

            /**
             * 最大活动连接数
             */
            @JSONField(name = "maxActive")
            public int _maxActive = 32;

            /**
             * 最大等待连接数
             */
            @JSONField(name = "maxWait")
            public int _maxWait = 5000;

            /**
             * 构建属性字典
             *
             * @return 属性字典
             */
            Properties buildPropMap() {
                Properties newProp = new Properties();
                newProp.setProperty("serverHost", _serverHost);
                newProp.setProperty("serverPort", String.valueOf(_serverPort));
                newProp.setProperty("db", _db);
                newProp.setProperty("userName", _userName);
                newProp.setProperty("password", _password);
                newProp.setProperty("initialSize", String.valueOf(_initialSize));
                newProp.setProperty("minIdle", String.valueOf(_minIdle));
                newProp.setProperty("maxActive", String.valueOf(_maxActive));
                newProp.setProperty("maxWait", String.valueOf(_maxWait));
                return newProp;
            }

            /**
             * 从 JSON 对象中创建配置项
             *
             * @param jsonObj JSON 对象
             * @return 配置项
             */
            static Item fromJSONObj(JSONObject jsonObj) {
                if (null == jsonObj) {
                    return null;
                }

                return jsonObj.toJavaObject(Item.class);
            }
        }
    }

    /**
     * DAO
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DAO {
    }
}
