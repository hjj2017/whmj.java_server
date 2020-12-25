package org.mj.bizserver.foundation;

/**
 * 业务结果
 *
 * @param <T> 最终结果类型
 */
public final class BizResultWrapper<T> {
    /**
     * 错误编号
     */
    private int _errorCode;

    /**
     * 错误消息
     */
    private String _errorMsg;

    /**
     * 最终结果
     */
    private T _finalResult;

    /**
     * 获取错误编号
     *
     * @return 错误编号
     */
    public int getErrorCode() {
        return _errorCode;
    }

    /**
     * 设置错误编号
     *
     * @param val 整数值
     */
    public void setErrorCode(int val) {
        _errorCode = val;
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    public String getErrorMsg() {
        return _errorMsg;
    }

    /**
     * 设置错误消息
     *
     * @param val 字符串值
     */
    public void setErrorMsg(String val) {
        _errorMsg = val;
    }

    /**
     * 获取最终结果
     *
     * @return 最终结果
     */
    public T getFinalResult() {
        return _finalResult;
    }

    /**
     * 设置最终结果
     *
     * @param val 最终结果
     */
    public void setFinalResult(T val) {
        _finalResult = val;
    }
}
