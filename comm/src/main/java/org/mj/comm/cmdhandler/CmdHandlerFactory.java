package org.mj.comm.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.comm.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 命令处理器工厂
 */
public class CmdHandlerFactory {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    /**
     * 处理器字典
     */
    private final Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new ConcurrentHashMap<>();

    /**
     * 扫描 Java 包
     */
    private final String _scanJavaPackage;

    /**
     * 初始化完成
     */
    private volatile boolean _initOK = false;

    /**
     * 类参数构造器
     *
     * @param scanJavaPackage 扫描 Java 包
     */
    public CmdHandlerFactory(String scanJavaPackage) {
        if (null == scanJavaPackage ||
            scanJavaPackage.isEmpty()) {
            throw new IllegalArgumentException("sanJavaPackage is null or empty");
        }

        _scanJavaPackage = scanJavaPackage;
    }

    /**
     * 尝试初始化
     */
    private void tryInit() {
        LOGGER.info("=== 完成命令与处理器的映射 ===");

        // 获取 ICmdHandler 的所有实现类
        Set<Class<?>> cmdHandlerClazzSet = PackageUtil.listSubClazz(
            _scanJavaPackage, true, ICmdHandler.class
        );

        for (Class<?> cmdHandlerClazz : cmdHandlerClazzSet) {
            if (null == cmdHandlerClazz ||
                0 != (cmdHandlerClazz.getModifiers() & Modifier.ABSTRACT)) {
                // 如果是抽象类,
                continue;
            }

            // 获取方法数组
            Method[] methodArray = cmdHandlerClazz.getDeclaredMethods();
            // 命令类
            Class<?> cmdClazz = null;

            for (Method currMethod : methodArray) {
                if (!currMethod.getName().equals("handle")) {
                    continue;
                }

                // 获取函数参数数组
                Class<?>[] paramTypeArray = currMethod.getParameterTypes();

                if (paramTypeArray.length < 4 ||
                    paramTypeArray[3] == GeneratedMessageV3.class || // 如果是 GeneratedMessageV3 消息本身, 则直接跳过!
                    !GeneratedMessageV3.class.isAssignableFrom(paramTypeArray[3])) {
                    continue;
                }

                cmdClazz = paramTypeArray[3];
                break;
            }

            if (null == cmdClazz) {
                continue;
            }

            try {
                // 创建指令处理器
                ICmdHandler<?> cmdHandlerImpl = (ICmdHandler<?>) cmdHandlerClazz.getDeclaredConstructor().newInstance();

                LOGGER.info(
                    "关联 {} <==> {}",
                    cmdClazz.getName(),
                    cmdHandlerClazz.getName()
                );

                _handlerMap.put(
                    cmdClazz, cmdHandlerImpl
                );
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * 根据消息创建命令处理器
     *
     * @param cmdClazz 命令类
     * @return 命令处理器
     */
    public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> cmdClazz) {
        if (null == cmdClazz) {
            return null;
        }

        if (!_initOK) {
            synchronized (this) {
                if (!_initOK) {
                    tryInit();
                    _initOK = true;
                }
            }
        }

        return _handlerMap.get(cmdClazz);
    }
}
