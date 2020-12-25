package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;

/**
 * 词条,
 * 主要用于记录游戏中的事件, 之后用于消息发送和广播
 */
public interface IWordz {
    /**
     * 获取用户 Id
     *
     * @return 用户 Id
     */
    default int getUserId() {
        return -1;
    }

    /**
     * 构建结果消息
     *
     * @return 结果消息
     */
    GeneratedMessageV3 buildResultMsg();

    /**
     * 构建广播消息
     *
     * @return 广播消息
     */
    GeneratedMessageV3 buildBroadcastMsg();

    /**
     * 构建 JSON 对象
     *
     * @return JSON 对象
     */
    default JSONObject buildJSONObj() {
        return null;
    }

    /**
     * 创建遮挡拷贝
     *
     * @return 词条
     */
    default IWordz createMaskCopy() {
        return this;
    }
}
