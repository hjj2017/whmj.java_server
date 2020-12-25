-- MySQL dump 10.17  Distrib 10.3.24-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: mj_game
-- ------------------------------------------------------
-- Server version	10.3.24-MariaDB-2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_club`
--

DROP TABLE IF EXISTS `t_club`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_club` (
  `club_id` int(11) NOT NULL COMMENT '亲友圈 Id',
  `club_name` varchar(64) DEFAULT NULL COMMENT '亲友圈名称',
  `creator_id` int(11) DEFAULT NULL COMMENT '创建人 ( 用户 ) Id',
  `room_card` int(11) DEFAULT NULL COMMENT '房卡数量',
  `notice` varchar(2048) DEFAULT NULL COMMENT '公告文本',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `num_of_people` int(11) DEFAULT NULL COMMENT '人数',
  `curr_state` tinyint(4) DEFAULT NULL COMMENT '当前状态',
  `fix_game_0` varchar(2048) DEFAULT NULL COMMENT '固定玩法 0',
  `fix_game_1` varchar(2048) DEFAULT NULL COMMENT '固定玩法 1',
  `fix_game_2` varchar(2048) DEFAULT NULL COMMENT '固定玩法 2',
  `fix_game_3` varchar(2048) DEFAULT NULL COMMENT '固定玩法 3',
  `fix_game_4` varchar(2048) DEFAULT NULL COMMENT '固定玩法 4',
  `fix_game_5` varchar(2048) DEFAULT NULL COMMENT '固定玩法 5',
  PRIMARY KEY (`club_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_club_member`
--

DROP TABLE IF EXISTS `t_club_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_club_member` (
  `user_id` int(11) NOT NULL COMMENT '用户 Id',
  `club_id` int(11) NOT NULL COMMENT '亲友圈 Id',
  `role` tinyint(4) DEFAULT NULL COMMENT '角色',
  `join_time` bigint(20) DEFAULT NULL COMMENT '加入时间',
  `curr_state` tinyint(4) DEFAULT NULL COMMENT '当前状态',
  PRIMARY KEY (`user_id`,`club_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_cost_room_card_conf`
--

DROP TABLE IF EXISTS `t_cost_room_card_conf`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_cost_room_card_conf` (
  `dummy_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '虚设 Id',
  `game_type_0` int(32) NOT NULL COMMENT '游戏类型 0',
  `game_type_1` int(32) NOT NULL COMMENT '游戏类型 1',
  `max_player` tinyint(11) NOT NULL COMMENT '最大玩家数量',
  `max_round` tinyint(11) NOT NULL COMMENT '最大局数',
  `max_circle` tinyint(11) NOT NULL COMMENT '最大圈数',
  `payment_way_club` tinyint(11) DEFAULT NULL COMMENT '亲友圈支付所需房卡数量',
  `payment_way_room_owner` tinyint(11) DEFAULT NULL COMMENT '房主支付所需房卡数量',
  `payment_way_aa` tinyint(11) DEFAULT NULL COMMENT 'AA 支付所需房卡数量',
  PRIMARY KEY (`dummy_id`),
  KEY `IX_game_type_0` (`game_type_0`),
  KEY `IX_game_type_1` (`game_type_1`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_user` (
  `user_id` int(11) NOT NULL COMMENT '用户 Id',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名称',
  `head_img` varchar(256) DEFAULT NULL COMMENT '头像',
  `sex` tinyint(4) DEFAULT 0 COMMENT '性别',
  `room_card` int(11) DEFAULT 0 COMMENT '房卡数量',
  `create_time` bigint(20) DEFAULT 0 COMMENT '创建时间',
  `client_ver` varchar(16) DEFAULT NULL COMMENT '客户端版本号',
  `last_login_time` bigint(20) DEFAULT 0 COMMENT '最后登录时间',
  `last_login_ip` varchar(32) DEFAULT NULL COMMENT '最后登录 IP',
  `state` tinyint(4) DEFAULT 0 COMMENT '当前状态',
  `phone_number` varchar(16) DEFAULT NULL COMMENT '手机号',
  `guest_id` varchar(64) DEFAULT NULL COMMENT '游客 Id',
  `weixin_open_id` varchar(64) DEFAULT NULL COMMENT '微信 OpenId',
  `weixin_gong_zhong_hao_open_id` varchar(64) DEFAULT NULL COMMENT '微信公众号 OpenId',
  `qq_open_id` varchar(64) DEFAULT NULL COMMENT 'QQ OpenId',
  `liaobei_open_id` varchar(64) DEFAULT NULL COMMENT '聊呗 OpenId',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_phone_number` (`phone_number`),
  UNIQUE KEY `UK_weixin_open_id` (`weixin_open_id`),
  UNIQUE KEY `UK_weixin_gong_zhong_hao_open_id` (`weixin_gong_zhong_hao_open_id`),
  UNIQUE KEY `UK_qq_open_id` (`qq_open_id`),
  UNIQUE KEY `UK_liaobei_open_id` (`liaobei_open_id`),
  UNIQUE KEY `UK_guest_id` (`guest_id`),
  KEY `IX_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-11-17 12:56:33
