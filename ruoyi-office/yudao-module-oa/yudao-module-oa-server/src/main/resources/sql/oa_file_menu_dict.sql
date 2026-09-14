-- ----------------------------
-- 企业云盘菜单和字典配置
-- ----------------------------

-- ----------------------------
-- 企业云盘相关字典类型
-- ----------------------------

-- 文件类型字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('文件类型', 'oa_file_type', 0, '企业云盘的文件类型')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '文件', '0', 'oa_file_type', 0, 'primary', '', '普通文件'),
(2, '文件夹', '1', 'oa_file_type', 0, 'warning', '', '文件夹')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- 文件分享类型字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('文件分享类型', 'oa_file_share_type', 0, '文件夹的分享类型')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '人员', '0', 'oa_file_share_type', 0, 'primary', '', '分享给指定人员'),
(2, '组织', '1', 'oa_file_share_type', 0, 'success', '', '分享给组织/部门')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- 文件分享权限字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('文件分享权限', 'oa_file_permission', 0, '文件夹的分享权限')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '仅查看', '0', 'oa_file_permission', 0, 'info', '', '只能查看文件'),
(2, '可管理', '1', 'oa_file_permission', 0, 'success', '', '可以管理文件')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- 文件分类字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('文件分类', 'oa_file_category', 0, '企业云盘的文件分类')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, '全部', 'all', 'oa_file_category', 0, 'default', '', '全部文件'),
(2, '图片', 'image', 'oa_file_category', 0, 'success', '', '图片文件'),
(3, '文档', 'document', 'oa_file_category', 0, 'primary', '', '文档文件'),
(4, '视频', 'video', 'oa_file_category', 0, 'warning', '', '视频文件'),
(5, '音频', 'audio', 'oa_file_category', 0, 'info', '', '音频文件'),
(6, '压缩包', 'archive', 'oa_file_category', 0, 'danger', '', '压缩包文件'),
(7, '其他', 'other', 'oa_file_category', 0, 'default', '', '其他文件')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- 文件分类与文件后缀映射字典
INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`) 
VALUES ('文件分类后缀映射', 'oa_file_category_suffix', 0, '文件分类与文件后缀的映射关系，value格式：分类值，label格式：后缀1,后缀2,后缀3')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`) VALUES
(1, 'bmp,gif,jpeg,jpg,png,svg,webp,ico,heic,heif,raw,psd,ai,eps', 'image', 'oa_file_category_suffix', 0, 'default', '', '图片文件后缀'),
(2, 'doc,docx,pdf,txt,rtf,odt,ppt,pptx,xls,xlsx,csv,md,html,htm,xml,json,yaml,yml,log', 'document', 'oa_file_category_suffix', 0, 'default', '', '文档文件后缀'),
(3, 'mp4,avi,mkv,mov,wmv,flv,webm,m4v,3gp,rm,rmvb,mpg,mpeg,ts,m2ts', 'video', 'oa_file_category_suffix', 0, 'default', '', '视频文件后缀'),
(4, 'mp3,wav,flac,aac,ogg,wma,m4a,ape,amr,mid,midi', 'audio', 'oa_file_category_suffix', 0, 'default', '', '音频文件后缀'),
(5, 'zip,rar,7z,tar,gz,bz2,xz,iso,dmg,cab,arj,lzh', 'archive', 'oa_file_category_suffix', 0, 'default', '', '压缩包文件后缀')
ON DUPLICATE KEY UPDATE `label` = VALUES(`label`), `value` = VALUES(`value`);

-- ----------------------------
-- 企业云盘菜单权限配置
-- ----------------------------

-- 获取或创建OA协同办公父菜单
SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = 'OA协同办公' LIMIT 1);

-- 如果没有OA协同办公菜单，则创建
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
SELECT 'OA协同办公', '', 1, 20, 0, '/oa', 'ep:briefcase', NULL, NULL, 0, 1, 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = 'OA协同办公');

-- 重新获取OA协同办公菜单ID
SET @parent_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = 'OA协同办公' LIMIT 1);

-- 企业云盘菜单
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`)
VALUES ('企业云盘', '', 2, 50, @parent_menu_id, 'file', 'ep:folder', 'oa/file/index', 'OaFileManagement', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 获取企业云盘菜单ID
SET @file_menu_id = (SELECT `id` FROM `system_menu` WHERE `name` = '企业云盘' AND `parent_id` = @parent_menu_id LIMIT 1);

-- 企业云盘按钮权限
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `status`, `visible`, `keep_alive`, `always_show`) VALUES
('查询文件', 'oa:file:query', 3, 1, @file_menu_id, '', '', '', 0, 1, 1, 1),
('创建文件夹', 'oa:file:create', 3, 2, @file_menu_id, '', '', '', 0, 1, 1, 1),
('上传文件', 'oa:file:upload', 3, 3, @file_menu_id, '', '', '', 0, 1, 1, 1),
('更新文件', 'oa:file:update', 3, 4, @file_menu_id, '', '', '', 0, 1, 1, 1),
('删除文件', 'oa:file:delete', 3, 5, @file_menu_id, '', '', '', 0, 1, 1, 1),
('导出文件列表', 'oa:file:export', 3, 6, @file_menu_id, '', '', '', 0, 1, 1, 1),
('收藏文件', 'oa:file:favorite', 3, 7, @file_menu_id, '', '', '', 0, 1, 1, 1)
ON DUPLICATE KEY UPDATE `permission` = VALUES(`permission`);

COMMIT;

