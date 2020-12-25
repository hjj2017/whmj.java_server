package org.mj.comm.util;

/**
 * 输出参数
 *
 * @param <T>
 */
public class OutParam<T> {
    /**
     * 参数值
     */
    private T _val = null;

    /**
     * 获取参数值
     *
     * @return 参数值
     */
    public T getVal() {
        return this._val;
    }

    /**
     * 设置参数值
     *
     * @param val 参数值
     */
    public void setVal(T val) {
        this._val = val;
    }

    /**
     * 获取输出值, 如果输出值为空则使用备选值
     *
     * @param outVal 输出参数
     * @param optVal 可选参数
     * @param <T>    参数类型
     * @return 参数值
     */
    static public <T> T optVal(OutParam<T> outVal, T optVal) {
        return (null == outVal || null == outVal.getVal()) ? optVal : outVal.getVal();
    }

    /**
     * 如果输出参数不为空则设置数值
     *
     * @param out 输出参数
     * @param val 参数值
     */
    static public <T> void putVal(OutParam<T> out, T val) {
        if (null != out) {
            out.setVal(val);
        }
    }
}
