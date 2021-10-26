package org.mj.proxyserver.base;

/**
 * 客户端消息半成品,
 * 包括 "消息编号" 和 "消息体字节数组", 这里并不将字节数组解析成具体的命令对象
 */
public class ClientMsgSemiFinished {
    /**
     * 消息编码
     */
    private int _msgCode;

    /**
     * 消息体
     */
    private byte[] _msgBody;

    /**
     * 获取消息编码
     *
     * @return 消息编码
     */
    public int getMsgCode() {
        return _msgCode;
    }

    /**
     * 设置消息编码
     *
     * @param val 消息编码
     */
    public void setMsgCode(int val) {
        _msgCode = val;
    }

    /**
     * 获取消息体字节数组
     *
     * @return 消息体字节数组
     */
    public byte[] getMsgBody() {
        return _msgBody;
    }

    /**
     * 设置消息体字节数组
     *
     * @param val 消息体字节数组
     */
    public void setMsgBody(byte[] val) {
        _msgBody = val;
    }

    /**
     * 释放资源
     */
    public void free() {
        _msgBody = null;
    }
}
