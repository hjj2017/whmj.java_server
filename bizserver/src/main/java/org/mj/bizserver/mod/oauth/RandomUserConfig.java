package org.mj.bizserver.mod.oauth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 随机用户配置
 */
final class RandomUserConfig {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GuestAuthProc.class);

    /**
     * 用户名称数组
     */
    static private String[] _userNameArray;

    /**
     * 头像数组
     */
    static private String[] _headImgArray;

    /**
     * 初始化锁
     */
    static private final ReentrantLock INIT_LOCKER = new ReentrantLock();

    /**
     * 随机对象
     */
    static private final Random RAND = new Random();

    /**
     * 私有化类默认构造器
     */
    private RandomUserConfig() {
    }

    /**
     * 随机一个用户名称
     *
     * @return 用户名称
     */
    static String randomAUserName() {
        // 获取用户名称数组
        String[] userNameArray = getUserNameArray();

        if (null == userNameArray ||
            userNameArray.length <= 0) {
            return "Anonymous";
        }

        String newUserName = "";

        // 创建姓氏
        int index = RAND.nextInt(userNameArray.length);
        newUserName += userNameArray[index];
        // 创建名字
        index = RAND.nextInt(userNameArray.length);
        newUserName += userNameArray[index];

        return newUserName;
    }

    /**
     * 获取用户名称数组
     *
     * @return 用户名称数组
     */
    static private String[] getUserNameArray() {
        // 初始化用户名称和头像
        initUserNameAndHeadImg();

        return _userNameArray;
    }

    /**
     * 随机一个头像
     *
     * @return 头像
     */
    static String randomAHeadImg() {
        // 获取用户头像数组
        String[] headImgArray = getHeadImgArray();

        if (null == headImgArray ||
            headImgArray.length <= 0) {
            return null;
        }

        String newHeadImg = "";

        // 创建头像
        int index = RAND.nextInt(headImgArray.length);
        newHeadImg += headImgArray[index];

        return newHeadImg;
    }

    /**
     * 获取用户名称数组
     *
     * @return 用户名称数组
     */
    static private String[] getHeadImgArray() {
        // 初始化用户名称和头像
        initUserNameAndHeadImg();

        return _headImgArray;
    }

    /**
     * 初始化用户名称和头像
     */
    static private void initUserNameAndHeadImg() {
        if (null != _userNameArray &&
            null != _headImgArray) {
            return;
        }

        final String packageName = GuestAuthProc.class.getPackageName();
        final String path = packageName.replace(".", "/");
        final String jsonFile = path + "/RandomUserConfig.json";

        // 获取输入流
        try (InputStream inS = GuestAuthProc.class.getClassLoader().getResourceAsStream(jsonFile)) {
            if (null == inS) {
                return;
            }

            if (!INIT_LOCKER.tryLock(200, TimeUnit.MILLISECONDS)) {
                // 加锁失败
                return;
            }

            // 获取访客配置
            JSONObject joGuestConf = JSONObject.parseObject(inS, JSONObject.class);

            if (null == joGuestConf ||
                joGuestConf.isEmpty()) {
                return;
            }

            JSONArray joArray = joGuestConf.getJSONArray("userNameArray");
            _userNameArray = new String[joArray.size()];

            for (int i = 0; i < joArray.size(); i++) {
                _userNameArray[i] = joArray.getString(i);
            }

            joArray = joGuestConf.getJSONArray("headImgArray");
            _headImgArray = new String[joArray.size()];

            for (int j = 0; j < joArray.size(); j++) {
                _headImgArray[j] = joArray.getString(j);
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            // 解锁
            INIT_LOCKER.unlock();
        }
    }

    /**
     * 随机一个性别
     *
     * @return 性别, -1 = 未知, 0 = 女, 1 = 男, 2 = 双性
     */
    static int randomASex() {
        return RAND.nextInt(2);
    }
}
