package org.mj.bizserver.base;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 阿里 OSS 服务
 */
public final class AliOSSZervice {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AliOSSZervice.class);

    /**
     * 单例对象
     */
    static private final AliOSSZervice _instance = new AliOSSZervice();

    /**
     * 是否已经初始化
     */
    private final AtomicBoolean _inited = new AtomicBoolean(false);

    /**
     * 工人队列
     */
    private final BlockingQueue<OSS> _workerQueue = new LinkedBlockingQueue<>();

    /**
     * 桶名称
     */
    private String _bucket = "";

    /**
     * 基础路径
     */
    private String _basePath = "";

    /**
     * 超时毫秒数
     */
    private int _timeoutMS = 5000;

    /**
     * 私有化类默认构造器
     */
    private AliOSSZervice() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public AliOSSZervice getInstance() {
        return _instance;
    }

    /**
     * 初始化
     *
     * @param usingConf 使用配置
     */
    public void init(Config usingConf) {
        if (null == usingConf ||
            usingConf.getWorkerCount() <= 0) {
            return;
        }

        if (!_inited.compareAndSet(false, true)) {
            return;
        }

        _bucket = usingConf.getBucket();
        _basePath = usingConf.getBasePath();
        _timeoutMS = usingConf.getTimeoutMS();

        for (int i = 0; i < usingConf.getWorkerCount(); i++) {
            _workerQueue.offer(new OSSClientBuilder().build(
                usingConf.getEndpoint(),
                usingConf.getAccessKeyId(),
                usingConf.getAccessKeySecret()
            ));
        }

        LOGGER.info(
            "已经初始化阿里 OSS, endpoint = {}, bucket = {}, basePath = {}, workerCount = {}",
            usingConf.getEndpoint(),
            usingConf.getBucket(),
            usingConf.getBasePath(),
            _workerQueue.size()
        );
    }

    /**
     * 保存文本内容
     *
     * @param path 路径
     * @param text 文本内容
     */
    public void saveText(String path, String text) {
        if (null == path ||
            null == text) {
            return;
        }

        if (!_inited.get()) {
            LOGGER.warn("阿里 OSS 服务尚未初始化");
            return;
        }

        OSS ossClient = null;

        try {
            // 获取 OSS 客户端
            ossClient = _workerQueue.poll(_timeoutMS, TimeUnit.MILLISECONDS);

            if (null == ossClient) {
                LOGGER.error(
                    "保存失败, OSS 为空! path = {}",
                    path
                );
                return;
            }

            // 创建字节数组流
            final InputStream inS = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

            // 上传到 OSS
            ossClient.putObject(
                _bucket,
                _basePath + "/" + path,
                inS
            );
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            if (null != ossClient) {
                // 执行到最后,
                // 如果 OSS 客户端不为空,
                // 则塞回到工作队列...
                _workerQueue.offer(ossClient);
            }
        }
    }

    /**
     * 阿里 OSS 配置
     */
    static public class Config {
        /**
         * 域名
         */
        private String _endpoint = null;

        /**
         * Id
         */
        private String _accessKeyId = null;

        /**
         * Key
         */
        private String _accessKeySecret = null;

        /**
         * 桶名称
         */
        private String _bucket = null;

        /**
         * 基础路径
         */
        private String _basePath = null;

        /**
         * 工人数量
         */
        private int _workerCount = 8;

        /**
         * 超时毫秒数
         */
        private int _timeoutMS = 5000;

        /**
         * 获取域名
         *
         * @return 域名
         */
        @JSONField(name = "endpoint")
        public String getEndpoint() {
            return _endpoint;
        }

        /**
         * 设置域名
         *
         * @param val 字符串
         */
        public void setEndpoint(String val) {
            _endpoint = val;
        }

        /**
         * 获取 Id
         *
         * @return Id
         */
        @JSONField(name = "accessKeyId")
        public String getAccessKeyId() {
            return _accessKeyId;
        }

        /**
         * 设置 Id
         *
         * @param val 字符串
         */
        public void setAccessKeyId(String val) {
            _accessKeyId = val;
        }

        /**
         * 获取 Key
         *
         * @return Key
         */
        @JSONField(name = "accessKeySecret")
        public String getAccessKeySecret() {
            return _accessKeySecret;
        }

        /**
         * 设置 Key
         *
         * @param val 字符串
         */
        public void setAccessKeySecret(String val) {
            _accessKeySecret = val;
        }

        /**
         * 获取桶名称
         *
         * @return 桶名称
         */
        @JSONField(name = "bucket")
        public String getBucket() {
            return _bucket;
        }

        /**
         * 设置桶名称
         *
         * @param val 字符串
         */
        public void setBucket(String val) {
            _bucket = val;
        }

        /**
         * 获取基础路径
         *
         * @return 基础路径
         */
        @JSONField(name = "basePath")
        public String getBasePath() {
            return _basePath;
        }

        /**
         * 设置基础路径
         *
         * @param val 字符串
         */
        public void setBasePath(String val) {
            _basePath = val;
        }

        /**
         * 获取工人数量
         *
         * @return 工人数量
         */
        @JSONField(name = "workerCount")
        public int getWorkerCount() {
            return _workerCount;
        }

        /**
         * 设置工人数量
         *
         * @param val 整数值
         */
        public void setWorkerCount(int val) {
            _workerCount = val;
        }

        /**
         * 获取超时毫秒数
         *
         * @return 超时毫秒数
         */
        @JSONField(name = "timeoutMS")
        public int getTimeoutMS() {
            return _timeoutMS;
        }

        /**
         * 设置超时毫秒数
         *
         * @param val 整数值
         */
        public void setTimeoutMS(int val) {
            _timeoutMS = val;
        }

        /**
         * 从 JSON 对象中创建配置
         *
         * @param jsonObj JSON 对象
         * @return 配置
         */
        static public Config fromJSONObj(JSONObject jsonObj) {
            if (null == jsonObj ||
                !jsonObj.containsKey("aliOSS")) {
                return null;
            }

            jsonObj = jsonObj.getJSONObject("aliOSS");
            return jsonObj.toJavaObject(Config.class);
        }
    }
}
