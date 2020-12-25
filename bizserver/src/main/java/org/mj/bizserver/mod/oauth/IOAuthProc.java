package org.mj.bizserver.mod.oauth;

/**
 * 登录过程接口
 */
public interface IOAuthProc {
    /**
     * 异步操作 Id, 主要用于分派登录线程
     *
     * @return 临时 Id
     */
    int getAsyncOpId();

    /**
     * 获取临时 Id
     *
     * @return 临时 Id
     */
    String getTempId();

    /**
     * 执行授权
     *
     * @return 用户 Id
     */
    int doAuth();
}
