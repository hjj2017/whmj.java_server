package org.mj.bizserver.base;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.def.WorkModeDef;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 阿里短信验证服务,
 * 给指定手机号发送验证码并将验证码暂存到 Redis
 */
public final class AliSMSAuthZervice {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AliSMSAuthZervice.class);

    /**
     * 单例对象
     */
    static private final AliSMSAuthZervice _instance = new AliSMSAuthZervice();

    /**
     * 验证码过期时间, 单位 = 毫秒
     */
    static private final int AUTH_CODE_EXPIRE_TIME = 300000;

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
     * 短信签名
     */
    private String _signName = null;

    /**
     * 模板代码
     */
    private String _templateCode = null;

    /**
     * 超时毫秒数
     */
    private int _timeoutMS = 5000;

    /**
     * 私有化类默认构造器
     */
    private AliSMSAuthZervice() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public AliSMSAuthZervice getInstance() {
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
            _workerQueue.add(new DefaultAcsClient(
                defaultProfile
            ));
        }

        _signName = usingConf.getSignName();
        _templateCode = usingConf.getTemplateCode();
        _timeoutMS = usingConf.getTimeoutMS();

        LOGGER.info(
            "已经初始化阿里短信验证服务, workerCount = {}",
            _workerQueue.size()
        );
    }

    /**
     * 获取验证码
     *
     * @param phoneNumber 手机号
     * @return 验证码
     */
    public String getAuthCode(String phoneNumber) {
        if (null == phoneNumber ||
            phoneNumber.length() < 11) {
            return null;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            return redisCache.get(RedisKeyDef.SMS_AUTH_CODE_PREFIX + phoneNumber);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return null;
    }

    /**
     * 移除验证码
     *
     * @param phoneNumber 手机号
     */
    public void removeAuthCode(String phoneNumber) {
        if (null == phoneNumber ||
            phoneNumber.length() < 11) {
            return;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            redisCache.del(RedisKeyDef.SMS_AUTH_CODE_PREFIX + phoneNumber);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * ( 异步方式 ) 发送验证码
     *
     * @param phoneNumber 手机号
     * @param callback    回调函数
     */
    public void sendAuthCode_async(
        final String phoneNumber, final IBizResultCallback<Boolean> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<Boolean>
            finalCall = (null != callback) ? callback : (resultX) -> {
        };

        final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

        if (null == phoneNumber ||
            phoneNumber.length() < 11) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            finalCall.apply(resultX);
            return;
        }

        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定 Id
            (int) Long.parseLong(phoneNumber),
            // 异步 IO 操作
            () -> sendAuthCode(phoneNumber, resultX),
            // 回到同步操作
            () -> finalCall.apply(resultX)
        );
    }

    /**
     * 发送验证码
     *
     * @param phoneNumber 手机号
     * @param resultX     业务结果
     */
    public void sendAuthCode(String phoneNumber, BizResultWrapper<Boolean> resultX) {
        if (null == phoneNumber ||
            phoneNumber.length() < 11) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        resultX.setFinalResult(false);

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            final String redisKey = RedisKeyDef.SMS_AUTH_CODE_PREFIX + phoneNumber;

            if (redisCache.exists(redisKey)) {
                LOGGER.error(
                    "已有验证码, phoneNumber = {}",
                    phoneNumber
                );
                return;
            }

            // 生成验证码并设置到 Redis
            final String authCode = genAuthCode();
            String ok = redisCache.set(
                redisKey,
                authCode,
                new SetParams().nx().px(AUTH_CODE_EXPIRE_TIME)
            );

            if (!"ok".equalsIgnoreCase(ok)) {
                LOGGER.error(
                    "设置验证码失败, phoneNumber = {}",
                    phoneNumber
                );
                return;
            }

            LOGGER.info(
                "已暂存验证码, phoneNumber = {}, authCode = {}",
                phoneNumber, authCode
            );

            if (!WorkModeDef.currIsDevMode()) {
                LOGGER.info(
                    "给用户发送短信验证码, phoneNumber = {}, authCode = {}",
                    phoneNumber, authCode
                );

                // 执行验证短信过程
                doAuthSMS(phoneNumber, authCode);
            }

            resultX.setFinalResult(true);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 生成验证码
     *
     * @return 验证码
     */
    static private String genAuthCode() {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            sb.append(RAND.nextInt(10));
        }

        return sb.toString();
    }

    /**
     * 执行短信过程
     *
     * @param phoneNumber 手机号
     * @param authCode    验证码
     */
    private void doAuthSMS(String phoneNumber, String authCode) {
        if (null == phoneNumber ||
            null == authCode) {
            return;
        }

        if (!_inited.get()) {
            LOGGER.error("阿里云短信服务尚未初始化");
            return;
        }

        IAcsClient acsClient = null;

        try {
            // 获取 ACS 客户端
            acsClient = _workerQueue.poll(_timeoutMS, TimeUnit.MILLISECONDS);

            if (null == acsClient) {
                LOGGER.error(
                    "发送验证短信失败, acsClient 为空! phoneNumber = {}",
                    phoneNumber
                );
                return;
            }

            JSONObject joParam = new JSONObject();
            joParam.put("code", authCode);

            final CommonRequest commReq = new CommonRequest();
            commReq.setSysMethod(MethodType.POST);
            commReq.setSysDomain("dysmsapi.aliyuncs.com");
            commReq.setSysVersion("2017-05-25");
            commReq.setSysAction("SendSms");
            commReq.putQueryParameter("RegionId", "cn-hangzhou");
            commReq.putQueryParameter("PhoneNumbers", phoneNumber);
            commReq.putQueryParameter("SignName", _signName);
            commReq.putQueryParameter("TemplateCode", _templateCode);
            commReq.putQueryParameter("TemplateParam", joParam.toJSONString());

            final CommonResponse commResp = acsClient.getCommonResponse(commReq);
            LOGGER.info(commResp.getData());
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            if (null != acsClient) {
                // 执行到最后,
                // 如果 OSS 客户端不为空,
                // 则塞回到工作队列...
                _workerQueue.add(acsClient);
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
         * 签名名称
         */
        private String _signName = null;

        /**
         * 模板代码
         */
        private String _templateCode = null;

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
         * 获取短信签名
         *
         * @return 短信签名
         */
        @JSONField(name = "signName")
        public String getSignName() {
            return _signName;
        }

        /**
         * 设置短信签名
         *
         * @param val 字符串值
         */
        public void setSignName(String val) {
            _signName = val;
        }

        /**
         * 获取模板代码
         *
         * @return 模板代码
         */
        @JSONField(name = "templateCode")
        public String getTemplateCode() {
            return _templateCode;
        }

        /**
         * 设置模板代码
         *
         * @param val 字符串值
         */
        public void setTemplateCode(String val) {
            _templateCode = val;
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
        static public AliSMSAuthZervice.Config fromJSONObj(JSONObject jsonObj) {
            if (null == jsonObj ||
                !jsonObj.containsKey("aliSMSAuth")) {
                return null;
            }

            jsonObj = jsonObj.getJSONObject("aliSMSAuth");
            return jsonObj.toJavaObject(AliSMSAuthZervice.Config.class);
        }
    }
}
