package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

/**
 * 地理位置
 */
public class GeoLocation {
    /**
     * 纬度
     */
    private float _latitude;

    /**
     * 经度
     */
    private float _longitude;

    /**
     * 海拔
     */
    private float _altitude;

    /**
     * 客户端 IP 地址
     */
    private String _clientIpAddr;

    /**
     * 获取纬度
     *
     * @return 纬度
     */
    public float getLatitude() {
        return _latitude;
    }

    /**
     * 设置纬度
     *
     * @param val 浮点数
     * @return this 指针
     */
    public GeoLocation setLatitude(float val) {
        _latitude = val;
        return this;
    }

    /**
     * 获取经度
     *
     * @return 经度
     */
    public float getLongitude() {
        return _longitude;
    }

    /**
     * 设置经度
     *
     * @param val 浮点数
     * @return this 指针
     */
    public GeoLocation setLongitude(float val) {
        _longitude = val;
        return this;
    }

    /**
     * 获取海拔高度
     *
     * @return 海拔高度
     */
    public float getAltitude() {
        return _altitude;
    }

    /**
     * 设置海拔高度
     *
     * @param val 浮点数
     * @return this 指针
     */
    public GeoLocation setAltitude(float val) {
        _altitude = val;
        return this;
    }

    /**
     * 获取客户端 IP 地址
     *
     * @return 客户端 IP 地址
     */
    public String getClientIpAddr() {
        return _clientIpAddr;
    }

    /**
     * 设置客户端 IP 地址
     *
     * @param val 字符串
     * @return this 指针
     */
    public GeoLocation setClientIpAddr(String val) {
        _clientIpAddr = val;
        return this;
    }
}
