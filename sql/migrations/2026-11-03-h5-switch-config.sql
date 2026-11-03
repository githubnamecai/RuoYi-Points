-- ----------------------------
-- H5 端登录 / 注册开关（系统管理 - 参数设置）
-- 说明：参数值仅 true（忽略大小写与首尾空格）为开启，其余任何取值均视为关闭
-- ----------------------------

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
SELECT 'H5端-登录开关', 'h5.login.enabled', 'true', 'Y', 'admin', NOW(), '', NULL,
       'H5 登录开关（true 开启，其他值关闭；关闭后 H5 登录接口直接返回“系统关闭登录”）'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'h5.login.enabled');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
SELECT 'H5端-注册开关', 'h5.register.enabled', 'true', 'Y', 'admin', NOW(), '', NULL,
       'H5 注册开关（true 开启，其他值关闭；关闭后隐藏 H5 注册入口且注册接口不可用）'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'h5.register.enabled');
