package org.mj.bizserver.mod.userlogin;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.ErrorEnum;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.base.AsyncOperationProcessorSingleton;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.IBizResultCallback;
import org.mj.bizserver.base.Ukey;
import org.mj.bizserver.mod.oauth.IOAuthProc;
import org.mj.bizserver.mod.oauth.OAuthProcFactory;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.bizserver.mod.userlogin.bizdata.LoginResult;
import org.mj.comm.util.DLock;
import org.mj.comm.util.OutParam;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 执行用户登录 ( 异步方式 )
 */
interface UserLoginBizLogic$doUserLogin {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(UserLoginBizLogic$doUserLogin.class);

    /**
     * 执行用户登录 ( 异步方式 )
     *
     * @param loginMethod 登录方式
     * @param propertyStr 属性字符串
     * @param callback    回调函数
     */
    default void doUserLogin_async(int loginMethod, final String propertyStr, IBizResultCallback<LoginResult> callback) {
        // 确保回调函数不为空
        final IBizResultCallback<LoginResult>
            finalCb = (null != callback) ? callback : (resultX) -> {
        };

        LOGGER.info(
            "用户登录, loginMethod = {}, propertyStr = {}",
            loginMethod,
            propertyStr
        );

        // 业务结果
        final BizResultWrapper<LoginResult> bizResult = new BizResultWrapper<>();

        // 转换成 JSON 对象
        JSONObject joProperty = JSONObject.parseObject(propertyStr);

        if (null == joProperty ||
            joProperty.isEmpty()) {
            // 如果 JSON 为空,
            ErrorEnum.PARAM_ERROR.fillResultX(bizResult);
            finalCb.apply(bizResult);
            return;
        }

        // 获取登录过程
        final IOAuthProc oauth = OAuthProcFactory.create(loginMethod, joProperty);

        if (null == oauth) {
            // 如果登录过程为空,
            LOGGER.error(
                "创建登录过程为空, loginMethod = {}",
                loginMethod
            );
            ErrorEnum.PASSPORT__AUTH_PROC_IS_NULL.fillResultX(bizResult);
            finalCb.apply(bizResult);
            return;
        }

        // 执行异步操作
        AsyncOperationProcessorSingleton.getInstance().process_0(
            // 绑定 ( 线程 ) Id
            oauth.getAsyncOpId(),
            // 异步 IO 操作
            () -> doUserLogin(oauth, bizResult),
            // 回到同步操作
            () -> finalCb.apply(bizResult)
        );
    }

    /**
     * 执行用户登陆
     *
     * @param oauth   授权方式
     * @param resultX 输出结果
     */
    default void doUserLogin(IOAuthProc oauth, BizResultWrapper<LoginResult> resultX) {
        if (null == oauth ||
            null == resultX) {
            ErrorEnum.PARAM_ERROR.fillResultX(resultX);
            return;
        }

        // 通过 "分布式锁" 锁定操作
        try (DLock locker = DLock.newLock("do_user_login?temp_id=" + oauth.getTempId())) {
            if (null == locker ||
                !locker.tryLock(5000)) {
                // 增加一个分布式锁,
                // 5 秒种内不能执行相同操作,
                // 避免重复创建账号...
                ErrorEnum.OPERATING_TOO_FREQUENTLY.fillResultX(resultX);
                return;
            }

            // 执行授权并返回用户 Id
            final int userId = oauth.doAuth();

            if (userId <= 0) {
                LOGGER.error(
                    "执行授权过程失败, tempId = {}",
                    oauth.getTempId()
                );
                ErrorEnum.PASSPORT__DO_AUTH_FAIL.fillResultX(resultX);
                return;
            }

            try (Jedis redisCache = RedisXuite.getRedisCache()) {
                // 授权成功后,
                // 清理用户详情缓存...
                redisCache.hdel(
                    RedisKeyDef.USER_X_PREFIX + userId,
                    RedisKeyDef.USER_DETAILZ
                );
            }

            // 尝试根据平台 Id 获取用户详情
            BizResultWrapper<UserDetailz> resultA = new BizResultWrapper<>();
            UserInfoBizLogic.getInstance().getUserDetailzByUserId(userId, resultA);

            // 获取用户详情
            UserDetailz userDetailz = resultA.getFinalResult();

            if (null == userDetailz) {
                LOGGER.error(
                    "用户详情为空, userId = {}",
                    userId
                );
                ErrorEnum.PASSPORT__USER_DETAILZ_IS_NULL.fillResultX(resultX);
                return;
            }

            final LoginResult finalResult = new LoginResult();
            finalResult.setUserId(userId);
            finalResult.setUserName(userDetailz.getUserName());
            finalResult.setTicket(TicketGen.genTicket(userId));

            OutParam<Long> out_ukeyExpireAt = new OutParam<>();
            String ukeyStr = Ukey.genUkeyStr(userId, out_ukeyExpireAt);
            finalResult.setUkeyStr(ukeyStr);
            finalResult.setUkeyExpireAt(out_ukeyExpireAt.getVal());

            // 设置最终结果
            resultX.setFinalResult(finalResult);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
