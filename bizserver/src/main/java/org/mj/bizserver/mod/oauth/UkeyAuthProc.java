package org.mj.bizserver.mod.oauth;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.base.Ukey;
import org.mj.bizserver.mod.oauth.dao.IUserDao;
import org.mj.bizserver.mod.oauth.dao.UserEntity;
import org.mj.comm.util.MySqlXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ukey 登录
 */
class UkeyAuthProc implements IOAuthProc {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(UkeyAuthProc.class);

    /**
     * t_user 数据表 user_id 列
     */
    static private final String T_USER_USER_ID = "user_id";

    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * Ukey 字符串
     */
    private final String _ukeyStr;

    /**
     * Ukey 过期时间
     */
    private final long _ukeyExpireAt;

    /**
     * 客户端 IP 地址
     */
    private final String _clientIpAddr;

    /**
     * 客户端版本号
     */
    private final String _clientVer;

    /**
     * 类参数构造器
     *
     * @param joProperty JSON 属性字典
     * @throws IllegalArgumentException if null == joProperty || joProperty.isEmpty
     */
    UkeyAuthProc(JSONObject joProperty) {
        if (null == joProperty ||
            joProperty.isEmpty()) {
            throw new IllegalArgumentException("joProperty 参数无效");
        }

        // 获取用户 Id 和 Ukey
        _userId = joProperty.getIntValue("userId");
        _ukeyStr = joProperty.getString("ukeyStr");
        _ukeyExpireAt = joProperty.getLongValue("ukeyExpireAt");
        // 获取客户端 IP 地址和版本号
        _clientIpAddr = joProperty.getString("clientIpAddr");
        _clientVer = joProperty.getString("clientVer");
    }

    @Override
    public int getAsyncOpId() {
        return _userId;
    }

    @Override
    public String getTempId() {
        return "Ukey_UserId_" + _userId;
    }

    @Override
    public int doAuth() {
        if (!Ukey.verify(_userId, _ukeyStr, _ukeyExpireAt)) {
            LOGGER.error(
                "Ukey 验证失败, userId = {}",
                _userId
            );
            return -1;
        }

        try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            // 获取 DAO
            final IUserDao userDao = sessionX.getMapper(IUserDao.class);

            // 获取用户实体
            UserEntity userEntity = userDao.getEntityByColumnX(T_USER_USER_ID, String.valueOf(_userId));
            // 获取当前时间
            final long nowTime = System.currentTimeMillis();

            if (null == userEntity) {
                LOGGER.error(
                    "用户实体为空, userId = {}",
                    _userId
                );
                return -1;
            }

            // 如果用户实体不为空,
            // 那么更新用户名称、头像、最后登录时间和 IP 地址
            //
            if (null == userEntity.getUserName()) {
                userEntity.setUserName(
                    RandomUserConfig.randomAUserName()
                );
            }

            if (null == userEntity.getHeadImg()) {
                userEntity.setHeadImg(
                    RandomUserConfig.randomAHeadImg()
                );
            }

            // 更新版本号和最后登录
            userEntity.setClientVer(_clientVer);
            userEntity.setLastLoginTime(nowTime);
            userEntity.setLastLoginIp(_clientIpAddr);

            userDao.insertOrUpdate(userEntity);

            return _userId;
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return -1;
    }
}
