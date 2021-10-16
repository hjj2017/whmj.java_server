package org.mj.bizserver.foundation;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.geoip.model.v20200101.DescribeIpv4LocationRequest;
import com.aliyuncs.geoip.model.v20200101.DescribeIpv4LocationResponse;
import com.aliyuncs.profile.DefaultProfile;
import org.mj.bizserver.def.ErrorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 根据 IP 地址查询地理位置,
 * XXX 注意: 这个功能需要开通阿里云 --> 云解析 DNS --> IP 地理位置库
 */
public final class AliIpv4LocationZervice {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AliIpv4LocationZervice.class);

    /**
     * 单例对象
     */
    static private final AliIpv4LocationZervice _instance = new AliIpv4LocationZervice();

    /**
     * 是否已经初始化
     */
    private final AtomicBoolean _inited = new AtomicBoolean(false);

    /**
     * 随机对象
     */
    static private final Random RAND = new Random();

    /**
     * 工人队列
     */
    private final BlockingQueue<IAcsClient> _workerQueue = new LinkedBlockingQueue<>();

    /**
     * 超时毫秒数
     */
    private int _timeoutMS = 5000;

    /**
     * 私有化类默认构造器
     */
    private AliIpv4LocationZervice() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public AliIpv4LocationZervice getInstance() {
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

        final DefaultProfile defaultProfile = DefaultProfile.getProfile(
            "cn-hangzhou",
            usingConf.getAccessKeyId(),
            usingConf.getAccessKeySecret()
        );

        for (int i = 0; i < usingConf.getWorkerCount(); i++) {
            _workerQueue.offer(new DefaultAcsClient(
                defaultProfile
            ));
        }

        _timeoutMS = usingConf.getTimeoutMS();

        LOGGER.info(
            "已经初始化阿里 IPv4 地址定位服务, workerCount = {}",
            _workerQueue.size()
        );
    }

    /**
     * ( 异步方式 ) 根据 IP 地址集合查询地理位置
     *
     * @param ipAddrSet IP 地址集合
     * @param callback  回调函数
     */
    public void queryGeoLocation_async(
        Set<String> ipAddrSet, final IBizResultCallback<Map<String, GeoLocation>> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Map<String, GeoLocation>>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        final BizResultWrapper<Map<String, GeoLocation>> resultX = new BizResultWrapper<>();

        if (null == ipAddrSet ||
            ipAddrSet.isEmpty()) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        if (!_inited.get()) {
            LOGGER.error("阿里云 IP 地理位置服务尚未初始化");
            ErrorEnum.SERVICE_NOT_INITED.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定 Id
            RAND.nextInt(_workerQueue.size()),
            // 异步 IO 操作
            () -> queryGeoLocation(ipAddrSet, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 根据客户端 IP 集合查询地理位置
     *
     * @param ipAddrSet 客户端 IP 集合
     * @param resultX   业务结果
     */
    private void queryGeoLocation(
        Set<String> ipAddrSet,
        BizResultWrapper<Map<String, GeoLocation>> resultX) {

        if (null == resultX) {
            return;
        }

        resultX.setFinalResult(Collections.emptyMap());

        if (null == ipAddrSet ||
            ipAddrSet.isEmpty()) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        if (!_inited.get()) {
            LOGGER.error("阿里云 IP 地理位置服务尚未初始化");
            ErrorEnum.SERVICE_NOT_INITED.fillResultX(resultX);
            return;
        }

        IAcsClient acsClient = null;

        try {
            // 获取 OSS 客户端
            acsClient = _workerQueue.poll(_timeoutMS, TimeUnit.MILLISECONDS);

            if (null == acsClient) {
                LOGGER.error("查询 IP 地理位置失败, acsClient 为空!");
                return;
            }

            Map<String, GeoLocation> ipAndGeoLocationMap = new HashMap<>();

            for (String currIpAddr : ipAddrSet) {
                // 通过阿里云 API 查询用户所在位置
                DescribeIpv4LocationRequest req0 = new DescribeIpv4LocationRequest();
                req0.setIp(currIpAddr);

                DescribeIpv4LocationResponse resp = acsClient.getAcsResponse(req0);

                if (null == resp) {
                    continue;
                }

                LOGGER.info(
                    "通过阿里云 IP 地理位置服务定位, currIpAddr = {}, 经度 = {}, 纬度 = {}",
                    currIpAddr,
                    resp.getLongitude(),
                    resp.getLatitude()
                );

                ipAndGeoLocationMap.put(currIpAddr, new GeoLocation(
                    currIpAddr,
                    Float.parseFloat(resp.getLatitude()),
                    Float.parseFloat(resp.getLongitude())
                ));
            }

            resultX.setFinalResult(ipAndGeoLocationMap);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            if (null != acsClient) {
                // 执行到最后,
                // 如果 OSS 客户端不为空,
                // 则塞回到工作队列...
                _workerQueue.offer(acsClient);
            }
        }
    }

    /**
     * 阿里短信服务配置
     */
    static public class Config {
        /**
         * Id
         */
        private String _accessKeyId = null;

        /**
         * Key
         */
        private String _accessKeySecret = null;

        /**
         * 工人数量
         */
        private int _workerCount = 8;

        /**
         * 超时毫秒数
         */
        private int _timeoutMS = 5000;

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
                !jsonObj.containsKey("aliIpv4Location")) {
                return null;
            }

            jsonObj = jsonObj.getJSONObject("aliIpv4Location");
            return jsonObj.toJavaObject(Config.class);
        }
    }

    /**
     * 地理坐标,
     * XXX 注意: 不包括海拔高度
     */
    static public class GeoLocation {
        /**
         * 客户端 IP 地址
         */
        private String _clientIpAddr;

        /**
         * 纬度
         */
        private float _latitude;

        /**
         * 经度
         */
        private float _longitude;

        /**
         * 类参数构造器
         *
         * @param clientIpAddr 客户端 IP 地址
         * @param latitude     纬度
         * @param longitude    经度
         */
        public GeoLocation(String clientIpAddr, float latitude, float longitude) {
            _clientIpAddr = clientIpAddr;
            _latitude = latitude;
            _longitude = longitude;
        }

        /**
         * 获取客户端 IP 地址
         *
         * @return 客户端 IP 地址
         */
        public String getClientIpAddr() {
            return _clientIpAddr;
        }

        /**
         * 设置客户端 IP 地址
         *
         * @param val 字符串值
         */
        public void setClientIpAddr(String val) {
            _clientIpAddr = val;
        }

        /**
         * 获取纬度
         *
         * @return 纬度
         */
        public float getLatitude() {
            return _latitude;
        }

        /**
         * 设置纬度
         *
         * @param val 浮点数
         */
        public void setLatitude(float val) {
            _latitude = val;
        }

        /**
         * 获取经度
         *
         * @return 经度
         */
        public float getLongitude() {
            return _longitude;
        }

        /**
         * 设置经度
         *
         * @param val 浮点数
         */
        public void setLongitude(float val) {
            _longitude = val;
        }
    }
}
