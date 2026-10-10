-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: news_course_design
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论id',
  `news_id` bigint NOT NULL COMMENT '所属帖子id',
  `user_id` bigint NOT NULL COMMENT '评论发布者id',
  `parent_id` bigint DEFAULT '0' COMMENT '父评论id，0=一级评论',
  `to_user_id` bigint DEFAULT NULL COMMENT '@回复的目标用户id（可选，显示“回复XX”）',
  `content` text NOT NULL COMMENT '评论内容',
  `status` tinyint DEFAULT '1' COMMENT '0删除 1正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `root_comment_id` bigint DEFAULT NULL COMMENT '根一级评论id，只有一级评论该字段为null/自己id，所有子评论统一存储顶层一级评论id',
  `like_count` int DEFAULT NULL COMMENT '点赞数，冗余字段',
  `dislike` int DEFAULT NULL COMMENT '踩数，冗余字段，不对外公开',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`news_id`) COMMENT '帖子Id',
  KEY `idx_root_comment_id` (`root_comment_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

INSERT INTO `comment` (`id`, `news_id`, `user_id`, `parent_id`, `to_user_id`, `content`, `status`, `create_time`, `update_time`, `root_comment_id`, `like_count`, `dislike`) VALUES (1,11,3,0,NULL,'我认为说得很对',1,'2026-08-26 14:24:00','2026-08-26 14:24:05',NULL,NULL,NULL),(2,11,4,1,3,'对在哪？',1,'2026-08-26 15:23:59','2026-09-15 15:14:21',1,0,NULL),(3,11,5,2,4,'她说的哪哪都对/柴犬',0,'2026-08-26 17:35:27','2026-08-26 17:35:29',1,NULL,NULL),(4,11,6,1,3,'我也认为很对',1,'2026-08-26 17:38:24','2026-08-26 17:38:25',1,NULL,NULL),(5,11,7,0,NULL,'这是第2条一级评论',1,'2026-08-26 21:11:38','2026-08-26 21:11:43',NULL,NULL,NULL),(6,11,8,0,NULL,'这是第3条一级评论',1,'2026-08-26 21:13:11','2026-08-26 21:13:13',NULL,NULL,NULL),(7,11,2,0,NULL,'11号帖子的一级评论，没有回复任何人，parentId为0，toUserId也为0',1,'2026-08-27 10:08:51',NULL,NULL,NULL,NULL),(8,11,2,7,2,'这是自己回复自己的测试',1,'2026-08-27 10:21:16',NULL,7,NULL,NULL),(9,11,2,0,NULL,'第二次测试新增评论功能，一级评论，父id应为0，toUserId应为null',1,'2026-08-27 10:39:10',NULL,NULL,NULL,NULL),(10,11,2,9,2,'回复第二次测试新增评论功能，子评论，父id应为9，toUserId应为2，rootCommentId应为9',1,'2026-08-27 10:42:53',NULL,9,NULL,NULL),(11,11,4,0,NULL,'测试改了表有没有影响，父评论，父id应为0，toUserId应为null，rootCommentId应为null',1,'2026-08-28 10:50:09',NULL,NULL,NULL,NULL),(12,11,4,11,4,'测试改了表有没有影响，子评论，父id应为11，toUserId应为4，rootCommentId应为11',1,'2026-08-28 10:51:24',NULL,11,NULL,NULL),(13,11,4,11,4,'用来删除',0,'2026-08-28 10:52:26',NULL,11,NULL,NULL),(14,11,11,0,NULL,'我是hhk，终于注册并登录上来了，现在评论id为11的帖子，是一级评论',1,'2026-08-31 16:17:25',NULL,NULL,NULL,NULL),(15,11,12,2,4,'我是庞媛媛，我爱你，你说什么我都支持你',1,'2026-08-31 17:12:40',NULL,2,NULL,NULL),(17,12,12,0,NULL,'看你吗？',1,'2026-09-07 15:57:14',NULL,NULL,NULL,NULL),(18,11,12,0,NULL,'2026/9/16加一条父评论',0,'2026-09-14 15:27:51',NULL,NULL,NULL,NULL),(19,12,12,0,NULL,'接接接',1,'2026-09-15 15:52:37',NULL,NULL,NULL,NULL),(20,13,12,0,NULL,'我也不懂',1,'2026-09-15 16:09:05',NULL,NULL,NULL,NULL),(21,14,12,0,NULL,'我不去',0,'2026-09-16 10:56:30','2026-09-16 11:21:33',NULL,1,NULL);

--
-- Table structure for table `comment_dislike`
--

DROP TABLE IF EXISTS `comment_dislike`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_dislike` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL COMMENT '评论id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `is_cancel` tinyint NOT NULL DEFAULT '0' COMMENT '0有效(已折叠) 1取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_comment_user` (`comment_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论不喜欢(折叠)表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_dislike`
--

INSERT INTO `comment_dislike` (`id`, `comment_id`, `user_id`, `is_cancel`, `create_time`, `update_time`, `is_deleted`) VALUES (1,3,12,0,'2026-09-02 16:03:54','2026-09-02 16:03:54',0),(2,4,12,0,'2026-09-15 16:47:13','2026-09-15 16:47:15',0),(3,14,12,0,'2026-09-16 11:04:25','2026-09-16 11:04:25',0);

--
-- Table structure for table `comment_like`
--

DROP TABLE IF EXISTS `comment_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL COMMENT '评论id',
  `user_id` bigint NOT NULL COMMENT '点赞用户id',
  `is_cancel` tinyint NOT NULL DEFAULT '0' COMMENT '0有效 1取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_comment_user` (`comment_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_like`
--

INSERT INTO `comment_like` (`id`, `comment_id`, `user_id`, `is_cancel`, `create_time`, `update_time`, `is_deleted`) VALUES (1,2,12,1,'2026-09-02 15:52:53','2026-09-15 15:14:21',0),(2,21,12,0,'2026-09-16 11:21:33','2026-09-16 11:21:33',0);

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `content` text NOT NULL,
  `user_id` int NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `view_count` int NOT NULL DEFAULT '0',
  `like_count` int NOT NULL DEFAULT '0',
  `media_type` tinyint DEFAULT NULL COMMENT '内容类型：1纯文字 2图文 3视频 4混合',
  `comment_count` int DEFAULT NULL COMMENT '评论总数，冗余字段',
  `collect_count` int DEFAULT NULL COMMENT '收藏总数，冗余字段',
  `status` tinyint DEFAULT NULL COMMENT '帐号状态：0 正常 1封禁',
  `is_deleted` varchar(255) DEFAULT NULL COMMENT '逻辑删除标记',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `news_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

INSERT INTO `news` (`id`, `title`, `content`, `user_id`, `create_time`, `view_count`, `like_count`, `media_type`, `comment_count`, `collect_count`, `status`, `is_deleted`, `update_time`) VALUES (11,'今天遇到一件开心的事情','这是内容：Content-Type未手动设置，浏览器自动生成multipart/form-data和后端@RequestPart匹配',1,'2025-12-10 10:05:15',0,1,NULL,15,1,NULL,NULL,'2026-09-14 15:31:01'),(12,'找个人一起去看风景','Content-Type未手动设置，浏览器自动生成multipart/form-data和后端@RequestPart匹配。带图片试试',1,'2025-12-10 10:06:46',0,0,NULL,1,NULL,NULL,NULL,'2026-09-15 15:52:37'),(13,'这个到底怎么做啊','插入新闻基本信息和图片',1,'2025-12-14 12:52:23',0,0,NULL,1,NULL,NULL,NULL,'2026-09-15 16:09:05'),(14,'一起看日出日落','地点是阿斯蒂芬',1,'2025-12-16 09:19:33',0,0,NULL,0,NULL,NULL,NULL,'2026-09-16 10:57:23'),(15,'新增新闻','sdadad',1,'2025-12-25 16:05:43',0,0,NULL,NULL,NULL,NULL,NULL,NULL);

--
-- Table structure for table `news_image`
--

DROP TABLE IF EXISTS `news_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news_image` (
  `id` int NOT NULL AUTO_INCREMENT,
  `news_id` int NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `sort_order` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `media_type` tinyint DEFAULT NULL COMMENT '媒体类型：1图片 2视频',
  `cover_url` varchar(255) DEFAULT NULL COMMENT '封面地址（仅视频使用）',
  `width` int DEFAULT NULL COMMENT '宽度（像素）',
  `height` int DEFAULT NULL COMMENT '高度（像素）',
  `duration` int DEFAULT NULL COMMENT '时长（秒，仅视频使用）',
  `is_deleted` varchar(255) DEFAULT NULL COMMENT '逻辑删除标记',
  PRIMARY KEY (`id`),
  KEY `news_id` (`news_id`),
  CONSTRAINT `news_image_ibfk_1` FOREIGN KEY (`news_id`) REFERENCES `news` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_image`
--

INSERT INTO `news_image` (`id`, `news_id`, `image_url`, `sort_order`, `create_time`, `media_type`, `cover_url`, `width`, `height`, `duration`) VALUES (15,13,'http://localhost:8080/uploads/news/1765687943239_5035.jpg',2,'2025-12-14 12:52:23',NULL,NULL,NULL,NULL,NULL),(16,12,'http://localhost:8080/uploads/news/1765847302606_6580.jpg',1,'2025-12-16 09:08:22',NULL,NULL,NULL,NULL,NULL),(21,14,'http://localhost:8080/uploads/news/1765847973909_3755.jpg',1,'2025-12-16 09:19:33',NULL,NULL,NULL,NULL,NULL),(23,15,'http://localhost:8080/uploads/news/1766649943863_6298.jpg',1,'2025-12-25 16:05:43',NULL,NULL,NULL,NULL,NULL),(27,11,'http://localhost:8080/uploads/news/1783853157669_3536.jpg',1,'2026-07-12 18:45:57',NULL,NULL,NULL,NULL,NULL);

--
-- Table structure for table `news_like`
--

DROP TABLE IF EXISTS `news_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `news_id` bigint NOT NULL COMMENT '帖子id',
  `user_id` bigint NOT NULL COMMENT '点赞用户id',
  `is_cancel` tinyint NOT NULL DEFAULT '0' COMMENT '0有效点赞 1取消点赞',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_post_user` (`news_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news_like`
--

INSERT INTO `news_like` (`id`, `news_id`, `user_id`, `is_cancel`, `create_time`, `update_time`, `is_deleted`) VALUES (1,11,12,0,'2026-09-02 15:35:48','2026-09-02 15:42:23',0);

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `report_type` tinyint NOT NULL COMMENT '1帖子 2评论',
  `target_id` bigint NOT NULL COMMENT '被举报帖子/评论id',
  `user_id` bigint NOT NULL COMMENT '举报人id',
  `reason_type` varchar(32) NOT NULL COMMENT '举报分类：色情/广告/侵权等',
  `remark` varchar(200) DEFAULT NULL COMMENT '补充描述',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0待审核 1审核通过(下架) 2驳回',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_type_target` (`report_type`,`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='举报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report`
--

INSERT INTO `report` (`id`, `report_type`, `target_id`, `user_id`, `reason_type`, `remark`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES (1,1,11,12,'色情低俗','该帖子涉及色情低俗内容，请审核员重新审核',2,'2026-09-14 11:31:46','2026-09-16 10:21:13',0),(2,1,14,12,'不实信息','该帖子涉及的内容未经确认，大肆宣传虚假信息，请审核员审核',0,'2026-09-16 10:28:28','2026-09-16 10:28:28',0);

--
-- Table structure for table `sms_code`
--

DROP TABLE IF EXISTS `sms_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_code` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `code` varchar(6) NOT NULL COMMENT '验证码',
  `scene` tinyint NOT NULL DEFAULT '1' COMMENT '场景：1注册 2登录',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0未使用 1已使用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_phone_scene` (`phone`,`scene`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='短信验证码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_code`
--

INSERT INTO `sms_code` (`id`, `phone`, `code`, `scene`, `expire_time`, `status`, `create_time`) VALUES (1,'19237837944','540564',1,'2026-08-31 15:27:51',0,'2026-08-31 15:22:50'),(2,'19173771254','784187',1,'2026-08-31 15:37:49',1,'2026-08-31 15:32:48'),(3,'19173771254','358788',1,'2026-08-31 15:44:58',1,'2026-08-31 15:39:57'),(4,'19237837999','097132',1,'2026-08-31 17:13:44',1,'2026-08-31 17:08:44'),(5,'19237837999','234447',2,'2026-08-31 17:25:25',1,'2026-08-31 17:20:25'),(6,'19237837944','687633',2,'2026-09-01 10:00:50',0,'2026-09-01 09:55:50'),(7,'19237837988','804735',1,'2026-09-01 11:07:21',1,'2026-09-01 11:02:21'),(8,'19237837944','464381',2,'2026-09-02 17:46:26',0,'2026-09-02 17:41:26'),(9,'19237837944','625383',2,'2026-09-16 10:09:25',0,'2026-09-16 10:04:25');

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_account` varchar(16) NOT NULL COMMENT '用户唯一账号，不可修改',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `cover` varchar(255) DEFAULT NULL COMMENT '背景封面URL',
  `bio` varchar(200) DEFAULT NULL COMMENT '个人简介',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `location` varchar(64) DEFAULT NULL COMMENT '所在地',
  `total_like_count` int DEFAULT NULL COMMENT '获赞总数，冗余字段',
  `follow_count` int DEFAULT NULL COMMENT '关注数，冗余字段',
  `fan_count` int DEFAULT NULL COMMENT '粉丝数，冗余字段',
  `status` varchar(255) DEFAULT NULL COMMENT '帐号状态：0 正常 1封禁',
  `is_deleted` varchar(255) DEFAULT NULL COMMENT '逻辑删除标记',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_account` (`user_account`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id`, `username`, `password`, `role`, `create_time`, `user_account`, `phone`, `avatar`, `cover`, `bio`, `birthday`, `location`, `total_like_count`, `follow_count`, `fan_count`, `status`, `is_deleted`, `update_time`) VALUES (1,'张三xxx','$2a$10$WSYcKsFPOg0fMEBUgMa07.LlqPcG4QR1DVfnSsadadBY4mZuAXnTO',1,'2025-09-16 20:00:53','12345678','15177575833',NULL,NULL,NULL,NULL,NULL,1,NULL,NULL,'0',NULL,'2026-09-02 15:42:23'),(2,'李四','$2a$10$9M.VR9Sf.ior74Io5w.A3.UI0D4HMvOmZ4S/nL9pdlESZ0jn49v82',0,'2025-10-16 16:56:00','87654321','13529919830',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL),(3,'麦晓雯','$2a$10$hBQBthw901ecLkok3s8bz.CNJOPxKsaaJJmVOEH2GguPZ.eI3bkxi',1,'2025-09-23 14:54:10','23456789','18201682930',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL),(4,'黄中武','$2a$10$Mh2In/N0BoF3A2ZTFGzMbOdrBRgI0Cq3FlI.cnV2qhfQ93oOtqp8K',1,'2025-12-04 15:46:00','34567891','19237837944',NULL,NULL,NULL,NULL,NULL,NULL,1,1,'0',NULL,'2026-09-15 17:11:08'),(5,'吴林武','$2a$10$d0ZyRYl/fhEMvAwEZ62GCud4abP9H3NaBskYhWr/6hL86poTvQAQ.',1,'2025-12-04 10:09:02','45678912','15678446421',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL),(6,'妲己','$2a$10$xJ71QzX7stm97Oz1jsnKYOBDaT8K0.z5p70Cjxd1AAXO18.5coRmm',1,'2025-12-04 10:09:44','56789123','15935746281',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL),(7,'陈真','$2a$10$isCIKPC6/KKG1B4JEJLrA.x.rBSY6Dx2P6c6m14y4rY.fV0bM0M2O',1,'2025-12-04 10:10:44','67891234','12358945624',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL),(8,'庞士元','$2a$10$NR6Ov0r9dgaezptYCtXIsOUUcdA5e2Ueu/Z9enhQRxvL51wXWjXDm',0,'2025-12-04 14:42:15','78912345','78542656623',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL),(9,'方长','$2a$10$rmOzl00RTKsElAEGz.9jlOX3c4GDTFQ8wAJnL/LedEyTUIEJpi60m',0,'2025-12-04 15:58:45','89123456','45587123655',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL),(10,'测试注册用户','$2a$10$3MteXg2bqZOAOXGrduAzheFYLNdlBQAbkLN6NspCLBN0EowVStgDS',0,'2025-12-10 16:25:50','91234567','65842698532',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL,NULL),(11,'用户r6dq','$2a$10$I.w9UTIcrUhgWcRZ9JTz6uzmz49fHs4D07snHpuySwEkhixJQ74Jm',0,'2026-08-31 15:40:27','59652367239','19173771254',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL,'2026-08-31 15:40:27'),(12,'庞媛媛','$2a$10$vk5dCYLDmaVANL0wxfXRZuirNlDUkjgoWOZ5tTi0G4a/IyXEYHEty',1,'2026-08-31 17:09:40','43964822138','19237837999',NULL,NULL,'世中逢尔，胜过百个泛泛之交',NULL,'广东深圳',NULL,1,1,'0',NULL,'2026-09-16 10:13:37'),(13,'用户ybh4','$2a$10$rJqOyxfme3ePuNpaeAz5BuSgL/mTzJZsU32F/N31LplWr0sR5kzky',0,'2026-09-01 11:06:51','75511201225','19237837988',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'0',NULL,'2026-09-01 11:06:51');

--
-- Table structure for table `user_collect`
--

DROP TABLE IF EXISTS `user_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户id',
  `collect_type` tinyint NOT NULL COMMENT '1帖子 2评论',
  `target_id` bigint NOT NULL COMMENT '帖子/评论id',
  `is_cancel` tinyint NOT NULL DEFAULT '0' COMMENT '0有效收藏 1取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_user_target` (`user_id`,`collect_type`,`target_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_collect`
--

INSERT INTO `user_collect` (`id`, `user_id`, `collect_type`, `target_id`, `is_cancel`, `create_time`, `update_time`, `is_deleted`) VALUES (1,12,1,11,0,'2026-09-14 14:36:02','2026-09-14 14:41:29',0),(2,12,2,2,1,'2026-09-14 14:45:51','2026-09-16 11:41:25',0);

--
-- Table structure for table `user_dislike_post`
--

DROP TABLE IF EXISTS `user_dislike_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_dislike_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '帖子id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `is_cancel` tinyint NOT NULL DEFAULT '0' COMMENT '0有效(不感兴趣) 1取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_post_user` (`post_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子不感兴趣表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_dislike_post`
--

INSERT INTO `user_dislike_post` (`id`, `post_id`, `user_id`, `is_cancel`, `create_time`, `update_time`, `is_deleted`) VALUES (1,15,12,0,'2026-09-16 09:56:08','2026-09-16 09:56:08',0);

--
-- Table structure for table `user_follow`
--

DROP TABLE IF EXISTS `user_follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_follow` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '关注者id',
  `follow_user_id` bigint NOT NULL COMMENT '被关注人id',
  `is_cancel` tinyint NOT NULL DEFAULT '0' COMMENT '0有效关注 1取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_user_follow` (`user_id`,`follow_user_id`),
  KEY `idx_follow_user` (`follow_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户关注表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_follow`
--

INSERT INTO `user_follow` (`id`, `user_id`, `follow_user_id`, `is_cancel`, `create_time`, `update_time`, `is_deleted`) VALUES (1,4,12,0,'2026-09-03 15:49:14','2026-09-03 15:49:42',0),(2,12,4,0,'2026-09-03 15:49:40','2026-09-15 17:11:08',0),(3,4,3,0,'2026-09-15 17:50:46','2026-09-15 17:51:30',0);

--
-- Table structure for table `user_notification`
--

DROP TABLE IF EXISTS `user_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收通知的用户id（内容作者/举报人）',
  `type` tinyint NOT NULL COMMENT '通知类型：1内容被下架 2举报被驳回',
  `content` varchar(500) NOT NULL COMMENT '通知内容',
  `target_type` tinyint DEFAULT NULL COMMENT '关联内容类型：1帖子 2评论',
  `target_id` bigint DEFAULT NULL COMMENT '关联内容id',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '0未读 1已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_user_read` (`user_id`,`is_read`),
  KEY `idx_user_time` (`user_id`,`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='站内通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_notification`
--

INSERT INTO `user_notification` (`id`, `user_id`, `type`, `content`, `target_type`, `target_id`, `is_read`, `create_time`, `update_time`, `is_deleted`) VALUES (1,12,2,'我们还在持续对您举报的对象进行核查分析中，会进一步收集并审核更大范围的数据与用户行为，请关注后续安全邮件通知来获取结果反馈。您的举报将帮助我们持续优化对这类违规行为的检测方案，感谢您的举报，祝您生活愉快【G.T.I.Security】',1,11,1,'2026-09-15 11:39:10','2026-09-15 14:03:00',0),(2,12,2,'我们还在持续对您举报的对象进行核查分析中，会进一步收集并审核更大范围的数据与用户行为，请关注后续安全邮件通知来获取结果反馈。您的举报将帮助我们持续优化对这类违规行为的检测方案，感谢您的举报，祝您生活愉快【G.T.I.Security】',1,11,1,'2026-09-16 10:21:13','2026-09-16 14:26:34',0);
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-16 15:22:55
