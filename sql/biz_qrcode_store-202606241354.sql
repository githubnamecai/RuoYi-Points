-- MySQL dump 10.13  Distrib 5.5.62, for Win64 (AMD64)
--
-- Host: 127.0.0.1    Database: quanyi_system
-- ------------------------------------------------------
-- Server version	5.7.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `biz_qrcode_store`
--

DROP TABLE IF EXISTS `biz_qrcode_store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `biz_qrcode_store` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `address` varchar(255) DEFAULT '' COMMENT '地址',
  `phone` varchar(20) DEFAULT '' COMMENT '联系电话',
  `purpose` varchar(200) DEFAULT '' COMMENT '用途',
  `qrcode_url` varchar(500) DEFAULT '' COMMENT '二维码访问链接',
  `qr_photo` varchar(500) DEFAULT '' COMMENT '二维码照片（图片oss地址）',
  `contact_person` varchar(50) DEFAULT '' COMMENT '对接人',
  `scan_count` bigint(20) DEFAULT '0' COMMENT '扫码次数',
  `sort_num` int(11) DEFAULT '0' COMMENT '排序号，数字越小越靠前',
  `ext_info` varchar(1000) DEFAULT '' COMMENT '备用扩展字段，存储额外文本/json',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  PRIMARY KEY (`id`),
  KEY `idx_del_flag` (`del_flag`),
  KEY `idx_sort` (`sort_num`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='二维码点位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biz_qrcode_store`
--

LOCK TABLES `biz_qrcode_store` WRITE;
/*!40000 ALTER TABLE `biz_qrcode_store` DISABLE KEYS */;
INSERT INTO `biz_qrcode_store` VALUES (1,'权益平台二维码','金山东方有线','','','https://gift.jsocn.com/h5/#/home','','',0,0,'','admin','2026-06-24 12:55:41','admin','2026-06-24 13:21:30','','0');
/*!40000 ALTER TABLE `biz_qrcode_store` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-24 13:54:00
