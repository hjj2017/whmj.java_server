package org.mj.bizserver.mod.record;

import org.mj.bizserver.TestIniter;
import org.mj.bizserver.def.GameType0Enum;
import org.mj.bizserver.mod.record.bizdata.RecordSummary;
import org.mj.comm.util.OutParam;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 战绩业务逻辑测试
 */
public class RecordBizLogicTest {
    //@Test
    public void allTest() {
        TestIniter.init();

        final CountDownLatch cd = new CountDownLatch(1);
        final OutParam<Integer> out_totalCount = new OutParam<>();

        RecordBizLogic.getInstance().getRecordList_async(
            7929621, -1, GameType0Enum.MAHJONG, null, 0, 20,
            out_totalCount,
            (resultX) -> {
                if (null == resultX ||
                    null == resultX.getFinalResult()) {
                    throw new RuntimeException("error");
                }

                List<RecordSummary> recordSummaryList = resultX.getFinalResult();

                for (RecordSummary recordSummary : recordSummaryList) {
                    recordSummary.getPlayerList();
                }

                cd.countDown();
            }
        );

        try {
            cd.await();
        } catch (Exception ex) {
            // 打印错误日志
            ex.printStackTrace();
        }
    }
}
