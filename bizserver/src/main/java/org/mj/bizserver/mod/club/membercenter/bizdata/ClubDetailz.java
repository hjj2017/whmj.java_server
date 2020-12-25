package org.mj.bizserver.mod.club.membercenter.bizdata;

import org.mj.bizserver.mod.club.membercenter.dao.ClubEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 亲友圈详情
 */
public class ClubDetailz {
    /**
     * 亲友圈 Id
     */
    private int _clubId;

    /**
     * 亲友圈名称
     */
    private String _clubName;

    /**
     * 创建时间
     */
    private long _createTime;

    /**
     * 创建人 ( 用户 ) Id
     */
    private int _creatorId;

    /**
     * 创建人 ( 用户 ) 名称
     */
    private String _creatorName;

    /**
     * 创建人头像
     */
    private String _creatorHeadImg;

    /**
     * 创建人性别
     */
    private int _creatorSex;

    /**
     * 房卡数量
     */
    private int _roomCard;

    /**
     * 固定玩法数组
     */
    private List<FixGameX> _fixGameXList = null;

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
     * 公告信息
     */
    private String _notice;

    /**
     * 我的角色
     */
    private RoleDef _myRole;

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
     * 获取创建时间
     *
     * @return 创建时间
     */
    public long getCreateTime() {
        return _createTime;
    }

    /**
     * 设置创建时间
     *
     * @param val 创建时间
     */
    public void setCreateTime(long val) {
        _createTime = val;
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
     * 获取创建人性别
     *
     * @return 创建人性别, -1 = 未知, 0 = 女, 1 = 男, 2 = 双性
     */
    public int getCreatorSex() {
        return _creatorSex;
    }

    /**
     * 设置创建人性别
     *
     * @param val 创建人性别, -1 = 未知, 0 = 女, 1 = 男, 2 = 双性
     */
    public void setCreatorSex(int val) {
        _creatorSex = val;
    }

    /**
     * 获取房卡数量
     *
     * @return 房卡数量
     */
    public int getRoomCard() {
        return _roomCard;
    }

    /**
     * 设置房卡数量
     *
     * @param val 房卡数量
     */
    public void setRoomCard(int val) {
        _roomCard = val;
    }

    /**
     * 获取固定玩法列表
     *
     * @return 固定玩法列表
     */
    public List<FixGameX> getFixGameXList() {
        return _fixGameXList;
    }

    /**
     * 设置固定玩法列表
     *
     * @param val 固定玩法列表
     */
    public void setFixGameXList(List<FixGameX> val) {
        _fixGameXList = val;
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
     * 获取公告信息
     *
     * @return 公告信息
     */
    public String getNotice() {
        return Objects.requireNonNullElse(_notice, "");
    }

    /**
     * 设置公告信息
     *
     * @param val 公告信息
     */
    public void setNotice(String val) {
        _notice = val;
    }

    /**
     * 获取我的角色
     *
     * @return 我的角色
     */
    public RoleDef getMyRole() {
        return _myRole;
    }

    /**
     * 获取我的角色的整数值
     *
     * @return 我的角色整数值
     */
    public int getMyRoleIntVal() {
        return null == _myRole ? -1 : _myRole.getIntVal();
    }

    /**
     * 设置我的角色
     *
     * @param val 我的角色
     */
    public void setMyRole(RoleDef val) {
        _myRole = val;
    }

    /**
     * 从实体中创建业务对象
     *
     * @param entity 实体
     * @return 业务对象
     */
    static public ClubDetailz fromEntity(ClubEntity entity) {
        if (null == entity) {
            return null;
        }

        final ClubDetailz bizObj = new ClubDetailz();
        bizObj.setClubId(entity.getClubId());
        bizObj.setClubName(entity.getClubName());
        bizObj.setCreateTime(entity.getCreateTime());
        bizObj.setCreatorId(entity.getCreatorId());
        bizObj.setRoomCard(entity.getRoomCard());
        bizObj.setNumOfPeople(entity.getNumOfPeople());
        bizObj.setNumOfGaming(0);
        bizObj.setNumOfWaiting(0);
        bizObj.setNotice(entity.getNotice());
        bizObj.setMyRole(null); // 先设置为空

        List<FixGameX> fixGameXList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            // 获取固定玩法 JSON 字符串
            String jsonStr = entity.getFixGameX(i);

            if (null == jsonStr ||
                !jsonStr.startsWith("{")) {
                continue;
            }

            FixGameX fixGameX = FixGameX.fromJSONStr(jsonStr);
            fixGameX.setIndex(i);

            if (null != fixGameX) {
                fixGameXList.add(fixGameX);
            }
        }

        bizObj.setFixGameXList(fixGameXList);
        return bizObj;
    }
}
