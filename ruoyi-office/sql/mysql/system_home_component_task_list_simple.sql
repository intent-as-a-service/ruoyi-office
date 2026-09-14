-- 任务列表组件初始化SQL（精简版）
-- 执行前请根据实际情况调整 id 和 category_id

INSERT INTO `ruoyi-office`.system_home_component
(id, category_id, name, code, component_path, description, preview_image, default_width, default_height, config_schema, status, sort, creator, create_time, updater, update_time, deleted, tenant_id)
VALUES(
  10, 
  2, 
  '任务列表', 
  'workbench_task_list', 
  'dashboard/home/components/taskLists/workbench-task-list.vue', 
  '展示我的单据、待办任务、已办任务、抄送我的四个Tab页签', 
  NULL, 
  24, 
  8, 
  '{"properties": [{"key": "paddingTop", "type": "number", "label": "内边距-上(px)", "default": 16, "min": 0, "max": 100, "required": false}, {"key": "paddingRight", "type": "number", "label": "内边距-右(px)", "default": 16, "min": 0, "max": 100, "required": false}, {"key": "paddingBottom", "type": "number", "label": "内边距-下(px)", "default": 16, "min": 0, "max": 100, "required": false}, {"key": "paddingLeft", "type": "number", "label": "内边距-左(px)", "default": 16, "min": 0, "max": 100, "required": false}, {"key": "marginTop", "type": "number", "label": "外边距-上(px)", "default": 0, "min": 0, "max": 100, "required": false}, {"key": "marginRight", "type": "number", "label": "外边距-右(px)", "default": 0, "min": 0, "max": 100, "required": false}, {"key": "marginBottom", "type": "number", "label": "外边距-下(px)", "default": 0, "min": 0, "max": 100, "required": false}, {"key": "marginLeft", "type": "number", "label": "外边距-左(px)", "default": 0, "min": 0, "max": 100, "required": false}, {"key": "maxRecordNum", "type": "number", "label": "显示任务最大值", "default": 10, "min": 5, "max": 50, "required": false}]}', 
  0, 
  10, 
  '1', 
  NOW(), 
  '', 
  NOW(), 
  0, 
  1
);

