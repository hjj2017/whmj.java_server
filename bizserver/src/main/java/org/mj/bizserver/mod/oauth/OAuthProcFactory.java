package org.mj.bizserver.mod.oauth;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.WorkModeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录过程工厂类
 */
public final class OAuthProcFactory {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(OAuthProcFactory.class);

    /**
     * 类默认构造器
     */
    private OAuthProcFactory() {
    }

    /**
     * 创建登录方式
     *
     * @param loginOrAuthMethod 登录或者授权方式
     * @param joProperty        JSON 属性
     * @return 登录方式
     */
    static public IOAuthProc create(int loginOrAuthMethod, JSONObject joProperty) {
        // 获取登录方式
        final MethodDef m = MethodDef.valueOf(loginOrAuthMethod);

        if (MethodDef.TESTER_LOGIN == m) {
            if (WorkModeDef.currIsDevMode()) {
                // 只有开发模式才允许测试员登录
                return new TesterAuthProc(joProperty);
            } else {
                LOGGER.error(
                    "服务器不是开发模式, 无法执行测试用户授权过程"
                );
                return null;
            }
        } else if (MethodDef.GUEST_LOGIN == m) {
            // 执行游客登录
            return new GuestAuthProc(joProperty);
        } else if (MethodDef.UKEY_LOGIN == m) {
            // 执行 Ukey 登录
            return new UkeyAuthProc(joProperty);
        } else if (MethodDef.PHONE_NUMBER_LOGIN == m) {
            // 执行手机号登录
            return new PhoneNumberAuthProc(joProperty);
        } else if (MethodDef.WEI_XIN_GONG_ZHONG_HAO_LOGIN == m) {
            // 微信公众号登录
            return new WeiXinGongZhongHaoAuthProc();
        } else {
            return null;
        }
    }
}
