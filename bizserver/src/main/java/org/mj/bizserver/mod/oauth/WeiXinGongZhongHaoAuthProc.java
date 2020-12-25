package org.mj.bizserver.mod.oauth;

/**
 * 微信公众号授权过程
 */
class WeiXinGongZhongHaoAuthProc implements IOAuthProc {
    @Override
    public int getAsyncOpId() {
        return 0;
    }

    @Override
    public String getTempId() {
        return "";
    }
    
    @Override
    public int doAuth() {
        return -1;
    }
}
