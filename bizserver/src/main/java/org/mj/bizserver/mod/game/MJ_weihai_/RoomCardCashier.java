package org.mj.bizserver.mod.game.MJ_weihai_;

import org.apache.ibatis.session.SqlSession;
import org.mj.bizserver.def.GameType1Enum;
import org.mj.bizserver.def.PaymentWayEnum;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.club.membercenter.MemberCenterBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.ClubDetailz;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RuleSetting;
import org.mj.bizserver.mod.game.MJ_weihai_.dao.CostRoomCardConfEntity;
import org.mj.bizserver.mod.game.MJ_weihai_.dao.ICostRoomCardConfDao;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.comm.util.MySqlXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * 房卡收银员,
 * XXX 注意: 在这里验证用户的房卡数量是否足够以及扣除房卡操作...
 */
final class RoomCardCashier {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RoomCardCashier.class);

    /**
     * 私有化类默认构造器
     */
    private RoomCardCashier() {
    }

    /**
     * 房卡数量是否足够
     *
     * @param userId   用户 Id
     * @param joinRoom 加入房间
     * @return true = 有足够的房卡, false = 房卡不足
     */
    static public boolean hasEnoughRoomCard(int userId, Room joinRoom) {
        if (userId <= 0 ||
            null == joinRoom) {
            return false;
        }

        return hasEnoughRoomCard(
            userId,
            joinRoom.getRuleSetting(), joinRoom.getOwnerId(), joinRoom.getClubId()
        );
    }

    /**
     * 是否有足够的房卡
     *
     * @param userId      用户 Id
     * @param ruleSetting 规则设置
     * @param roomOwnerId 房主用户 Id
     * @param clubId      亲友圈 Id
     * @return true = 有足够的房卡, false = 房卡不足
     */
    static public boolean hasEnoughRoomCard(int userId, RuleSetting ruleSetting, int roomOwnerId, int clubId) {
        if (userId <= 0 ||
            null == ruleSetting) {
            return false;
        }

        // 查找消耗房卡配置
        final CostRoomCardConfEntity foundConf = findCostRoomCardConf(ruleSetting);

        if (null == foundConf) {
            LOGGER.error(
                "消耗房卡配置为空, userId = {}, gameType0 = {}, gameType1 = {}, maxPlayer = {}, maxRound = {}, maxCircle = {}",
                userId,
                GameType1Enum.MJ_weihai_.getGameType0().getStrVal(),
                GameType1Enum.MJ_weihai_.getStrVal(),
                ruleSetting.getMaxPlayer(),
                ruleSetting.getMaxRound(),
                ruleSetting.getMaxCircle()
            );
            return false;
        }

        if (ruleSetting.getPaymentWay() == PaymentWayEnum.ROOM_OWNER ||
            ruleSetting.getPaymentWay() == PaymentWayEnum.AA) {
            // 获取玩家详情
            final BizResultWrapper<UserDetailz> resultA = new BizResultWrapper<>();
            UserInfoBizLogic.getInstance().getUserDetailzByUserId(
                userId, resultA
            );
            final UserDetailz currUser = resultA.getFinalResult();

            if (null == currUser) {
                LOGGER.error(
                    "用户详情为空, userId = {}",
                    userId
                );
                return false;
            }

            if (ruleSetting.getPaymentWay() == PaymentWayEnum.ROOM_OWNER) {
                // 如果是房主支付
                return currUser.getUserId() != roomOwnerId
                    || currUser.getRoomCard() >= foundConf.getPaymentWayRoomOwner();
            } else if (ruleSetting.getPaymentWay() == PaymentWayEnum.AA) {
                // 如果是 AA 支付
                return currUser.getRoomCard() >= foundConf.getPaymentWayAA();
            }
        } else if (ruleSetting.getPaymentWay() == PaymentWayEnum.CLUB) {
            // 获取亲友圈详情
            BizResultWrapper<ClubDetailz> resultA = new BizResultWrapper<>();
            MemberCenterBizLogic.getInstance().getClubDetailz(userId, clubId, resultA);
            ClubDetailz currClub = resultA.getFinalResult();

            return null != currClub
                && currClub.getRoomCard() >= foundConf.getPaymentWayClub();
        }

        return false;
    }

    /**
     * 消耗房卡
     *
     * @param currRoom 当前房间
     */
    static public void costRoomCard(Room currRoom) {
        if (null == currRoom) {
            return;
        }

        // 获取规则设置
        final RuleSetting ruleSetting = currRoom.getRuleSetting();
        // 查找消耗房卡配置
        final CostRoomCardConfEntity foundConf = findCostRoomCardConf(ruleSetting);

        if (null == foundConf) {
            LOGGER.error(
                "消耗房卡配置为空, roomId = {}",
                currRoom.getRoomId()
            );
            return;
        }

        if (ruleSetting.getPaymentWay() == PaymentWayEnum.ROOM_OWNER) {
            // 业务结果
            final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

            // 房主减房卡
            UserInfoBizLogic.getInstance().costRoomCard(
                currRoom.getOwnerId(),
                foundConf.getPaymentWayRoomOwner(),
                resultX
            );

            if (Boolean.TRUE != resultX.getFinalResult()) {
                LOGGER.error(
                    "房主扣除房卡失败, userId = {}, atRoomId = {}",
                    currRoom.getOwnerId(),
                    currRoom.getRoomId()
                );
                return;
            }

            LOGGER.info(
                "房主扣除房卡成功, userId = {}, atRoomId = {}, costRoomCard = {}",
                currRoom.getOwnerId(),
                currRoom.getRoomId(),
                foundConf.getPaymentWayRoomOwner()
            );

            // 设置消耗的房卡数量
            currRoom.setCostRoomCard(
                foundConf.getPaymentWayRoomOwner()
            );
        } else if (ruleSetting.getPaymentWay() == PaymentWayEnum.AA) {
            int costRoomCard = 0;

            for (Player currPlayer : currRoom.getPlayerListCopy()) {
                if (null == currPlayer) {
                    continue;
                }

                // 业务结果
                final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

                // 每个人都减房卡
                UserInfoBizLogic.getInstance().costRoomCard(
                    currPlayer.getUserId(),
                    foundConf.getPaymentWayAA(),
                    resultX
                );

                if (Boolean.TRUE != resultX.getFinalResult()) {
                    LOGGER.error(
                        "AA 扣除房卡失败, userId = {}, atRoomId = {}",
                        currPlayer.getUserId(),
                        currRoom.getRoomId()
                    );
                    continue;
                }

                LOGGER.info(
                    "AA 扣除房卡成功, userId = {}, atRoomId = {}, costRoomCard = {}",
                    currPlayer.getUserId(),
                    currRoom.getRoomId(),
                    foundConf.getPaymentWayAA()
                );

                costRoomCard += foundConf.getPaymentWayAA();
            }

            // 设置消耗的房卡数量
            currRoom.setCostRoomCard(costRoomCard);
        } else if (ruleSetting.getPaymentWay() == PaymentWayEnum.CLUB) {
            // 业务结果
            final BizResultWrapper<Boolean> resultX = new BizResultWrapper<>();

            // 亲友圈减房卡
            MemberCenterBizLogic.getInstance().costRoomCard(
                currRoom.getClubId(),
                foundConf.getPaymentWayClub(),
                resultX
            );

            if (Boolean.TRUE != resultX.getFinalResult()) {
                LOGGER.error(
                    "亲友圈扣除房卡失败, userId = {}, atRoomId = {}",
                    currRoom.getOwnerId(),
                    currRoom.getRoomId()
                );
                return;
            }

            LOGGER.info(
                "亲友圈扣除房卡成功, userId = {}, atRoomId = {}, costRoomCard = {}",
                currRoom.getOwnerId(),
                currRoom.getRoomId(),
                foundConf.getPaymentWayRoomOwner()
            );

            // 设置消耗的房卡数量
            currRoom.setCostRoomCard(
                foundConf.getPaymentWayRoomOwner()
            );
        }
    }

    /**
     * 查找消耗房卡配置
     *
     * @param ruleSetting 规则设置
     * @return 消耗房卡配置实体
     */
    static private CostRoomCardConfEntity findCostRoomCardConf(RuleSetting ruleSetting) {
        if (null == ruleSetting) {
            return null;
        }

        // 获取消耗房卡配置实体列表
        List<CostRoomCardConfEntity> confList = listCostRoomCardConf();

        for (CostRoomCardConfEntity currConf : confList) {
            if (null == currConf ||
                currConf.getMaxPlayer() != ruleSetting.getMaxPlayer()) {
                continue;
            }

            if (ruleSetting.getMaxRound() > 0 &&
                ruleSetting.getMaxRound() == currConf.getMaxRound()) {
                return currConf;
            }

            if (ruleSetting.getMaxCircle() > 0 &&
                ruleSetting.getMaxCircle() == currConf.getMaxCircle()) {
                return currConf;
            }
        }

        return null;
    }

    /**
     * 获取消耗房卡配置列表
     *
     * @return 消耗房卡配置列表
     */
    static private List<CostRoomCardConfEntity> listCostRoomCardConf() {
        try (SqlSession sessionX = MySqlXuite.openGameDbSession()) {
            return sessionX.getMapper(ICostRoomCardConfDao.class).listByGameType(
                GameType1Enum.MJ_weihai_.getGameType0().getIntVal(),
                GameType1Enum.MJ_weihai_.getIntVal()
            );
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return Collections.emptyList();
    }
}
