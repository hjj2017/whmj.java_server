package org.mj.bizserver.allmsg;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MsgRecognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 内部服务器通信消息
 */
public final class InternalServerMsg {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(InternalServerMsg.class);

    /**
     * 代理服务器 Id
     */
    private String _proxyServerId;

    /**
     * 远程会话 Id, 也就是客户端连接代理服务器的标识 Id
     */
    private int _remoteSessionId = -1;

    /**
     * 来自用户 Id
     */
    private int _fromUserId = -1;

    /**
     * 客户端 IP 地址
     */
    private String _clientIP = null;

    /**
     * 消息编码
     */
    private int _msgCode;

    /**
     * 消息体
     */
    private byte[] _msgBody;

    /**
     * 获取代理服务器 Id
     *
     * @return 代理服务器 Id
     */
    public String getProxyServerId() {
        return _proxyServerId;
    }

    /**
     * 设置代理服务器 Id
     *
     * @param val 代理服务器 Id
     * @return this 指针
     */
    public InternalServerMsg setProxyServerId(String val) {
        _proxyServerId = val;
        return this;
    }

    /**
     * 获取远程会话 Id
     *
     * @return 远程会话 Id
     */
    public int getRemoteSessionId() {
        return _remoteSessionId;
    }

    /**
     * 设置远程会话 Id
     *
     * @param val 远程会话 Id
     * @return this 指针
     */
    public InternalServerMsg setRemoteSessionId(int val) {
        _remoteSessionId = val;
        return this;
    }

    /**
     * 获取来自用户 Id
     *
     * @return 来自用户 Id
     */
    public int getFromUserId() {
        return _fromUserId;
    }

    /**
     * 设置来自用户 Id
     *
     * @param val 来自用户 Id
     * @return this 指针
     */
    public InternalServerMsg setFromUserId(int val) {
        _fromUserId = val;
        return this;
    }

    /**
     * 获取客户端 IP 地址
     *
     * @return 客户端 IP 地址
     */
    public String getClientIP() {
        return _clientIP;
    }

    /**
     * 设置客户端 IP 地址
     *
     * @param val 客户端 IP 地址
     * @return this 指针
     */
    public InternalServerMsg setClientIP(String val) {
        _clientIP = val;
        return this;
    }

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
     * @return this 指针
     */
    public InternalServerMsg setMsgCode(int val) {
        _msgCode = val;
        return this;
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
     * @return this 指针
     */
    public InternalServerMsg setMsgBody(byte[] val) {
        _msgBody = val;
        return this;
    }

    /**
     * 承认错误
     *
     * @param resultX 业务结果
     * @return 0 = 没有错误, 否则返回错误编号
     */
    public int admitError(BizResultWrapper<?> resultX) {
        if (null == resultX ||
            0 == resultX.getErrorCode()) {
            return 0;
        }

        CommProtocol.ErrorHintResult.Builder b = CommProtocol.ErrorHintResult.newBuilder();
        b.setErrorCode(resultX.getErrorCode());
        b.setErrorMsg(resultX.getErrorMsg());
        CommProtocol.ErrorHintResult r = b.build();

        putProtoMsg(r);

        return resultX.getErrorCode();
    }

    /**
     * 设置协议消息
     *
     * @param msgObj 消息对象
     */
    public void putProtoMsg(GeneratedMessageV3 msgObj) {
        if (null == msgObj) {
            return;
        }

        _msgCode = MsgRecognizer.getMsgCodeByMsgClazz(msgObj.getClass());
        _msgBody = msgObj.toByteArray();
    }

    /**
     * 获取协议消息
     *
     * @return 消息对象
     */
    public GeneratedMessageV3 getProtoMsg() {
        // 获取消息构建器
        Message.Builder msgBuilder = MsgRecognizer.getMsgBuilderByMsgCode(_msgCode);

        if (null == msgBuilder) {
            LOGGER.error(
                "未找到消息构建器, msgCode = {}",
                _msgCode
            );
            return null;
        }

        try {
            msgBuilder.clear();
            msgBuilder.mergeFrom(_msgBody);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }

        Message newMsg = msgBuilder.build();

        if (newMsg instanceof GeneratedMessageV3) {
            // 如果是 Protobuf 消息,
            return (GeneratedMessageV3) newMsg;
        } else {
            return null;
        }
    }

    /**
     * 释放资源
     */
    public void free() {
        _msgBody = null;
    }
}
