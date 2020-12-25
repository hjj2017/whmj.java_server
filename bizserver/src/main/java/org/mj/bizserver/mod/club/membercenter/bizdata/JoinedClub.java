package org.mj.bizserver.mod.club.membercenter.bizdata;

import org.mj.bizserver.mod.club.membercenter.dao.ClubEntity;

import java.util.Objects;

/**
 * 已经加入的亲友圈
 */
public class JoinedClub {
    /**
     * 亲友圈 Id
     */
    private int _clubId;

    /**
     * 亲友圈名称
     */
    private String _clubName;

    /**
     * 创建人 ( 用户 ) Id
     */
    private int _creatorId;

    /**
     * 创建人 ( 用户 ) 名称
     */
    private String _creatorName;

    /**
     * 创建人 ( 用户 ) 性别
     */
    private int _creatorSex;

    /**
     * 创建人头像
     */
    private String _creatorHeadImg;

    /**
     * 亲友圈人数
     */
    private int _numOfPeople;

    /**
     * 游戏中的数量 ( 桌数 )
     */
    private int _numOfGaming;

    /**
     * 等待数量 ( 桌数 )
     */
    private int _numOfWaiting;

    /**
     * 获取亲友圈 Id
     *
     * @return 亲友圈 Id
     */
    public int getClubId() {
        return _clubId;
    }

    /**
     * 设置亲友圈 Id
     *
     * @param val 亲友圈 Id
     */
    public void setClubId(int val) {
        _clubId = val;
    }

    /**
     * 获取亲友圈名称
     *
     * @return 亲友圈名称
     */
    public String getClubName() {
        return Objects.requireNonNullElse(_clubName, "");
    }

    /**
     * 设置亲友圈名称
     *
     * @param val 亲友圈名称
     */
    public void setClubName(String val) {
        _clubName = val;
    }

    /**
     * 获取创建人 ( 用户 ) Id
     *
     * @return 创建人 ( 用户 ) Id
     */
    public int getCreatorId() {
        return _creatorId;
    }

    /**
     * 设置创建人 ( 用户 ) Id
     *
     * @param val 创建人 ( 用户 ) Id
     */
    public void setCreatorId(int val) {
        _creatorId = val;
    }

    /**
     * 获取创建人 ( 用户 ) 名称
     *
     * @return 创建人 ( 用户 ) 名称
     */
    public String getCreatorName() {
        return Objects.requireNonNullElse(_creatorName, "");
    }

    /**
     * 获取创建人 ( 用户 ) 名称
     *
     * @param val 创建人 ( 用户 ) 名称
     */
    public void setCreatorName(String val) {
        _creatorName = val;
    }

    /**
     * 获取创建人 ( 用户 ) 性别
     *
     * @return -1 = 未知, 0 = 女, 1 = 男, 2 = 双性
     */
    public int getCreatorSex() {
        return _creatorSex;
    }

    /**
     * 设置创建人 ( 用户 ) 性别
     *
     * @param val 整数值, -1 = 未知, 0 = 女, 1 = 男, 2 = 双性
     */
    public void setCreatorSex(int val) {
        _creatorSex = val;
    }

    /**
     * 获取创建人头像
     *
     * @return 创建人头像
     */
    public String getCreatorHeadImg() {
        return Objects.requireNonNullElse(_creatorHeadImg, "");
    }

    /**
     * 设置创建人头像
     *
     * @param val 创建人头像
     */
    public void setCreatorHeadImg(String val) {
        _creatorHeadImg = val;
    }

    /**
     * 获取亲友圈人数
     *
     * @return 亲友圈人数
     */
    public int getNumOfPeople() {
        return _numOfPeople;
    }

    /**
     * 设置亲友圈人数
     *
     * @param val 亲友圈人数
     */
    public void setNumOfPeople(int val) {
        _numOfPeople = val;
    }

    /**
     * 获取游戏中的数量 ( 桌数 )
     *
     * @return 游戏中的数量 ( 桌数 )
     */
    public int getNumOfGaming() {
        return _numOfGaming;
    }

    /**
     * 设置游戏中的数量 ( 桌数 )
     *
     * @param val 游戏中的数量 ( 桌数 )
     */
    public void setNumOfGaming(int val) {
        _numOfGaming = val;
    }

    /**
     * 获取等待数量 ( 桌数 )
     *
     * @return 等待数量 ( 桌数 )
     */
    public int getNumOfWaiting() {
        return _numOfWaiting;
    }

    /**
     * 设置等待数量 ( 桌数 )
     *
     * @param val 等待数量 ( 桌数 )
     */
    public void setNumOfWaiting(int val) {
        _numOfWaiting = val;
    }

    /**
     * 从实体中创建业务对象
     *
     * @param entity 实体
     * @return 业务对象
     */
    static public JoinedClub fromEntity(ClubEntity entity) {
        if (null == entity) {
            return null;
        }

        final JoinedClub bizObj = new JoinedClub();
        bizObj.setClubId(entity.getClubId());
        bizObj.setClubName(entity.getClubName());
        bizObj.setCreatorId(entity.getCreatorId());
        bizObj.setNumOfPeople(entity.getNumOfPeople());
        bizObj.setNumOfGaming(0);
        bizObj.setNumOfWaiting(0);

        return bizObj;
    }
}
