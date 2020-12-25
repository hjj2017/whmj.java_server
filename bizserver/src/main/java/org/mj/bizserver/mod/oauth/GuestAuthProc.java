package org.mj.bizserver.mod.oauth;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.mod.oauth.dao.IUserDao;
import org.mj.bizserver.mod.oauth.dao.UserEntity;
import org.mj.comm.util.MySqlXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 访客授权过程
 */
class GuestAuthProc implements IOAuthProc {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GuestAuthProc.class);

    /**
     * t_user 数据表 guest_id 列
     */
    static private final String T_USER_GUEST_ID = "guest_id";

    /**
     * 初始房卡数量
     */
    static private final int INIT_ROOM_CARD = 9999;

    /**
     * Code
     */
    private final String _code;

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
    GuestAuthProc(JSONObject joProperty) {
        if (null == joProperty ||
            joProperty.isEmpty()) {
            throw new IllegalArgumentException("joProperty 参数无效");
        }

        _code = joProperty.getString("code");
        // 获取客户端 IP 地址和版本号
        _clientIpAddr = joProperty.getString("clientIpAddr");
        _clientVer = joProperty.getString("clientVer");
    }

    @Override
    public int getAsyncOpId() {
        if (null == _code ||
            _code.isEmpty()) {
            return 0;
        } else {
            return _code.charAt(_code.length() - 1);
        }
    }

    @Override
    public String getTempId() {
        return "Guest_" + _code;
    }

    @Override
    public int doAuth() {
        // 游客 Id
        final String strGuestId = getTempId();

        try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            // 获取 DAO
            final IUserDao userDao = sessionX.getMapper(IUserDao.class);

            // 获取用户实体
            UserEntity userEntity = userDao.getEntityByColumnX(T_USER_GUEST_ID, strGuestId);
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
                userDao.updateColumnXByUserId(userId, T_USER_GUEST_ID, strGuestId);
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
