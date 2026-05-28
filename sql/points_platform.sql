-- ============================================================
-- 权益积分平台 - 数据库初始化脚本
-- 基于 RuoYi-Vue 3.9.2
-- 使用前请先执行 sql/ry_20260417.sql 与 sql/quartz.sql 初始化系统表
-- 然后再执行本脚本创建积分平台相关业务表
-- ============================================================

SET NAMES utf8mb4;
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
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品分类表';

INSERT INTO `t_goods_category` (category_id, parent_id, ancestors, category_name, order_num, status, create_by, create_time)
VALUES (1, 0, '0', '虚拟商品', 1, '0', 'admin', NOW()),
       (2, 0, '0', '实物商品', 2, '0', 'admin', NOW()),
       (3, 1, '0,1', '话费充值', 1, '0', 'admin', NOW()),
       (4, 1, '0,1', '视频会员', 2, '0', 'admin', NOW()),
       (5, 2, '0,2', '生活日用', 1, '0', 'admin', NOW()),
       (6, 2, '0,2', '数码电器', 2, '0', 'admin', NOW());

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
    `cover_img`       varchar(500)  DEFAULT NULL COMMENT '封面图',
    `images`          varchar(2000) DEFAULT NULL COMMENT '轮播图，JSON数组',
    `points`          int(11)       NOT NULL DEFAULT 0 COMMENT '兑换所需积分',
    `original_price`  decimal(10, 2) DEFAULT 0 COMMENT '原价（仅展示用）',
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
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品表';

INSERT INTO `t_goods`(category_id, goods_name, goods_type, cover_img, points, original_price, stock, status, sort, description, create_by, create_time)
VALUES (3, '10元话费充值', '1', 'https://via.placeholder.com/300x300?text=Phone', 1000, 10.00, 1000, '1', 100, '<p>10元话费充值，3工作日内到账</p>', 'admin', NOW()),
       (3, '30元话费充值', '1', 'https://via.placeholder.com/300x300?text=Phone30', 3000, 30.00, 500, '1', 99, '<p>30元话费充值</p>', 'admin', NOW()),
       (4, '爱奇艺月卡', '1', 'https://via.placeholder.com/300x300?text=iQiYi', 1500, 19.90, 200, '1', 98, '<p>爱奇艺黄金VIP会员月卡</p>', 'admin', NOW()),
       (5, '精美陶瓷马克杯', '0', 'https://via.placeholder.com/300x300?text=Cup', 800, 39.00, 100, '1', 95, '<p>优质陶瓷工艺马克杯</p>', 'admin', NOW()),
       (6, '蓝牙耳机', '0', 'https://via.placeholder.com/300x300?text=Earphone', 5000, 199.00, 50, '1', 90, '<p>高品质无线蓝牙耳机</p>', 'admin', NOW());

-- ----------------------------
-- 3、H5用户表
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `user_id`         bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `nickname`        varchar(64)   DEFAULT NULL COMMENT '昵称',
    `phone`           varchar(20)   NOT NULL COMMENT '手机号',
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
    UNIQUE KEY `uniq_phone` (`phone`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8mb4 COMMENT ='H5用户表';

INSERT INTO `t_user`(nickname, phone, avatar, points_balance, total_earned, status, register_time)
VALUES ('测试用户1', '13800138000', NULL, 10000, 10000, '0', NOW()),
       ('测试用户2', '13800138001', NULL, 5000, 5000, '0', NOW());

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
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户收货地址表';

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
    `points_used`     int(11)       NOT NULL COMMENT '消耗积分',
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
  DEFAULT CHARSET = utf8mb4 COMMENT ='兑换订单表';

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
  DEFAULT CHARSET = utf8mb4 COMMENT ='积分规则表';

INSERT INTO `t_points_rule`(rule_code, rule_name, rule_type, reward_points, daily_limit, status, create_by, create_time)
VALUES ('SIGN_IN', '每日签到', '0', 10, 1, '0', 'admin', NOW()),
       ('INVITE_USER', '邀请新用户', '1', 200, 0, '0', 'admin', NOW()),
       ('SHARE', '分享商品', '1', 5, 3, '0', 'admin', NOW());

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
  DEFAULT CHARSET = utf8mb4 COMMENT ='签到全局配置表';

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
  DEFAULT CHARSET = utf8mb4 COMMENT ='签到记录表';

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
  DEFAULT CHARSET = utf8mb4 COMMENT ='积分变动明细表';

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
INSERT INTO sys_menu VALUES (2010, '商品分类', 2000, 1, 'category', 'points/category/index', 1, 0, 'C', '0', '0', 'points:category:list', 'tree', 'admin', NOW(), '', NULL, '商品分类菜单');
INSERT INTO sys_menu VALUES (2011, '分类查询', 2010, 1, '', '', 1, 0, 'F', '0', '0', 'points:category:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2012, '分类新增', 2010, 2, '', '', 1, 0, 'F', '0', '0', 'points:category:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2013, '分类修改', 2010, 3, '', '', 1, 0, 'F', '0', '0', 'points:category:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2014, '分类删除', 2010, 4, '', '', 1, 0, 'F', '0', '0', 'points:category:remove', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu VALUES (2020, '商品管理', 2000, 2, 'goods', 'points/goods/index', 1, 0, 'C', '0', '0', 'points:goods:list', 'shopping', 'admin', NOW(), '', NULL, '商品管理菜单');
INSERT INTO sys_menu VALUES (2021, '商品查询', 2020, 1, '', '', 1, 0, 'F', '0', '0', 'points:goods:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2022, '商品新增', 2020, 2, '', '', 1, 0, 'F', '0', '0', 'points:goods:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2023, '商品修改', 2020, 3, '', '', 1, 0, 'F', '0', '0', 'points:goods:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2024, '商品删除', 2020, 4, '', '', 1, 0, 'F', '0', '0', 'points:goods:remove', '#', 'admin', NOW(), '', NULL, '');

-- 订单管理
INSERT INTO sys_menu VALUES (2030, '订单管理', 2000, 3, 'order', 'points/order/index', 1, 0, 'C', '0', '0', 'points:order:list', 'documentation', 'admin', NOW(), '', NULL, '订单管理菜单');
INSERT INTO sys_menu VALUES (2031, '订单查询', 2030, 1, '', '', 1, 0, 'F', '0', '0', 'points:order:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2032, '订单发货', 2030, 2, '', '', 1, 0, 'F', '0', '0', 'points:order:ship', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2033, '订单关闭', 2030, 3, '', '', 1, 0, 'F', '0', '0', 'points:order:close', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2034, '订单导出', 2030, 4, '', '', 1, 0, 'F', '0', '0', 'points:order:export', '#', 'admin', NOW(), '', NULL, '');

-- 积分管理
INSERT INTO sys_menu VALUES (2040, '积分规则', 2000, 4, 'rule', 'points/rule/index', 1, 0, 'C', '0', '0', 'points:rule:list', 'rate', 'admin', NOW(), '', NULL, '积分规则菜单');
INSERT INTO sys_menu VALUES (2041, '规则查询', 2040, 1, '', '', 1, 0, 'F', '0', '0', 'points:rule:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2042, '规则编辑', 2040, 2, '', '', 1, 0, 'F', '0', '0', 'points:rule:edit', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu VALUES (2050, '签到配置', 2000, 5, 'sign', 'points/sign/index', 1, 0, 'C', '0', '0', 'points:sign:edit', 'date', 'admin', NOW(), '', NULL, '签到配置菜单');
INSERT INTO sys_menu VALUES (2060, '积分明细', 2000, 6, 'detail', 'points/detail/index', 1, 0, 'C', '0', '0', 'points:detail:list', 'log', 'admin', NOW(), '', NULL, '积分明细菜单');
INSERT INTO sys_menu VALUES (2061, '明细查询', 2060, 1, '', '', 1, 0, 'F', '0', '0', 'points:detail:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2062, '明细导出', 2060, 2, '', '', 1, 0, 'F', '0', '0', 'points:detail:export', '#', 'admin', NOW(), '', NULL, '');

-- H5用户管理
INSERT INTO sys_menu VALUES (2070, 'H5用户', 2000, 7, 'h5user', 'points/h5user/index', 1, 0, 'C', '0', '0', 'points:h5user:list', 'user', 'admin', NOW(), '', NULL, 'H5用户菜单');
INSERT INTO sys_menu VALUES (2071, '用户查询', 2070, 1, '', '', 1, 0, 'F', '0', '0', 'points:h5user:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2072, '用户冻结', 2070, 2, '', '', 1, 0, 'F', '0', '0', 'points:h5user:freeze', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (2073, '积分调整', 2070, 3, '', '', 1, 0, 'F', '0', '0', 'points:h5user:adjust', '#', 'admin', NOW(), '', NULL, '');

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

SET FOREIGN_KEY_CHECKS = 1;
