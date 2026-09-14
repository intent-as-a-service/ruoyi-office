# 会议室预定功能部署说明（重构版）

## 一、功能概述

会议室预定申请单功能，采用list/info分离的页面结构，完全参照印章申请单的代码规范实现。支持会议室的预定申请、审批流程、时间冲突检测等功能。

## 二、代码结构

### 后端结构（参照印章申请单）

```
ruoyi-office/yudao-module-oa/yudao-module-oa-server/
├── controller/admin/meetingroom/
│   ├── MeetingRoomBookingController.java       # 预定控制器
│   └── vo/
│       ├── MeetingRoomBookingSaveReqVO.java    # 保存/提交VO
│       ├── MeetingRoomBookingPageReqVO.java    # 分页查询VO
│       └── MeetingRoomBookingRespVO.java       # 响应VO
├── service/meetingroom/
│   ├── MeetingRoomBookingService.java          # 预定服务接口
│   └── MeetingRoomBookingServiceImpl.java      # 预定服务实现
├── dal/
│   ├── dataobject/meetingroom/
│   │   └── MeetingRoomBookingDO.java           # 预定DO
│   └── mysql/meetingroom/
│       └── MeetingRoomBookingMapper.java       # 预定Mapper
└── resources/sql/
    └── meeting_room_booking.sql                # SQL脚本
```

### 前端结构（参照印章申请单）

```
ruoyi-office-vben/apps/web-antd/src/
├── api/oa/meetingroom/booking/
│   └── index.ts                                # API接口
└── views/oa/meetingroom/booking/
    ├── list/                                   # 列表页面
    │   ├── index.vue                           # 列表主页面
    │   └── data.ts                             # 列表配置
    └── info/                                   # 详情/表单页面
        ├── index.vue                           # 表单主页面
        └── data.ts                             # 表单配置
```

## 三、关键特性

### 1. 页面结构（与印章申请单一致）

#### List页面 (`list/index.vue`)
- 使用 `useVbenVxeGrid` 构建列表
- 单据编号列使用 `createRouterLinkColumn` 创建可点击链接
- 点击单据编号跳转到 info 页面
- 新建按钮通过 `router.push` 跳转到 info 页面
- 使用 `onActivated` 钩子，页签切换时自动刷新
- 权限控制删除操作

#### Info页面 (`info/index.vue`)
- 使用 `BasicForm` 组件（不是modal弹窗）
- 完整的表单页面，包含表头信息
- 支持保存草稿和提交审批两种操作
- 集成 `AttachmentList` 组件
- 使用 `CardContainer` 组件作为容器
- 支持只读模式和审批模式
- 暴露 `beforeApproval`、`loadData`、`handleSaveAndSubmit` 方法供父组件调用

### 2. 路由配置

```javascript
// 列表页面路由
{
  path: '/oa/meetingroom/booking',
  component: () => import('#/views/oa/meetingroom/booking/list/index.vue'),
  name: 'OaMeetingRoomBookingList',
  meta: {
    title: '会议室预定',
    icon: 'ep:calendar',
  },
}

// 详情页面路由
{
  path: '/oa/meetingroom/booking-info',
  component: () => import('#/views/oa/meetingroom/booking/info/index.vue'),
  name: 'OaMeetingRoomBookingInfo',
  meta: {
    title: '会议室预定详情',
    hideInMenu: true,
    showInTabs: true,
  },
}
```

### 3. 数据库字典

需要配置以下字典类型：

| 字典类型 | 字典代码 | 说明 |
|---------|---------|------|
| 单据状态 | BPM_PROCESS_INSTANCE_STATUS | 通用单据状态 |
| 使用状态 | oa_meeting_booking_use_status | 会议室使用状态 |
| 会议提醒 | oa_meeting_reminder_type | 会议提醒类型 |
| 会议室类型 | oa_meeting_room_type | 会议室类型 |

### 4. 时间控制

- **前端限制**：DatePicker 的 `minuteStep: 30`，只能选择整点或半点
- **后端校验**：
  - 开始时间必须早于结束时间
  - 开始时间不能是过去时间
  - 检测会议室时间冲突（基于使用状态）

### 5. 审批流程集成

- 流程定义Key：`oa_meeting_room_booking`
- 支持 `BpmProcessInstanceStatusEditValue` 权限控制
- 草稿状态可编辑、可删除
- 审批中/已通过状态只读，可取消
- 实现 `FlowBillService` 接口

## 四、部署步骤

### 1. 执行SQL脚本

```bash
mysql -u root -p database_name < meeting_room_booking.sql
```

SQL脚本包含：
- 数据库表结构
- 字典类型和字典数据
- 菜单权限配置

### 2. 配置前端路由

在路由配置文件中添加list和info两个路由（参见上述路由配置）。

### 3. 配置BPM审批流程

在BPM模块中配置流程定义，流程Key为：`oa_meeting_room_booking`

### 4. 配置会议室下拉列表

后端已添加 `/oa/meeting-room/simple-list` 接口，返回允许预定的会议室列表。

## 五、与印章申请单的对照关系

| 印章申请单 | 会议室预定 | 说明 |
|-----------|-----------|------|
| `/oa/seal/seal-apply` | `/oa/meetingroom/booking` | 列表路由 |
| `/oa/seal/seal-apply-info` | `/oa/meetingroom/booking-info` | 详情路由 |
| `sealapply/list/` | `booking/list/` | 列表目录 |
| `sealapply/info/` | `booking/info/` | 详情目录 |
| `SealSelectModal` | - | 会议室预定无特殊选择器 |
| `SealApplyBillService` | `MeetingRoomBookingService` | 服务接口 |
| `BasicForm` | `BasicForm` | 表单组件一致 |
| `AttachmentList` | `AttachmentList` | 附件组件一致 |
| `CardContainer` | `CardContainer` | 容器组件一致 |

## 六、API接口说明

### 后端接口

| 接口路径 | 方法 | 说明 |
|---------|------|------|
| `/oa/meeting-room-booking/save` | POST | 保存草稿 |
| `/oa/meeting-room-booking/submit` | POST | 提交审批 |
| `/oa/meeting-room-booking/get` | GET | 获取详情 |
| `/oa/meeting-room-booking/page` | GET | 分页查询 |
| `/oa/meeting-room-booking/delete` | DELETE | 删除单据 |
| `/oa/meeting-room-booking/export-excel` | GET | 导出Excel |
| `/oa/meeting-room-booking/cancel` | PUT | 取消预定 |
| `/oa/meeting-room/simple-list` | GET | 会议室下拉列表 |

### 前端API

```typescript
// 保存草稿
saveMeetingRoomBooking(data)

// 提交审批
submitMeetingRoomBooking(data)

// 获取详情
getMeetingRoomBooking(id)

// 分页查询
getMeetingRoomBookingPage(params)

// 删除
deleteMeetingRoomBooking(id)

// 导出
exportMeetingRoomBookingExcel(params)

// 取消预定
cancelMeetingRoomBooking(id)

// 会议室列表
getMeetingRoomSelectList()

// 用户列表
getUserSelectList()
```

## 七、测试清单

### 功能测试
- [ ] 列表页面显示正常
- [ ] 点击新建跳转到info页面
- [ ] 点击单据编号跳转到info页面
- [ ] 创建预定申请单（草稿）
- [ ] 提交预定申请单（发起审批）
- [ ] 编辑草稿状态的申请单
- [ ] 删除草稿状态的申请单
- [ ] 查看申请单详情（只读模式）
- [ ] 分页查询和搜索
- [ ] 导出Excel
- [ ] 附件上传和下载

### 时间冲突测试
- [ ] 预定完全重叠的时间段
- [ ] 预定部分重叠的时间段
- [ ] 编辑时排除当前记录

### 审批流程测试
- [ ] 提交审批
- [ ] 审批通过
- [ ] 审批拒绝
- [ ] 撤回审批

### 权限测试
- [ ] 草稿状态：可编辑、可删除
- [ ] 审批中：只读、可撤回
- [ ] 已通过：只读
- [ ] 已拒绝：只读

## 八、注意事项

1. **前后端字段映射**：
   - 时间字段使用时间戳格式 (`valueFormat: 'x'`)
   - 参会人员ID和姓名分别存储为数组
   - 附件URL使用数组格式

2. **状态管理**：
   - `processStatus`：单据状态（审批流程状态）
   - `useStatus`：使用状态（会议室实际使用状态）

3. **依赖接口**：
   - 确保 `/system/user/simple-list` 接口可用
   - 确保 `/oa/meeting-room/simple-list` 接口可用

4. **BPM集成**：
   - 必须配置流程定义：`oa_meeting_room_booking`
   - `FlowBillService` 接口已实现

## 九、技术支持

如遇问题，请检查：
1. 代码结构是否严格按照印章申请单的模式
2. 列表和详情是否分为两个独立页面
3. 路由配置是否正确
4. BasicForm组件是否正确使用
5. 审批流程定义是否配置
6. 字典数据是否完整

---

**重构完成时间**：2025-11-13
**参照模板**：印章申请单 (sealapply)
**代码规范**：宇擎项目标准规范

