-- MySQL dump 10.17  Distrib 10.3.24-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: mj_log_template
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
-- Table structure for table `t_template_room_log`
--

DROP TABLE IF EXISTS `t_template_room_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_template_room_log` (
  `room_uuid` varchar(32) NOT NULL COMMENT '房间 UUId',
  `room_id` int(11) DEFAULT NULL COMMENT '房间 Id, 6 位数字',
  `owner_id` int(11) DEFAULT NULL COMMENT '房主用户 Id',
  `club_id` int(11) DEFAULT NULL COMMENT '亲友圈 Id',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `over_time` bigint(20) DEFAULT NULL COMMENT '结束时间',
  `game_type_0` varchar(16) DEFAULT NULL COMMENT '游戏类型 0',
  `game_type_1` varchar(16) DEFAULT NULL COMMENT '游戏类型 1',
  `rule_setting` varchar(2048) DEFAULT NULL COMMENT '( JSON 字符串 ) 房间规则设置',
  `all_player` varchar(2048) DEFAULT NULL COMMENT '( JSON 字符串 ) 所有玩家',
  `all_total_score` varchar(2048) DEFAULT NULL COMMENT '( JSON 字符串 ) 所有总分',
  `cost_room_card` int(11) DEFAULT NULL COMMENT '消耗房卡数量',
  `actual_round_count` int(11) DEFAULT NULL COMMENT '实际局数',
  `user_id_0` int(11) DEFAULT NULL COMMENT '用户 Id 0',
  `user_id_1` int(11) DEFAULT NULL COMMENT '用户 Id 1',
  `user_id_2` int(11) DEFAULT NULL COMMENT '用户 Id 2',
  `user_id_3` int(11) DEFAULT NULL COMMENT '用户 Id 3',
  `user_id_4` int(11) DEFAULT NULL COMMENT '用户 Id 4',
  `user_id_5` int(11) DEFAULT NULL COMMENT '用户 Id 5',
  `curr_state` tinyint(4) DEFAULT NULL COMMENT '当前状态',
  PRIMARY KEY (`room_uuid`),
  KEY `IX_club_id` (`club_id`),
  KEY `IX_game_type_0` (`game_type_0`),
  KEY `IX_game_type_1` (`game_type_1`),
  KEY `IX_user_id_0` (`user_id_0`),
  KEY `IX_user_id_1` (`user_id_1`),
  KEY `IX_user_id_2` (`user_id_2`),
  KEY `IX_user_id_3` (`user_id_3`),
  KEY `IX_user_id_4` (`user_id_4`),
  KEY `IX_user_id_5` (`user_id_5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_template_round_log`
--

DROP TABLE IF EXISTS `t_template_round_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_template_round_log` (
  `room_uuid` varchar(32) NOT NULL COMMENT '房间 UUId',
  `round_index` int(11) NOT NULL COMMENT '牌局索引, 从 0 开始',
  `create_time` bigint(20) DEFAULT NULL COMMENT '牌局创建时间',
  `all_player` varchar(2048) DEFAULT NULL COMMENT '所有玩家',
  `all_curr_score` varchar(2048) DEFAULT NULL COMMENT '所有当前分数',
  `user_id_0` int(11) DEFAULT NULL COMMENT '用户 Id 0',
  `user_id_1` int(11) DEFAULT NULL COMMENT '用户 Id 1',
  `user_id_2` int(11) DEFAULT NULL COMMENT '用户 Id 2',
  `user_id_3` int(11) DEFAULT NULL COMMENT '用户 Id 3',
  `user_id_4` int(11) DEFAULT NULL COMMENT '用户 Id 4',
  `user_id_5` int(11) DEFAULT NULL COMMENT '用户 Id 5',
  `playback_code` varchar(32) DEFAULT NULL COMMENT '回放码',
  `playback_stub` varchar(256) DEFAULT NULL COMMENT '回放存根',
  PRIMARY KEY (`room_uuid`,`round_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_template_user_game_log`
--

DROP TABLE IF EXISTS `t_template_user_game_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_template_user_game_log` (
  `room_uuid` varchar(64) NOT NULL COMMENT '房间 UUId',
  `user_id` int(11) NOT NULL COMMENT '用户 Id',
  `club_id` int(11) DEFAULT NULL COMMENT '亲友圈 Id',
  `room_id` int(11) DEFAULT NULL COMMENT '房间 Id',
  `game_type_0` int(11) DEFAULT NULL COMMENT '游戏类型 0',
  `game_type_1` int(11) DEFAULT NULL COMMENT '游戏类型 1',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `total_score` int(11) DEFAULT NULL COMMENT '总分数',
  `is_winner` tinyint(4) DEFAULT NULL COMMENT '是否是大赢家',
  PRIMARY KEY (`room_uuid`,`user_id`),
  KEY `IX_user_id` (`user_id`),
  KEY `IX_club_id` (`club_id`),
  KEY `IX_game_type_0` (`game_type_0`),
  KEY `IX_game_type_1` (`game_type_1`)
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
