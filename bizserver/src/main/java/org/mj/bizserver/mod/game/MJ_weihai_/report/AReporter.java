package org.mj.bizserver.mod.game.MJ_weihai_.report;

import java.util.ArrayList;
import java.util.List;

/**
 * 记者
 */
public class AReporter {
    /**
     * 报告词条列表
     */
    private final List<IWordz> _wordzList = new ArrayList<>();

    /**
     * 添加词条
     *
     * @param w 词条
     * @return 当前词条
     */
    public IWordz addWordz(IWordz w) {
        if (null != w) {
            _wordzList.add(w);
        }

        return w;
    }

    /**
     * 获取词条列表
     *
     * @return 词条列表
     */
    public List<IWordz> getWordzList() {
        return _wordzList;
    }
}
