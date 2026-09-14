-- ----------------------------
-- 职位字典类型和数据
-- ----------------------------

-- 职位字典类型
INSERT INTO system_dict_type(name, type, status, remark, creator, create_time, updater, update_time, deleted, deleted_time)
VALUES ('职位', 'hrm_job_post', 0, '员工职位', '1', NOW(), '1', NOW(), false, NULL);

SET @dict_type_id = 'hrm_job_post';

-- 职位字典数据
INSERT INTO system_dict_data(dict_type, label, value, sort, status, color_type, css_class, remark, creator, create_time, updater, update_time, deleted) VALUES
(@dict_type_id, '产品经理', '1', 1, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '设计师', '2', 2, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '财务', '3', 3, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '运营专员', '4', 4, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '前台', '5', 5, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '开发工程师', '6', 6, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '测试工程师', '7', 7, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '人事专员', '8', 8, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '行政专员', '9', 9, 0, 'default', '', '', '1', NOW(), '1', NOW(), false),
(@dict_type_id, '销售专员', '10', 10, 0, 'default', '', '', '1', NOW(), '1', NOW(), false);

