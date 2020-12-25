package org.mj.bizserver.mod.game.MJ_weihai_.report;

import java.util.List;

/**
 * 记者小队,
 * 主要用于消息群发和战报记录
 */
public class ReporterTeam {
    /**
     * 所属房间 Id
     */
    private final int _roomId;

    /**
     * 私人事件记者
     */
    private final AReporter _rptr0 = new AReporter();

    /**
     * 公共事件记者
     */
    private final AReporter _rptr1 = new AReporter();

    /**
     * 回放事件记者
     */
    private final AReporter _rptr2 = new AReporter();

    /**
     * 类参数构造器
     *
     * @param roomId 所属房间 Id
     */
    public ReporterTeam(int roomId) {
        _roomId = roomId;
    }

    /**
     * 获取所属房间 Id
     *
     * @return 房间 Id
     */
    public int getRoomId() {
        return _roomId;
    }

    /**
     * 添加私人词条
     *
     * @param w 词条
     * @return 当前添加的词条
     */
    public IWordz addPrivateWordz(
        IWordz w) {
        return _rptr0.addWordz(w);
    }

    /**
     * 获取私人词条列表
     *
     * @return 词条列表
     */
    public List<IWordz> getPrivateWordzList() {
        return _rptr0.getWordzList();
    }

    /**
     * 添加公共词条
     *
     * @param w 词条
     * @return 当前添加的词条
     */
    public IWordz addPublicWordz(
        IWordz w) {
        return _rptr1.addWordz(w);
    }

    /**
     * 获取公共词条列表
     *
     * @return 词条列表
     */
    public List<IWordz> getPublicWordzList() {
        return _rptr1.getWordzList();
    }

    /**
     * 添加回放词条
     *
     * @param w 当前添加的词条
     * @return 当前添加的词条
     */
    public IWordz addPlaybackWordz(
        IWordz w) {
        return _rptr2.addWordz(w);
    }

    /**
     * 获取回放词条列表
     *
     * @return 词条列表
     */
    public List<IWordz> getPlaybackWordzList() {
        return _rptr2.getWordzList();
    }
}
