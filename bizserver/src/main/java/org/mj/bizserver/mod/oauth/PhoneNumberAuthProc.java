package org.mj.bizserver.mod.oauth;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.base.AliSMSAuthZervice;
import org.mj.bizserver.mod.oauth.dao.IUserDao;
import org.mj.bizserver.mod.oauth.dao.UserEntity;
import org.mj.comm.util.MySqlXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 手机号登录
 */
class PhoneNumberAuthProc implements IOAuthProc {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(PhoneNumberAuthProc.class);

    /**
     * 初始房卡
     */
    static private final int INIT_ROOM_CARD = 10;

    /**
     * 手机号字段
     */
    static private final String COL_USER_PHONE_NUMBER = "phone_number";

    /**
     * 手机号
     */
    private final String _phoneNumber;

    /**
     * 验证码
     */
    private final String _authCode;

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
    PhoneNumberAuthProc(JSONObject joProperty) {
        if (null == joProperty ||
            joProperty.isEmpty()) {
            throw new IllegalArgumentException("joProperty 参数无效");
        }

        _phoneNumber = joProperty.getString("phoneNumber");
        _authCode = joProperty.getString("authCode");

        if (null == _phoneNumber ||
            _phoneNumber.length() < 11 ||
            null == _authCode ||
            _authCode.isEmpty()) {
            throw new IllegalArgumentException("joProperty 参数无效, 手机号不合法或验证码为空");
        }

        // 获取客户端 IP 地址和版本号
        _clientIpAddr = joProperty.getString("clientIpAddr");
        _clientVer = joProperty.getString("clientVer");
    }

    @Override
    public int getAsyncOpId() {
        // 用最后 4 位作为 Id
        String strTempId = _phoneNumber.substring(_phoneNumber.length() - 4);
        return Integer.parseInt(strTempId);
    }

    @Override
    public String getTempId() {
        return "PhoneNumber_" + _phoneNumber;
    }

    @Override
    public int doAuth() {
        // 获取已经发送给用户的验证码
        final String smsXCode = AliSMSAuthZervice.getInstance().getAuthCode(_phoneNumber);

        if (!_authCode.equals(smsXCode)) {
            LOGGER.error(
                "验证码不一致, phoneNumber = {}, authCode = {}, 期望验证码 = {}",
                _phoneNumber, _authCode, smsXCode
            );
            return -1;
        }

        // 验证通过后移除验证码
        AliSMSAuthZervice.getInstance().removeAuthCode(_phoneNumber);

        // 操作数据库表 t_user
        try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            // 获取 DAO
            final IUserDao userDao = sessionX.getMapper(IUserDao.class);

            // 获取用户实体
            UserEntity userEntity = userDao.getEntityByColumnX(COL_USER_PHONE_NUMBER, _phoneNumber);
            // 获取当前时间
            final long nowTime = System.currentTimeMillis();

            if (null == userEntity) {
                // 泵发一个新的用户 Id
                final int userId = UserIdPump.popUpUserId();

                userEntity = new UserEntity();
                userEntity.setUserId(userId);
                userEntity.setUserName(RandomUserConfig.randomAUserName());
                userEntity.setHeadImg(RandomUserConfig.randomAHeadImg());
                userEntity.setSex(RandomUserConfig.randomASex());
                userEntity.setRoomCard(INIT_ROOM_CARD);
                userEntity.setCreateTime(nowTime);
                userEntity.setClientVer(_clientVer);
                userEntity.setLastLoginTime(nowTime);
                userEntity.setLastLoginIp(_clientIpAddr);
                userEntity.setState(0);

                userDao.insertOrUpdate(userEntity);
                userDao.updateColumnXByUserId(userId, COL_USER_PHONE_NUMBER, _phoneNumber);
            } else {
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
            }

            return userEntity.getUserId();
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return -1;
    }
}
