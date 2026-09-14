# 员工离职申请单定时任务配置说明

## 任务说明

**任务名称**：员工离职申请单离职日期处理 Job  
**处理器名称**：`employeeResignationByResignationDateJob`  
**执行频率**：每天 01:00 执行一次  
**Cron 表达式**：`0 0 1 * * ?`  
**功能**：处理已审批通过且到达离职日期的员工离职申请单，更新员工档案状态为离职

## 配置步骤

根据 [XXL-Job 使用文档](https://ruoyioffice.com/job/#_4-%E6%9B%B4%E5%A4%9A%E4%BD%BF%E7%94%A8%E6%95%99%E7%A8%8B)：

### 1. 确保 XXL-Job 调度中心已启动

确保 XXL-Job 调度中心已部署并运行，默认地址：`http://127.0.0.1:9090/xxl-job-admin`

### 2. 确保服务配置正确

在 `application-local.yaml` 中已配置：
```yaml
xxl:
  job:
    enabled: true # 是否开启调度中心
    admin:
      addresses: http://127.0.0.1:9090/xxl-job-admin # 调度中心部署跟地址
```

### 3. 在 XXL-Job 调度中心配置任务

① 登录 XXL-Job 调度中心管理界面

② 在【执行器管理】菜单中，确保已添加 `hrm-server` 执行器：
   - **AppName**：`hrm-server`（对应 `spring.application.name`）
   - **名称**：HRM 服务
   - **注册方式**：自动注册

③ 启动 `hrm-server` 服务后，执行器会自动注册到调度中心

④ 在【任务管理】菜单中，点击【新增】按钮，填写任务信息：
   - **执行器**：选择 `hrm-server`
   - **任务描述**：员工离职申请单离职日期处理 Job
   - **路由策略**：第一个（默认）
   - **Cron**：`0 0 1 * * ?`（每天 01:00 执行）
   - **运行模式**：BEAN
   - **JobHandler**：`employeeResignationByResignationDateJob`
   - **阻塞处理策略**：单机串行（默认）
   - **任务超时时间**：0（不限制）
   - **失败重试次数**：0（不重试）

⑤ 点击【保存】按钮，任务创建完成

### 4. 测试任务

① 在任务列表中，找到刚创建的任务，点击【操作】按钮

② 选择【执行一次】，立即执行一次任务进行测试

③ 查看执行日志，确认任务执行成功

## 任务执行逻辑

1. 查询条件：
   - `process_status = 2`（审批通过）
   - `resignation_date = 当天日期`

2. 对符合条件的每条离职申请单：
   - 调用 `EmployeeResignationBillService.updateEmployeeFromResignationBill()` 方法
   - 更新员工档案的状态为离职（6）
   - 记录成功和失败数量

3. 返回执行结果：
   - 成功返回：`处理完成：成功 X 个，失败 Y 个`
   - 如果没有需要处理的数据，返回：`今天没有需要处理的员工离职申请单`

## 注意事项

- 该任务支持多租户，使用 `@TenantJob` 注解，会自动遍历所有租户执行
- 任务执行失败不会影响其他单据的处理
- 建议在业务低峰期执行（已配置为每天 01:00）
- 如需修改执行时间，在 XXL-Job 调度中心修改 Cron 表达式即可
- 审批通过后不会立即更新员工档案，而是等待到达离职日期后由定时任务统一处理

