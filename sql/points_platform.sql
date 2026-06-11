-- ============================================================
-- 权益积分平台 - 数据库初始化脚本
-- 基于 RuoYi-Vue 3.9.2
-- 使用前请先执行 sql/ry_20260417.sql 与 sql/quartz.sql 初始化系统表
-- 然后再执行本脚本创建积分平台相关业务表
-- ============================================================
-- 修改sql语句utf8 和 utf8_general_ci
SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1、商品分类表
-- ----------------------------
DROP TABLE IF EXISTS `t_goods_category`;
CREATE TABLE `t_goods_category`
(
    `category_id`   bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `parent_id`     bigint(20)    DEFAULT 0 COMMENT '父分类ID',
    `ancestors`     varchar(255)  DEFAULT '' COMMENT '祖级列表',
    `category_name` varchar(64)   NOT NULL COMMENT '分类名称',
    `order_num`     int(4)        DEFAULT 0 COMMENT '显示顺序',
    `icon`          varchar(255)  DEFAULT NULL COMMENT '分类图标',
    `status`        char(1)       DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `del_flag`      char(1)       DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    `create_by`     varchar(64)   DEFAULT '' COMMENT '创建者',
    `create_time`   datetime      DEFAULT NULL COMMENT '创建时间',
    `update_by`     varchar(64)   DEFAULT '' COMMENT '更新者',
    `update_time`   datetime      DEFAULT NULL COMMENT '更新时间',
    `remark`        varchar(500)  DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`category_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 100
  DEFAULT CHARSET = utf8 COMMENT ='商品分类表';

-- INSERT INTO `t_goods_category` (category_id, parent_id, ancestors, category_name, order_num, status, create_by, create_time)
-- VALUES (1, 0, '0', '虚拟商品', 1, '0', 'admin', NOW()),
--        (2, 0, '0', '实物商品', 2, '0', 'admin', NOW()),
--        (3, 1, '0,1', '话费充值', 1, '0', 'admin', NOW()),
--        (4, 1, '0,1', '视频会员', 2, '0', 'admin', NOW()),
--        (5, 2, '0,2', '生活日用', 1, '0', 'admin', NOW()),
--        (6, 2, '0,2', '数码电器', 2, '0', 'admin', NOW());
INSERT INTO `t_goods_category` VALUES (1,0,'0','数码电器',1,NULL,'0','0','admin','2026-05-28 16:19:56','admin','2026-06-01 09:40:02',NULL),(2,0,'0','生活日用',2,NULL,'0','0','admin','2026-05-28 16:19:56','admin','2026-06-01 09:40:14',NULL),(3,1,'0,1','数码设备',1,NULL,'0','0','admin','2026-05-28 16:19:56','admin','2026-06-01 09:40:27',NULL),(4,1,'0,1','家用电器',2,NULL,'0','0','admin','2026-05-28 16:19:56','admin','2026-06-01 09:43:50',NULL),(5,2,'0,2','手机电脑',1,NULL,'0','0','admin','2026-05-28 16:19:56','admin','2026-06-01 09:40:46',NULL),(6,2,'0,2','家居家装',2,NULL,'0','0','admin','2026-05-28 16:19:56','admin','2026-06-01 09:43:27',NULL),(7,0,'0','运营商',0,NULL,'0','0','admin','2026-05-29 09:54:49','',NULL,NULL),(8,7,'0,7','融合套餐',1,NULL,'0','0','admin','2026-05-29 09:55:02','',NULL,NULL),(9,7,'0,7','手机卡',2,NULL,'0','0','admin','2026-05-29 09:55:10','',NULL,NULL),(10,7,'0,7','宽带',3,NULL,'0','0','admin','2026-05-29 09:55:23','',NULL,NULL),(11,7,'0,7','有线电视',4,NULL,'0','0','admin','2026-05-29 09:55:29','admin','2026-05-29 09:55:39',NULL),(12,4,'0,1,4','家用电器',0,'','0','2','admin','2026-06-01 09:40:33','',NULL,NULL);

-- ----------------------------
-- 2、商品表
-- ----------------------------
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods`
(
    `goods_id`        bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `category_id`     bigint(20)    NOT NULL COMMENT '分类ID',
    `goods_name`      varchar(128)  NOT NULL COMMENT '商品名称',
    `goods_type`      char(1)       DEFAULT '0' COMMENT '商品类型（0实物 1虚拟）',
    `dept_id`        int(11)        COMMENT '部门id',
    `parent_id`        int(11)        COMMENT '上级部门id',
    `cover_img`       varchar(500)  DEFAULT NULL COMMENT '封面图',
    `images`          varchar(2000) DEFAULT NULL COMMENT '轮播图，JSON数组',
    `points`          int(11)       NOT NULL DEFAULT 0 COMMENT '兑换所需积分',
    `original_price`  decimal(10, 2) DEFAULT 0 COMMENT '原价（仅展示用）',
    `price`           decimal(10, 2) DEFAULT NULL COMMENT '金额',
    `discount_price`  decimal(10, 2) DEFAULT NULL COMMENT '优惠金额',
    `stock`           int(11)       DEFAULT 0 COMMENT '库存',
    `sales`           int(11)       DEFAULT 0 COMMENT '已售数量',
    `limit_per_user`  int(11)       DEFAULT 0 COMMENT '每人限兑数量(0不限制)',
    `description`     text          COMMENT '商品描述（富文本HTML）',
    `status`          char(1)       DEFAULT '0' COMMENT '上架状态（0下架 1上架）',
    `sort`            int(4)        DEFAULT 0 COMMENT '排序',
    `del_flag`        char(1)       DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    `version`         int(11)       DEFAULT 0 COMMENT '乐观锁版本',
    `create_by`       varchar(64)   DEFAULT '' COMMENT '创建者',
    `create_time`     datetime      DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64)   DEFAULT '' COMMENT '更新者',
    `update_time`     datetime      DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500)  DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`goods_id`),
    KEY `idx_category` (`category_id`),
    KEY `idx_status_sort` (`status`, `sort`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  DEFAULT CHARSET = utf8 COMMENT ='商品表';

-- INSERT INTO `t_goods`(category_id, goods_name, goods_type, cover_img, points, original_price, stock, status, sort, description, create_by, create_time)
-- VALUES (3, '10元话费充值', '1', 'https://via.placeholder.com/300x300?text=Phone', 1000, 10.00, 1000, '1', 100, '<p>10元话费充值，3工作日内到账</p>', 'admin', NOW()),
--        (3, '30元话费充值', '1', 'https://via.placeholder.com/300x300?text=Phone30', 3000, 30.00, 500, '1', 99, '<p>30元话费充值</p>', 'admin', NOW()),
--        (4, '爱奇艺月卡', '1', 'https://via.placeholder.com/300x300?text=iQiYi', 1500, 19.90, 200, '1', 98, '<p>爱奇艺黄金VIP会员月卡</p>', 'admin', NOW()),
--        (5, '精美陶瓷马克杯', '0', 'https://via.placeholder.com/300x300?text=Cup', 800, 39.00, 100, '1', 95, '<p>优质陶瓷工艺马克杯</p>', 'admin', NOW()),
--        (6, '蓝牙耳机', '0', 'https://via.placeholder.com/300x300?text=Earphone', 5000, 199.00, 50, '1', 90, '<p>高品质无线蓝牙耳机</p>', 'admin', NOW());

INSERT INTO `t_goods` VALUES (1000,8,'融合套餐（宽带+电视）480元/年','0',NULL,NULL,'/profile/upload/2026/05/29/融合套餐（300M宽带+电视）_20260529151832A010.png',NULL,480,10.00,999,1,0,'<p><span style=\"color: rgb(51, 51, 51);\">近年来，随着用户对于家庭宽带、有线电视的综合使用需求持续提升，用户对手机流量与家庭宽带的综合使用需求持续提升，高性价比的宽带+电视的融合套餐备受青睐。中国广电推出宽带+电视融合套餐，年费仅480元，整合高速宽带和电视服务，综合优势突出。本文将为大家全面解读该套餐的资费详情、优惠规则、适用人群及办理方式。</span></p><p><img src=\"/dev-api/profile/upload/2026/05/29/融合套餐（300M宽带+电视）_20260529151851A011.png\"></p><h2><strong style=\"color: rgb(51, 51, 51);\">套餐基础信息</strong></h2><ol><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">年费价格</strong><span style=\"color: rgb(51, 51, 51);\">：480 元 / 年</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">宽带服务</strong><span style=\"color: rgb(51, 51, 51);\">：300M 高速宽带</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">有线电视</strong><span style=\"color: rgb(51, 51, 51);\">：电视频道</span></li></ol><p><br></p>','1',100,'0',1,'admin','2026-05-28 16:19:56','admin','2026-06-01 09:00:34',NULL),(1001,8,'融合套餐（宽带+电视）600元/年','0',NULL,NULL,'/profile/upload/2026/05/29/融合套餐（500M宽带+电视）_20260529151825A009.png',NULL,600,30.00,999,0,0,'<p><span style=\"color: rgb(51, 51, 51);\">近年来，随着用户对于家庭宽带、有线电视的综合使用需求持续提升，用户对手机流量与家庭宽带的综合使用需求持续提升，高性价比的宽带+电视的融合套餐备受青睐。中国广电推出宽带+电视融合套餐，年费仅600元，整合高速宽带和电视服务，综合优势突出。本文将为大家全面解读该套餐的资费详情、优惠规则、适用人群及办理方式。</span></p><p><img src=\"/dev-api/profile/upload/2026/05/29/融合套餐（500M宽带+电视）_20260529151813A008.png\"></p><h2><strong style=\"color: rgb(51, 51, 51);\">套餐基础信息</strong></h2><ol><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">年费价格</strong><span style=\"color: rgb(51, 51, 51);\">：600 元 / 年</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">宽带服务</strong><span style=\"color: rgb(51, 51, 51);\">：500M 高速宽带</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">有线电视</strong><span style=\"color: rgb(51, 51, 51);\">：电视频道</span></li></ol>','1',99,'0',0,'admin','2026-05-28 16:19:56','admin','2026-05-29 15:18:26',NULL),(1002,8,'融合套餐（宽带+电视）1080元/年','0',NULL,NULL,'/profile/upload/2026/05/29/融合套餐（1000M宽带+电视）_20260529151736A007.png',NULL,1080,19.90,999,0,0,'<p><span style=\"color: rgb(51, 51, 51);\">近年来，随着用户对于家庭宽带、有线电视的综合使用需求持续提升，用户对手机流量与家庭宽带的综合使用需求持续提升，高性价比的宽带+电视的融合套餐备受青睐。中国广电推出宽带+电视融合套餐，年费仅1080元，整合高速宽带和电视服务，综合优势突出。本文将为大家全面解读该套餐的资费详情、优惠规则、适用人群及办理方式。</span></p><p><img src=\"/dev-api/profile/upload/2026/05/29/融合套餐（1000M宽带+电视）_20260529151727A006.png\"></p><h2><strong style=\"color: rgb(51, 51, 51);\">套餐基础信息</strong></h2><ol><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">年费价格</strong><span style=\"color: rgb(51, 51, 51);\">：1080 元 / 年</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">宽带服务</strong><span style=\"color: rgb(51, 51, 51);\">：1000M 高速宽带</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">有线电视</strong><span style=\"color: rgb(51, 51, 51);\">：电视频道</span></li></ol>','1',98,'0',0,'admin','2026-05-28 16:19:56','admin','2026-05-29 15:17:38',NULL),(1003,6,'东方有线精美陶瓷马克杯','0',NULL,NULL,'/profile/upload/2026/05/29/杯子_20260529151620A005.jpg',NULL,100,39.00,100,0,0,'<p>东方有线优质陶瓷工艺马克杯</p>','1',95,'0',0,'admin','2026-05-28 16:19:56','admin','2026-06-01 11:00:05',NULL),(1004,9,'固网用户专享惠民年卡29元/套餐','0',NULL,NULL,'/profile/upload/2026/05/29/惠民年卡_20260529151144A002.jpg',NULL,299,199.00,999,0,0,'<p><span style=\"color: rgb(51, 51, 51);\">近年来，随着5G普及，越来越多的用户希望以低资费获取高流量。中国广电推出的双百套餐，以每月29元低月租，搭配高达100GB的全国流量，成为近期热门流量套餐。本文将对其资费详情、优惠规则、适用人群及办理方式进行全面解读。</span></p><p><br></p><p><img src=\"/dev-api/profile/upload/2026/05/29/惠民年卡_20260529151151A003.jpg\"></p><p><br></p><h2><strong style=\"color: rgb(51, 51, 51);\">套餐基础信息</strong></h2><ol><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">月租价格</strong><span style=\"color: rgb(51, 51, 51);\">：29元/月</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">国内流量：</strong><span style=\"color: rgb(51, 51, 51);\">每月共100GB（包含基本套餐流量与优惠赠送部分）</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">语音通话</strong><span style=\"color: rgb(51, 51, 51);\">：每月共100分钟</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">标准资费：</strong><span style=\"color: rgb(51, 51, 51);\">流量3元/GB、语音0.15元/分钟、短信0.1元/条</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">适用网络：</strong><span style=\"color: rgb(51, 51, 51);\">中国广电5G网络，全国通用</span></li></ol>','1',90,'0',0,'admin','2026-05-28 16:19:56','admin','2026-05-29 15:14:54',NULL),(1005,9,'双百MAX套餐','0',NULL,NULL,'/profile/upload/2026/05/29/双百MAX卡_20260529100102A001.png',NULL,1000,0.00,999,0,0,'<p><span style=\"color: rgb(51, 51, 51);\">近年来，随着5G普及，越来越多的用户希望以低资费获取高流量。中国广电推出的双百套餐，以每月39元低月租，搭配高达150GB的全国流量，成为近期热门流量套餐。本文将对其资费详情、优惠规则、适用人群及办理方式进行全面解读。</span></p><p><img src=\"/dev-api/profile/upload/2026/05/29/双百MAX卡_20260529151510A004.png\"></p><h2><strong style=\"color: rgb(51, 51, 51);\">套餐基础信息</strong></h2><ol><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">月租价格：</strong><span style=\"color: rgb(51, 51, 51);\">39元/月</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">国内流量：</strong><span style=\"color: rgb(51, 51, 51);\">每月共150GB（包含基本套餐流量与优惠赠送部分）</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">语音通话</strong><span style=\"color: rgb(51, 51, 51);\">：每月共150分钟</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">标准资费：</strong><span style=\"color: rgb(51, 51, 51);\">流量3元/GB、语音0.15元/分钟、短信0.1元/条</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">适用网络：</strong><span style=\"color: rgb(51, 51, 51);\">中国广电5G网络，全国通用</span></li></ol><p><br></p>','1',0,'0',0,'admin','2026-05-29 10:01:04','admin','2026-05-29 15:15:11',NULL),(1006,10,'5G+宽带（49元/月）','0',NULL,NULL,'/profile/upload/2026/05/29/融合套餐（5G+宽带）_20260529152018A012.png',NULL,588,0.00,999,0,0,'<p><span style=\"color: rgb(51, 51, 51);\">近年来，随着 5G 网络全面普及，用户对手机流量与家庭宽带的综合使用需求持续提升，高性价比的手机 + 宽带融合套餐备受青睐。中国广电推出 5G + 宽带融合套餐，月租仅 49 元，整合海量流量、通话时长与高速宽带服务，综合优势突出。本文将为大家全面解读该套餐的资费详情、优惠规则、适用人群及办理方式。</span></p><p><img src=\"/dev-api/profile/upload/2026/05/29/融合套餐（5G+宽带）_20260529152055A013.png\"></p><h2><strong style=\"color: rgb(51, 51, 51);\">套餐基础信息</strong></h2><ol><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">月租价格</strong><span style=\"color: rgb(51, 51, 51);\">：49 元 / 月</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">手机权益</strong><span style=\"color: rgb(51, 51, 51);\">：国内通用流量 100GB，国内语音通话 100 分钟</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">宽带服务</strong><span style=\"color: rgb(51, 51, 51);\">：500M 高速宽带</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">标准资费</strong><span style=\"color: rgb(51, 51, 51);\">：流量 3 元 / GB、语音 0.15 元 / 分钟、短信 0.1 元 / 条</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">适用网络</strong><span style=\"color: rgb(51, 51, 51);\">：中国广电 5G 网络 + 500M 宽带，全国通用</span></li></ol>','1',100,'0',0,'admin','2026-05-29 15:21:01','admin','2026-05-29 15:21:37',NULL),(1007,11,'5G+电视（575元/年）','0',NULL,NULL,'/profile/upload/2026/05/29/融合套餐（5G+电视）_20260529152150A014.png',NULL,575,0.00,998,1,0,'<p><span style=\"color: rgb(51, 51, 51);\">如今 5G 网络广泛普及，手机通信与有线电视成为家庭日常刚需。中国广电推出</span><strong style=\"color: rgb(51, 51, 51);\">5G + 电视融合套餐</strong><span style=\"color: rgb(51, 51, 51);\">，年费仅 575 元，一站式整合手机流量、通话及有线电视服务，性价比十足。本文将全面解读该套餐资费详情、优惠规则、适用人群及办理方式。</span></p><p><img src=\"/dev-api/profile/upload/2026/05/29/融合套餐（5G+电视）_20260529152225A015.png\"></p><h2><strong style=\"color: rgb(51, 51, 51);\">套餐基础信息</strong></h2><ol><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">套餐费用</strong><span style=\"color: rgb(51, 51, 51);\">：575 元 / 年</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">手机权益</strong><span style=\"color: rgb(51, 51, 51);\">：国内通用流量 100GB，国内语音通话 100 分钟</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">电视服务</strong><span style=\"color: rgb(51, 51, 51);\">：有线电视收视服务</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">标准资费</strong><span style=\"color: rgb(51, 51, 51);\">：流量 3 元 / GB、语音 0.15 元 / 分钟、短信 0.1 元 / 条</span></li><li data-list=\"bullet\"><span class=\"ql-ui\" contenteditable=\"false\"></span><strong style=\"color: rgb(51, 51, 51);\">适用网络</strong><span style=\"color: rgb(51, 51, 51);\">：中国广电 5G 网络，服务全国通用</span></li></ol>','1',101,'0',1,'admin','2026-05-29 15:22:27','admin','2026-05-29 15:22:34',NULL),(1008,3,'创维电视55寸','0',NULL,NULL,'/profile/upload/2026/06/01/1_20260601100603A001.jpg',NULL,2500,0.00,1,0,0,'','1',0,'0',0,'admin','2026-06-01 10:06:45','',NULL,NULL);


-- ----------------------------
-- 3、H5用户表
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `user_id`         bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `nickname`        varchar(64)   DEFAULT NULL COMMENT '昵称',
    `name`        varchar(64)   DEFAULT NULL COMMENT '姓名',
    `id_number`        varchar(256)   DEFAULT NULL COMMENT '身份证号（加密存储）',
    `id_number_hash`   char(64)       DEFAULT NULL COMMENT '身份证号Hash（SHA-256，用于检索）',
    `phone`           varchar(256)   NOT NULL COMMENT '手机号（加密存储）',
    `phone_hash`      char(64)       NOT NULL COMMENT '手机号Hash（SHA-256，用于检索）',
    `password`        varchar(100)  DEFAULT NULL COMMENT '密码',
    `avatar`          varchar(500)  DEFAULT NULL COMMENT '头像',
    `points_balance`  int(11)       DEFAULT 0 COMMENT '积分余额',
    `total_earned`    int(11)       DEFAULT 0 COMMENT '累计获得',
    `total_consumed`  int(11)       DEFAULT 0 COMMENT '累计消耗',
    `continuous_days` int(11)       DEFAULT 0 COMMENT '连续签到天数',
    `last_sign_date`  date          DEFAULT NULL COMMENT '最近签到日期',
    `status`          char(1)       DEFAULT '0' COMMENT '状态（0正常 1冻结）',
    `register_time`   datetime      DEFAULT NULL COMMENT '注册时间',
    `last_login_time` datetime      DEFAULT NULL COMMENT '最近登录时间',
    `last_login_ip`   varchar(64)   DEFAULT NULL COMMENT '最近登录IP',
    `del_flag`        char(1)       DEFAULT '0' COMMENT '删除标志',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uniq_phone_hash` (`phone_hash`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8 COMMENT ='H5用户表';


-- INSERT INTO `t_user`(nickname, phone, avatar, points_balance, total_earned, status, register_time)
-- VALUES ('测试用户1', '13800138000', NULL, 10000, 10000, '0', NOW()),
--        ('测试用户2', '13800138001', NULL, 5000, 5000, '0', NOW());

-- ----------------------------
-- 4、收货地址表
-- ----------------------------
DROP TABLE IF EXISTS `t_user_address`;
CREATE TABLE `t_user_address`
(
    `address_id`    bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id`       bigint(20)    NOT NULL COMMENT '用户ID',
    `consignee`     varchar(64)   NOT NULL COMMENT '收件人',
    `phone`         varchar(20)   NOT NULL COMMENT '联系电话',
    `province`      varchar(64)   DEFAULT NULL COMMENT '省',
    `city`          varchar(64)   DEFAULT NULL COMMENT '市',
    `district`      varchar(64)   DEFAULT NULL COMMENT '区/县',
    `detail`        varchar(255)  NOT NULL COMMENT '详细地址',
    `is_default`    char(1)       DEFAULT '0' COMMENT '是否默认（0否 1是）',
    `create_time`   datetime      DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`address_id`),
    KEY `idx_user` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户收货地址表';

-- ----------------------------
-- 5、订单表
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order`
(
    `order_id`        bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`        varchar(64)   NOT NULL COMMENT '订单编号',
    `user_id`         bigint(20)    NOT NULL COMMENT '用户ID',
    `user_phone`      varchar(20)   DEFAULT NULL COMMENT '冗余-用户手机',
    `goods_id`        bigint(20)    NOT NULL COMMENT '商品ID',
    `goods_name`      varchar(128)  DEFAULT NULL COMMENT '冗余-商品名称',
    `goods_cover`     varchar(500)  DEFAULT NULL COMMENT '冗余-商品封面',
    `goods_type`      char(1)       DEFAULT '0' COMMENT '商品类型',
    `quantity`        int(11)       DEFAULT 1 COMMENT '数量',
    `dept_id`        int(11)        COMMENT '部门id',
    `parent_id`        int(11)        COMMENT '上级部门id',
    `points_used`     int(11)       NOT NULL COMMENT '消耗积分',
    `pay_amount`      decimal(10, 2) DEFAULT NULL COMMENT '支付金额',
    `status`          char(1)       DEFAULT '0' COMMENT '订单状态（0待发货 1已发货 2已完成 3已关闭）',
    `consignee`       varchar(64)   DEFAULT NULL COMMENT '收件人',
    `phone`           varchar(20)   DEFAULT NULL COMMENT '联系电话',
    `address`         varchar(500)  DEFAULT NULL COMMENT '收货地址',
    `express_company` varchar(64)   DEFAULT NULL COMMENT '物流公司',
    `express_no`      varchar(64)   DEFAULT NULL COMMENT '物流单号',
    `ship_time`       datetime      DEFAULT NULL COMMENT '发货时间',
    `finish_time`     datetime      DEFAULT NULL COMMENT '完成时间',
    `close_reason`    varchar(255)  DEFAULT NULL COMMENT '关闭原因',
    `close_time`      datetime      DEFAULT NULL COMMENT '关闭时间',
    `create_by`       varchar(64)   DEFAULT '' COMMENT '创建者',
    `create_time`     datetime      DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64)   DEFAULT '' COMMENT '更新者',
    `update_time`     datetime      DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500)  DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`order_id`),
    UNIQUE KEY `uniq_order_no` (`order_no`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_goods` (`goods_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 100000
  DEFAULT CHARSET = utf8 COMMENT ='兑换订单表';

INSERT INTO `ry-vue`.t_order
(order_id, order_no, user_id, user_phone, goods_id, goods_name, goods_cover, goods_type, quantity, dept_id, parent_id, points_used, status, consignee, phone, address, express_company, express_no, ship_time, finish_time, close_reason, close_time, create_by, create_time, update_by, update_time, remark)
VALUES(2, 'PE17800419204125366', 10001, '13800138001', 1007, '5G+电视（575元/年）', '/profile/upload/2026/05/29/融合套餐（5G+电视）_20260529152150A014.png', '0', 1, NULL, NULL, 575, '2', '测试2', '19272111232', '北京市 北京市 东城区 1123', '线下发货', '20260529', '2026-05-29 16:06:23', '2026-05-29 16:09:42', NULL, NULL, '测试用户2', '2026-05-29 16:05:20', 'admin', '2026-05-29 16:09:42', '');

-- ----------------------------
-- 6、积分规则表
-- ----------------------------
DROP TABLE IF EXISTS `t_points_rule`;
CREATE TABLE `t_points_rule`
(
    `rule_id`      bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `rule_code`    varchar(64)   NOT NULL COMMENT '规则编码（如 SIGN_IN/INVITE/SHARE）',
    `rule_name`    varchar(128)  NOT NULL COMMENT '规则名称',
    `rule_type`    char(1)       DEFAULT '0' COMMENT '规则类型（0签到 1任务 2其他）',
    `reward_points` int(11)      DEFAULT 0 COMMENT '奖励积分',
    `daily_limit`  int(11)       DEFAULT 0 COMMENT '每日上限次数(0不限制)',
    `config_json`  text          COMMENT '动态JSON配置',
    `status`       char(1)       DEFAULT '0' COMMENT '状态（0启用 1停用）',
    `create_by`    varchar(64)   DEFAULT '' COMMENT '创建者',
    `create_time`  datetime      DEFAULT NULL COMMENT '创建时间',
    `update_by`    varchar(64)   DEFAULT '' COMMENT '更新者',
    `update_time`  datetime      DEFAULT NULL COMMENT '更新时间',
    `remark`       varchar(500)  DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`rule_id`),
    UNIQUE KEY `uniq_rule_code` (`rule_code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 100
  DEFAULT CHARSET = utf8 COMMENT ='积分规则表';

INSERT INTO `t_points_rule`(rule_code, rule_name, rule_type, reward_points, daily_limit, status, create_by, create_time)
VALUES ('SIGN_IN', '每日签到', '0', 10, 1, '0', 'admin', NOW()),
       ('INVITE_USER', '邀请新用户', '1', 200, 0, '1', 'admin', NOW()),
       ('SHARE', '分享商品', '1', 5, 3, '1', 'admin', NOW());

-- ----------------------------
-- 7、签到配置表
-- ----------------------------
DROP TABLE IF EXISTS `t_sign_config`;
CREATE TABLE `t_sign_config`
(
    `config_id`         bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `enabled`           char(1)       DEFAULT '1' COMMENT '签到开关（0关 1开）',
    `base_points`       int(11)       DEFAULT 10 COMMENT '基础积分',
    `continuous_reward` text          COMMENT '连续签到奖励JSON，如：[{"day":7,"points":50},{"day":15,"points":100},{"day":30,"points":300}]',
    `repair_enabled`    char(1)       DEFAULT '1' COMMENT '是否允许补签（0否 1是）',
    `repair_cost`       int(11)       DEFAULT 50 COMMENT '补签消耗积分',
    `repair_max_days`   int(11)       DEFAULT 3 COMMENT '最多可补签天数',
    `update_by`         varchar(64)   DEFAULT '' COMMENT '更新者',
    `update_time`       datetime      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`config_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='签到全局配置表';

INSERT INTO `t_sign_config`(config_id, enabled, base_points, continuous_reward, repair_enabled, repair_cost, repair_max_days, update_by, update_time)
VALUES (1, '1', 10,
        '[{"day":3,"points":20},{"day":7,"points":50},{"day":15,"points":100},{"day":30,"points":300}]',
        '1', 50, 3, 'admin', NOW());

-- ----------------------------
-- 8、签到记录表
-- ----------------------------
DROP TABLE IF EXISTS `t_sign_record`;
CREATE TABLE `t_sign_record`
(
    `record_id`        bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`          bigint(20)    NOT NULL COMMENT '用户ID',
    `sign_date`        date          NOT NULL COMMENT '签到日期',
    `points`           int(11)       DEFAULT 0 COMMENT '获得积分',
    `continuous_days`  int(11)       DEFAULT 1 COMMENT '当时连续签到天数',
    `is_repair`        char(1)       DEFAULT '0' COMMENT '是否补签（0否 1是）',
    `create_time`      datetime      DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`record_id`),
    UNIQUE KEY `uniq_user_date` (`user_id`, `sign_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='签到记录表';

-- ----------------------------
-- 9、积分明细表
-- ----------------------------
DROP TABLE IF EXISTS `t_points_detail`;
CREATE TABLE `t_points_detail`
(
    `detail_id`    bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `user_id`      bigint(20)    NOT NULL COMMENT '用户ID',
    `change_type`  char(1)       NOT NULL COMMENT '变动类型（0增加 1扣减）',
    `source_type`  varchar(32)   NOT NULL COMMENT '来源类型（SIGN_IN签到 EXCHANGE兑换 REFUND退还 ADMIN_ADJUST后台调整 INVITE邀请等）',
    `source_id`    varchar(64)   DEFAULT NULL COMMENT '来源业务ID(订单号/签到ID等)',
    `change_points` int(11)      NOT NULL COMMENT '变动积分（正数）',
    `balance`      int(11)       NOT NULL COMMENT '变动后余额',
    `description`  varchar(255)  DEFAULT NULL COMMENT '描述',
    `create_by`    varchar(64)   DEFAULT '' COMMENT '操作人',
    `create_time`  datetime      DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`detail_id`),
    KEY `idx_user_time` (`user_id`, `create_time`),
    KEY `idx_source` (`source_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='积分变动明细表';

-- ----------------------------
-- 10、菜单数据（接入 RuoYi 权限体系）
-- ----------------------------
-- 删除可能存在的旧菜单
DELETE FROM sys_menu WHERE menu_id BETWEEN 2000 AND 2099;
DELETE FROM sys_role_menu WHERE menu_id BETWEEN 2000 AND 2099;

-- 一级菜单：积分平台
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2000, '积分平台', 0, 5, 'points', NULL, 1, 0, 'M', '0', '0', '', 'tree-table', 'admin', NOW(), '积分平台目录');

-- 商品管理
-- INSERT INTO sys_menu VALUES (2010, '商品分类', 2000, 1, 'category', 'points/category/index', 1, 0, 'C', '0', '0', 'points:category:list', 'tree', 'admin', NOW(), '', NULL, '商品分类菜单');
INSERT INTO sys_menu VALUES (2010, '商品分类', 2000, 1, 'category', 'points/category/index', '', '', 1, 0, 'C', '0', '0', 'points:category:list', 'tree', 'admin', NOW(), '', NULL, '商品分类菜单');
INSERT INTO sys_menu VALUES (2011, '分类查询', 2010, 1, '', '','', '', 1, 0, 'F', '0', '0', 'points:category:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2012, '分类新增', 2010, 2, '', '','', '', 1, 0, 'F', '0', '0', 'points:category:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2013, '分类修改', 2010, 3, '', '','', '', 1, 0, 'F', '0', '0', 'points:category:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2014, '分类删除', 2010, 4, '', '','', '', 1, 0, 'F', '0', '0', 'points:category:remove', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu VALUES (2020, '商品管理', 2000, 2, 'goods', 'points/goods/index','', '', 1, 0, 'C', '0', '0', 'points:goods:list', 'shopping', 'admin', NOW(), '', NULL, '商品管理菜单');
INSERT INTO sys_menu VALUES (2021, '商品查询', 2020, 1, '', '','', '', 1, 0, 'F', '0', '0', 'points:goods:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2022, '商品新增', 2020, 2, '', '','', '', 1, 0, 'F', '0', '0', 'points:goods:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2023, '商品修改', 2020, 3, '', '', '', '',1, 0, 'F', '0', '0', 'points:goods:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2024, '商品删除', 2020, 4, '', '','', '', 1, 0, 'F', '0', '0', 'points:goods:remove', '#', 'admin', NOW(), '', NULL, '');

-- 订单管理
INSERT INTO sys_menu VALUES (2030, '订单管理', 2000, 3, 'order', 'points/order/index', '', '',1, 0, 'C', '0', '0', 'points:order:list', 'documentation', 'admin', NOW(), '', NULL, '订单管理菜单');
INSERT INTO sys_menu VALUES (2031, '订单查询', 2030, 1, '', '','', '', 1, 0, 'F', '0', '0', 'points:order:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2032, '订单发货', 2030, 2, '', '', '', '',1, 0, 'F', '0', '0', 'points:order:ship', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2033, '订单关闭', 2030, 3, '', '','', '', 1, 0, 'F', '0', '0', 'points:order:close', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2034, '订单导出', 2030, 4, '', '', '', '',1, 0, 'F', '0', '0', 'points:order:export', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2035, '订单修改', 2030, 5, '', '', '', '',1, 0, 'F', '0', '0', 'points:order:edit', '#', 'admin', NOW(), '', NULL, '');

-- 积分管理
INSERT INTO sys_menu VALUES (2040, '积分规则', 2000, 4, 'rule', 'points/rule/index', '', '',1, 0, 'C', '0', '0', 'points:rule:list', 'rate', 'admin', NOW(), '', NULL, '积分规则菜单');
INSERT INTO sys_menu VALUES (2041, '规则查询', 2040, 1, '', '','', '', 1, 0, 'F', '0', '0', 'points:rule:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2042, '规则编辑', 2040, 2, '', '', '', '',1, 0, 'F', '0', '0', 'points:rule:edit', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu VALUES (2050, '签到配置', 2000, 5, 'sign', 'points/sign/index', '', '',1, 0, 'C', '0', '0', 'points:sign:edit', 'date', 'admin', NOW(), '', NULL, '签到配置菜单');
INSERT INTO sys_menu VALUES (2060, '积分明细', 2000, 6, 'detail', 'points/detail/index','', '',1, 0, 'C', '0', '0', 'points:detail:list', 'log', 'admin', NOW(), '', NULL, '积分明细菜单');
INSERT INTO sys_menu VALUES (2061, '明细查询', 2060, 1, '', '', '', '',1, 0, 'F', '0', '0', 'points:detail:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2062, '明细导出', 2060, 2, '', '', '', '',1, 0, 'F', '0', '0', 'points:detail:export', '#', 'admin', NOW(), '', NULL, '');

-- H5用户管理
INSERT INTO sys_menu VALUES (2070, 'H5用户', 2000, 7, 'h5user', 'points/h5user/index', '', '',1, 0, 'C', '0', '0', 'points:h5user:list', 'user', 'admin', NOW(), '', NULL, 'H5用户菜单');
INSERT INTO sys_menu VALUES (2071, '用户查询', 2070, 1, '', '', '', '',1, 0, 'F', '0', '0', 'points:h5user:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2072, '用户冻结', 2070, 2, '', '', '', '',1, 0, 'F', '0', '0', 'points:h5user:freeze', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2073, '积分调整', 2070, 3, '', '', '', '',1, 0, 'F', '0', '0', 'points:h5user:adjust', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2074, '用户新增', 2070, 4, '', '', '', '',1, 0, 'F', '0', '0', 'points:h5user:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2075, '用户修改', 2070, 5, '', '', '', '',1, 0, 'F', '0', '0', 'points:h5user:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2076, '用户删除', 2070, 6, '', '', '', '',1, 0, 'F', '0', '0', 'points:h5user:remove', '#', 'admin', NOW(), '', NULL, '');

-- 给 admin 角色（role_id=1）授权 (RuoYi 默认 admin 拥有所有菜单，无需插入)
-- 给普通角色 role_id=2 也授权，方便测试
INSERT IGNORE INTO sys_role_menu(role_id, menu_id)
SELECT 2, menu_id FROM sys_menu WHERE menu_id BETWEEN 2000 AND 2099;

-- 字典数据：商品类型、订单状态等
INSERT IGNORE INTO sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (200, '商品类型', 'points_goods_type', '0', 'admin', NOW(), '0实物 1虚拟'),
       (201, '订单状态', 'points_order_status', '0', 'admin', NOW(), '订单状态'),
       (202, '积分变动类型', 'points_change_type', '0', 'admin', NOW(), '0增加 1扣减'),
       (203, '积分来源', 'points_source_type', '0', 'admin', NOW(), '积分来源类型');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (1, '实物', '0', 'points_goods_type', '', 'primary', 'N', '0', 'admin', NOW()),
       (2, '虚拟', '1', 'points_goods_type', '', 'success', 'N', '0', 'admin', NOW()),
       (1, '待发货', '0', 'points_order_status', '', 'warning', 'N', '0', 'admin', NOW()),
       (2, '已发货', '1', 'points_order_status', '', 'primary', 'N', '0', 'admin', NOW()),
       (3, '已完成', '2', 'points_order_status', '', 'success', 'N', '0', 'admin', NOW()),
       (4, '已关闭', '3', 'points_order_status', '', 'danger', 'N', '0', 'admin', NOW()),
       (1, '增加', '0', 'points_change_type', '', 'success', 'N', '0', 'admin', NOW()),
       (2, '扣减', '1', 'points_change_type', '', 'danger', 'N', '0', 'admin', NOW()),
       (1, '签到', 'SIGN_IN', 'points_source_type', '', 'success', 'N', '0', 'admin', NOW()),
       (2, '兑换', 'EXCHANGE', 'points_source_type', '', 'danger', 'N', '0', 'admin', NOW()),
       (3, '退还', 'REFUND', 'points_source_type', '', 'info', 'N', '0', 'admin', NOW()),
       (4, '后台调整', 'ADMIN_ADJUST', 'points_source_type', '', 'warning', 'N', '0', 'admin', NOW()),
       (5, '邀请奖励', 'INVITE', 'points_source_type', '', 'primary', 'N', '0', 'admin', NOW()),
       (6, '连续奖励', 'CONTINUOUS_BONUS', 'points_source_type', '', 'success', 'N', '0', 'admin', NOW()),
       (7, '补签', 'SIGN_REPAIR', 'points_source_type', '', 'info', 'N', '0', 'admin', NOW());

-- ----------------------------
-- 11、优惠券表
-- ----------------------------
DROP TABLE IF EXISTS `t_coupon`;
CREATE TABLE `t_coupon` (
    `coupon_id`      bigint(20)      NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
    `dept_id`        bigint(20)      DEFAULT NULL COMMENT '部门ID（用于数据权限）',
    `parent_id`      bigint(20)      DEFAULT 0 COMMENT '父级ID（用于层级管理，无则为0）',
    `coupon_name`    varchar(128)    NOT NULL COMMENT '优惠券名称',
    `coupon_type`    char(1)         DEFAULT '0' COMMENT '类型（0满减券 1折扣券 2无门槛券）',
    `min_amount`     int(11)         DEFAULT 0 COMMENT '使用门槛（满X积分可用，0为无门槛）',
    `discount_value` int(11)         NOT NULL COMMENT '优惠面值（抵扣积分或折扣率）',
    `total_count`    int(11)         DEFAULT -1 COMMENT '发行总量（-1为不限量）',
    `issued_count`   int(11)         DEFAULT 0 COMMENT '已发放数量',
    `limit_per_user` int(11)         DEFAULT 1 COMMENT '每人限领数量',
    `use_type`       char(1)         DEFAULT '0' COMMENT '适用范围（0全部商品 1指定商品分类 2指定商品）',
    `time_type`      char(1)         DEFAULT '0' COMMENT '有效期类型（0绝对时间 1相对天数）',
    `start_time`     datetime        DEFAULT NULL COMMENT '有效期开始时间（绝对时间用）',
    `end_time`       datetime        DEFAULT NULL COMMENT '有效期结束时间（绝对时间用）',
    `valid_days`     int(11)         DEFAULT NULL COMMENT '领取后有效天数（相对天数用）',
    `status`         char(1)         DEFAULT '0' COMMENT '状态（0正常 1停用 2过期）',
    `del_flag`       char(1)         DEFAULT '0' COMMENT '删除标志',
    `create_by`      varchar(64)     DEFAULT '' COMMENT '创建者',
    `create_time`    datetime        DEFAULT NULL COMMENT '创建时间',
    `update_by`      varchar(64)     DEFAULT '' COMMENT '更新者',
    `update_time`    datetime        DEFAULT NULL COMMENT '更新时间',
    `remark`         varchar(500)    DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='优惠券主表';

-- ----------------------------
-- 12、优惠券-商品/分类 关联表
-- ----------------------------
DROP TABLE IF EXISTS `t_coupon_goods`;
CREATE TABLE `t_coupon_goods` (
    `id`             bigint(20)      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `coupon_id`      bigint(20)      NOT NULL COMMENT '优惠券ID',
    `ref_id`         bigint(20)      NOT NULL COMMENT '关联的商品ID或分类ID',
    PRIMARY KEY (`id`),
    KEY `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='优惠券-商品关联表';

-- ----------------------------
-- 13、用户领券记录表
-- ----------------------------
DROP TABLE IF EXISTS `t_user_coupon`;
CREATE TABLE `t_user_coupon` (
    `user_coupon_id` bigint(20)      NOT NULL AUTO_INCREMENT COMMENT '领券记录ID',
    `user_id`        bigint(20)      NOT NULL COMMENT 'H5用户ID',
    `coupon_id`      bigint(20)      NOT NULL COMMENT '优惠券ID',
    `receive_type`   char(1)         DEFAULT '0' COMMENT '获取方式（0主动领取 1后台发放）',
    `status`         char(1)         DEFAULT '0' COMMENT '使用状态（0未使用 1已使用 2已过期）',
    `order_id`       bigint(20)      DEFAULT NULL COMMENT '使用的订单ID',
    `start_time`     datetime        NOT NULL COMMENT '生效时间',
    `end_time`       datetime        NOT NULL COMMENT '过期时间',
    `use_time`       datetime        DEFAULT NULL COMMENT '使用时间',
    `create_time`    datetime        DEFAULT NULL COMMENT '领取时间',
    PRIMARY KEY (`user_coupon_id`),
    KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户优惠券记录表';

-- 优惠券管理菜单
INSERT INTO sys_menu VALUES (2080, '优惠券管理', 2000, 8, 'coupon', 'points/coupon/index', '', '',1, 0, 'C', '0', '0', 'points:coupon:list', 'money', 'admin', NOW(), '', NULL, '优惠券管理菜单');
INSERT INTO sys_menu VALUES (2081, '优惠券查询', 2080, 1, '', '', '', '',1, 0, 'F', '0', '0', 'points:coupon:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2082, '优惠券新增', 2080, 2, '', '', '', '',1, 0, 'F', '0', '0', 'points:coupon:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2083, '优惠券修改', 2080, 3, '', '', '', '',1, 0, 'F', '0', '0', 'points:coupon:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2084, '优惠券删除', 2080, 4, '', '', '', '',1, 0, 'F', '0', '0', 'points:coupon:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2085, '发放优惠券', 2080, 5, '', '', '', '',1, 0, 'F', '0', '0', 'points:coupon:issue', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2086, '发放记录查询', 2080, 6, '', '', '', '',1, 0, 'F', '0', '0', 'points:coupon:record', '#', 'admin', NOW(), '', NULL, '');


INSERT INTO sys_menu VALUES (2090, '优惠券领取记录管理', 2000,9, 'usercoupon', 'points/usercoupon/index', '', '',1, 0, 'C', '0', '0', 'points:usercoupon:list', 'money', 'admin', NOW(), '', NULL, '优惠券用户领取记录管理菜单');
INSERT INTO sys_menu VALUES (2091, '优惠券领取记录查询', 2090, 1, '', '', '', '',1, 0, 'F', '0', '0', 'points:usercoupon:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2092, '优惠券领取记录新增', 2090, 2, '', '', '', '',1, 0, 'F', '0', '0', 'points:usercoupon:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2093, '优惠券领取记录修改', 2090, 3, '', '', '', '',1, 0, 'F', '0', '0', 'points:usercoupon:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2094, '优惠券是否领取', 2090, 4, '', '', '', '',1, 0, 'F', '0', '0', 'points:usercoupon:receive', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2095, '优惠券状态更新', 2090, 5, '', '', '', '',1, 0, 'F', '0', '0', 'points:usercoupon:isstatus', '#', 'admin', NOW(), '', NULL, '');


-- 字典数据：优惠券类型、范围、有效期等
INSERT IGNORE INTO sys_dict_type(dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (204, '优惠券类型', 'points_coupon_type', '0', 'admin', NOW(), '0满减 1折扣 2无门槛'),
       (205, '适用范围', 'points_coupon_use_type', '0', 'admin', NOW(), '0全部 1分类 2指定商品'),
       (206, '有效期类型', 'points_coupon_time_type', '0', 'admin', NOW(), '0绝对时间 1相对天数'),
       (207, '领券方式', 'points_coupon_receive_type', '0', 'admin', NOW(), '0主动领取 1后台发放'),
       (208, '优惠券状态', 'points_user_coupon_status', '0', 'admin', NOW(), '0未使用 1已使用 2已过期');

INSERT INTO sys_dict_data(dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time)
VALUES (1, '满减券', '0', 'points_coupon_type', '', 'primary', 'Y', '0', 'admin', NOW()),
       (2, '折扣券', '1', 'points_coupon_type', '', 'success', 'N', '0', 'admin', NOW()),
       (3, '无门槛券', '2', 'points_coupon_type', '', 'warning', 'N', '0', 'admin', NOW()),
       
       (1, '全部商品', '0', 'points_coupon_use_type', '', 'primary', 'Y', '0', 'admin', NOW()),
       (2, '指定分类', '1', 'points_coupon_use_type', '', 'success', 'N', '0', 'admin', NOW()),
       (3, '指定商品', '2', 'points_coupon_use_type', '', 'warning', 'N', '0', 'admin', NOW()),
       
       (1, '固定时间', '0', 'points_coupon_time_type', '', 'primary', 'Y', '0', 'admin', NOW()),
       (2, '领取后天数', '1', 'points_coupon_time_type', '', 'success', 'N', '0', 'admin', NOW()),
       
       (1, '主动领取', '0', 'points_coupon_receive_type', '', 'primary', 'Y', '0', 'admin', NOW()),
       (2, '后台发放', '1', 'points_coupon_receive_type', '', 'success', 'N', '0', 'admin', NOW()),
       
       (1, '未使用', '0', 'points_user_coupon_status', '', 'success', 'Y', '0', 'admin', NOW()),
       (2, '已使用', '1', 'points_user_coupon_status', '', 'info', 'N', '0', 'admin', NOW()),
       (3, '已过期', '2', 'points_user_coupon_status', '', 'danger', 'N', '0', 'admin', NOW());

-- ----------------------------
-- 新增字段：商品金额、优惠金额、订单支付金额
-- ----------------------------
ALTER TABLE `t_goods` ADD COLUMN `price` decimal(10,2) DEFAULT NULL COMMENT '金额' AFTER `original_price`;
ALTER TABLE `t_goods` ADD COLUMN `discount_price` decimal(10,2) DEFAULT NULL COMMENT '优惠金额' AFTER `price`;
ALTER TABLE `t_order` ADD COLUMN `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额' AFTER `points_used`;

SET FOREIGN_KEY_CHECKS = 1;
