DROP TABLE IF EXISTS `biz_qrcode_store`;
CREATE TABLE `biz_qrcode_store` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `address` varchar(255) DEFAULT '' COMMENT '地址',
  `phone` varchar(20) DEFAULT '' COMMENT '联系电话',
  `purpose` varchar(200) DEFAULT '' COMMENT '用途',
  `qrcode_url` varchar(500) DEFAULT '' COMMENT '二维码访问链接',
  `qr_photo` varchar(500) DEFAULT '' COMMENT '二维码照片（图片oss地址）',
  `contact_person` varchar(50) DEFAULT '' COMMENT '对接人',
  `scan_count` bigint DEFAULT 0 COMMENT '扫码次数',
  `sort_num` int DEFAULT 0 COMMENT '排序号，数字越小越靠前',
  `ext_info` varchar(1000) DEFAULT '' COMMENT '备用扩展字段，存储额外文本/json',
  -- 若依框架标准通用审计字段
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  PRIMARY KEY (`id`),
  KEY `idx_del_flag` (`del_flag`),
  KEY `idx_sort` (`sort_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='二维码点位信息表';