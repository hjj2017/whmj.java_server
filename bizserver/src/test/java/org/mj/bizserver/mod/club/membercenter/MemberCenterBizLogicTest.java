package org.mj.bizserver.mod.club.membercenter;

import org.junit.Test;
import org.mj.bizserver.TestIniter;

import java.util.concurrent.CountDownLatch;

/**
 * 亲友圈成员中心业务逻辑测试
 */
public class MemberCenterBizLogicTest {
    //@Test
    public void allTest() {
        TestIniter.init();

        final CountDownLatch cd = new CountDownLatch(1);

        MemberCenterBizLogic.getInstance().joinClub_async(8344532, 414984, (resultX) -> {
            cd.countDown();
        });

        try {
            cd.await();
        } catch (Exception ex) {
            // 打印错误日志
            ex.printStackTrace();
        }
    }
}
