-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('扫码统计', '2231', '1', 'scan', 'scan/scan/index', 1, 0, 'C', '0', '0', 'scan:scan:list', '#', 'admin', sysdate(), '', null, '扫码统计菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('扫码统计查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'scan:scan:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('扫码统计新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'scan:scan:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('扫码统计修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'scan:scan:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('扫码统计删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'scan:scan:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('扫码统计导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'scan:scan:export',       '#', 'admin', sysdate(), '', null, '');