package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;

/**
 * 麻将荒庄
 */
public class Wordz_MahjongHuangZhuang implements IWordz {
    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return null;
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.MahjongHuangZhuangBroadcast.newBuilder().build();
    }

    @Override
    public JSONObject buildJSONObj() {
        final JSONObject jsonObj = new JSONObject();
        jsonObj.put("clazzName", this.getClass().getSimpleName());
        return jsonObj;
    }
}
