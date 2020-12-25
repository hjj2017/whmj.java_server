package org.mj.bizserver.mod.club.adminctrl;

import org.mj.bizserver.TestIniter;
import org.mj.bizserver.def.GameType0Enum;
import org.mj.bizserver.def.GameType1Enum;
import org.mj.bizserver.mod.club.membercenter.bizdata.FixGameX;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 亲友圈管理员控制逻辑测试
 */
public class AdminCtrlBizLogicTest {
    //@Test
    public void allTest() {
        TestIniter.init();

        final CountDownLatch cd = new CountDownLatch(1);

//        // 创建亲友圈
//        AdminCtrlBizLogic.getInstance().createClub_async(
//            7929621, "AfrX's Club", (resultX) -> {
//            cd.countDown();
//        });

//        // 同意申请
//        AdminCtrlBizLogic.getInstance().approvalToJoin_async(
//            7929621, 8344532, 414984, true, (resultX) -> {
//            cd.countDown();
//        });

//        // 修改角色
//        AdminCtrlBizLogic.getInstance().changeRole_async(
//            7929621, 8344532, 414984, RoleDef.ADMIN, (resultX) -> {
//            cd.countDown();
//        });

//        // 开除成员
//        AdminCtrlBizLogic.getInstance().dismissAMember_async(
//            7929621, 8344532, 414984, (resultX) -> {
//            cd.countDown();
//        });

//        // 充值房卡
//        AdminCtrlBizLogic.getInstance().exchangeRoomCard_async(
//            7929621, 414984, 99, (resultX) -> {
//            cd.countDown();
//        });

        final Map<Integer, Integer> ruleMap = new HashMap<>();
        ruleMap.put(1001, 1);
        ruleMap.put(1002, 2);
        FixGameX fixGameX = new FixGameX();
        fixGameX.setIndex(3);
        fixGameX.setGameType0(GameType0Enum.MAHJONG);
        fixGameX.setGameType1(GameType1Enum.MJ_weihai_);
        fixGameX.setRuleMap(ruleMap);

        // 修改默认玩法
        AdminCtrlBizLogic.getInstance().modifyFixGameX_async(
            7929621, 414984, fixGameX, (resultX) -> {
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
