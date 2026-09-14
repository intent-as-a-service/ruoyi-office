# 会议室预定功能部署说明

## 一、功能概述

会议室预定申请单功能，支持会议室的预定申请、审批流程、时间冲突检测等功能。

## 二、数据库表结构

### 1. 主表：oa_meeting_room_booking

**表说明**：会议室预定申请单表

**字段说明**：
- `id`: 主键ID
- `bill_code`: 单据编号（唯一）
- `process_instance_id`: 流程实例编号（对接审批流）
- `process_status`: 单据状态（0草稿 1审批中 2审批通过 3审批拒绝 4已取消）
- `room_id`: 会议室ID
- `room_name`: 会议室名称
- `room_location`: 会议室位置
- `room_type`: 会议室类型
- `meeting_title`: 会议名称
- `meeting_start_time`: 会议开始时间（整点或半点）
- `meeting_end_time`: 会议结束时间（整点或半点）
- `moderator_id`: 主持人ID
- `moderator_name`: 主持人姓名
- `meeting_remark`: 会议备注
- `reminder_type`: 会议提醒（1不提醒 2提前5分钟 3提前10分钟 4提前15分钟 5提前30分钟）
- `attendees`: 与会人ID列表（JSON数组格式）
- `attendee_names`: 与会人姓名列表（JSON数组格式）
- `attachment_urls`: 附件URL列表（JSON数组格式）
- `use_status`: 使用状态（0待使用 1使用中 2已完成 3已取消）
- `creator_name`: 申请人姓名
- `company_id`: 公司ID
- `company_name`: 公司名称
- `dept_id`: 部门ID
- `dept_name`: 部门名称
- `remark`: 备注

### 2. 字典配置

需要在 `system_dict_type` 和 `system_dict_data` 表中添加以下字典：

**字典类型**：
- `oa_meeting_booking_status`: 会议室预定状态

**字典数据**：
| 排序 | 标签 | 值 | 字典类型 | 颜色类型 |
|------|------|-----|----------|---------|
| 1 | 待审批 | 0 | oa_meeting_booking_status | warning |
| 2 | 已通过 | 1 | oa_meeting_booking_status | success |
| 3 | 已拒绝 | 2 | oa_meeting_booking_status | danger |
| 4 | 已取消 | 3 | oa_meeting_booking_status | info |
| 5 | 已完成 | 4 | oa_meeting_booking_status | primary |

### 3. 菜单权限

需要在 `system_menu` 表中添加以下菜单权限：

**父菜单**：会议室管理（在OA协同办公下）

**子菜单**：会议室预定
- 查询预定：`oa:meeting-room-booking:query`
- 创建预定：`oa:meeting-room-booking:create`
- 更新预定：`oa:meeting-room-booking:update`
- 删除预定：`oa:meeting-room-booking:delete`
- 导出预定：`oa:meeting-room-booking:export`
- 提交预定：`oa:meeting-room-booking:submit`
- 取消预定：`oa:meeting-room-booking:cancel`
- 审批通过：`oa:meeting-room-booking:approve`
- 审批拒绝：`oa:meeting-room-booking:reject`

## 三、部署步骤

### 1. 执行SQL脚本

按顺序执行以下SQL文件：
```bash
# 1. 执行会议室预定表SQL
mysql -u root -p database_name < meeting_room_booking.sql
```

### 2. 后端代码说明

已生成的后端代码文件：
- **DO对象**：`MeetingRoomBookingDO.java`
- **VO对象**：
  - `MeetingRoomBookingSaveReqVO.java` - 保存/提交请求VO
  - `MeetingRoomBookingPageReqVO.java` - 分页查询请求VO
  - `MeetingRoomBookingRespVO.java` - 响应VO
- **Mapper**：`MeetingRoomBookingMapper.java`
- **Service**：
  - `MeetingRoomBookingService.java` - 服务接口
  - `MeetingRoomBookingServiceImpl.java` - 服务实现
- **Controller**：`MeetingRoomBookingController.java`
- **错误码**：在 `ErrorCodeConstants.java` 中添加
- **枚举**：在 `OaBillTypeEnum.java` 中添加

### 3. 前端代码说明

已生成的前端代码文件：
- **API接口**：`src/api/oa/meetingroom/booking/index.ts`
- **数据配置**：`src/views/oa/meetingroom/booking/data.ts`
- **列表页面**：`src/views/oa/meetingroom/booking/index.vue`
- **表单组件**：`src/views/oa/meetingroom/booking/modules/form.vue`

### 4. 路由配置（需手动添加）

在前端路由配置中添加：
```typescript
{
  path: 'meetingroombooking',
  name: 'OaMeetingRoomBooking',
  component: () => import('#/views/oa/meetingroom/booking/index.vue'),
  meta: {
    title: '会议室预定',
    icon: 'ep:calendar',
  },
}
```

## 四、功能特性

### 1. 时间控制
- 前端限制：只能选择整点或半点时间（如：15:00、15:30）
- 后端校验：
  - 开始时间必须早于结束时间
  - 开始时间不能是过去时间
  - 检测会议室时间冲突

### 2. 审批流程
- 单据支持草稿保存和提交审批两种操作
- 与BPM审批流程集成
- 流程定义Key：`oa_meeting_room_booking`

### 3. 状态管理
- **单据状态（process_status）**：
  - 0：草稿
  - 1：审批中
  - 2：审批通过
  - 3：审批拒绝
  - 4：已取消
  
- **使用状态（use_status）**：
  - 0：待使用
  - 1：使用中
  - 2：已完成
  - 3：已取消

### 4. 权限控制
- 草稿状态：可编辑、可删除
- 审批中/已通过：可取消预定
- 审批拒绝/已取消：只能查看

## 五、注意事项

1. **时间冲突检测**：系统会自动检测同一会议室在相同时间段是否有其他预定（待使用或使用中状态）
2. **字典数据**：确保前端使用的字典类型已在数据库中正确配置
3. **审批流程**：需要在BPM模块中配置对应的流程定义（流程Key：`oa_meeting_room_booking`）
4. **TODO项**：
   - 会议室列表API需要对接实际的会议室信息接口
   - 用户列表API需要对接实际的用户信息接口
   - 公司和部门列表API需要对接实际的组织架构接口

## 六、测试建议

### 1. 功能测试
- [ ] 创建预定申请单（草稿）
- [ ] 提交预定申请单（发起审批）
- [ ] 编辑草稿状态的申请单
- [ ] 删除草稿状态的申请单
- [ ] 取消审批中或已通过的申请单
- [ ] 查看申请单详情
- [ ] 分页查询和搜索
- [ ] 导出Excel

### 2. 时间冲突测试
- [ ] 预定完全重叠的时间段
- [ ] 预定部分重叠的时间段
- [ ] 预定包含其他预定的时间段
- [ ] 预定被其他预定包含的时间段

### 3. 审批流程测试
- [ ] 提交审批
- [ ] 审批通过
- [ ] 审批拒绝
- [ ] 取消审批

### 4. 边界测试
- [ ] 选择过去的时间（应被禁止）
- [ ] 结束时间早于开始时间（应被禁止）
- [ ] 必填字段验证
- [ ] 字段长度限制验证

## 七、技术支持

如遇问题，请检查：
1. 数据库表结构是否正确创建
2. 字典数据是否正确配置
3. 菜单权限是否正确分配
4. 审批流程定义是否配置
5. 前后端字段映射是否一致

