package org.mj.bizserver.mod.record;

/**
 * 战绩业务逻辑
 */
public final class RecordBizLogic implements
    RecordBizLogic$getRecordDetailz_async,
    RecordBizLogic$getRecordList_async,
    RecordBizLogic$saveARecord {
    /**
     * 单例对象
     */
    static private final RecordBizLogic _instance = new RecordBizLogic();

    /**
     * 私有化类默认构造器
     */
    private RecordBizLogic() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public RecordBizLogic getInstance() {
        return _instance;
    }
}
