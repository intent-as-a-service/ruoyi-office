# OA协同办公-企业云盘数据库表结构设计文档

## 概述

本文档描述了OA协同办公模块下企业云盘功能的数据库表结构设计，包含三个核心表：
- `oa_file_info` - 文件信息表
- `oa_file_permission` - 文件权限表
- `oa_file_favorite` - 收藏文件表

## 表结构详细说明

### 1. oa_file_info（文件信息表）

**表名**: `oa_file_info`  
**用途**: 存储文件和文件夹的基本信息

#### 字段说明

| 字段名 | 类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | - | 是 | 自增 | 文件ID（主键） |
| parent_id | bigint | - | 是 | 0 | 父文件夹ID，0表示根目录 |
| file_type | tinyint | - | 是 | - | 文件类型（0文件夹 1文件） |
| file_name | varchar | 255 | 是 | - | 文件名称 |
| file_size | bigint | - | 否 | 0 | 文件大小（字节），文件夹为0 |
| file_extension | varchar | 50 | 否 | NULL | 文件扩展名（如：jpg、pdf、docx） |
| file_path | varchar | 500 | 否 | NULL | 文件存储路径 |
| file_url | varchar | 500 | 否 | NULL | 文件访问URL |
| file_md5 | varchar | 64 | 否 | NULL | 文件MD5值，用于秒传和去重 |
| is_shared | bit | 1 | 是 | 0 | 是否共享文件夹（0否 1是），仅文件夹有效 |
| share_type | tinyint | - | 否 | NULL | 文件夹分享类型（0人员 1组织），仅共享文件夹有效 |
| share_permission | tinyint | - | 否 | NULL | 分享权限（0仅查看 1可管理），仅共享文件夹有效 |
| owner_id | bigint | - | 是 | - | 所有者ID（用户ID） |
| owner_name | varchar | 100 | 否 | NULL | 所有者名称 |
| dept_id | bigint | - | 否 | NULL | 所属部门ID |
| dept_name | varchar | 100 | 否 | NULL | 所属部门名称 |
| sort_order | int | - | 否 | 0 | 排序序号 |
| remark | varchar | 500 | 否 | NULL | 备注 |
| creator | varchar | 64 | 否 | '' | 创建者 |
| create_time | datetime | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updater | varchar | 64 | 否 | '' | 更新者 |
| update_time | datetime | - | 是 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | bit | 1 | 是 | 0 | 是否删除（逻辑删除标记） |
| tenant_id | bigint | - | 是 | 0 | 租户编号 |

#### 索引说明

- **PRIMARY KEY**: `id` - 主键索引
- **INDEX**: `idx_parent_id` - 父文件夹索引，用于快速查询某个文件夹下的内容
- **INDEX**: `idx_owner_id` - 所有者索引，用于快速查询某用户的文件
- **INDEX**: `idx_file_type` - 文件类型索引，用于区分文件和文件夹
- **INDEX**: `idx_create_time` - 创建时间索引，用于按时间排序

#### 设计说明

1. **树形结构**: 通过 `parent_id` 实现文件夹的树形结构
2. **类型区分**: `file_type` 字段区分文件（1）和文件夹（0）
3. **共享机制**: 
   - `is_shared` 标识文件夹是否共享
   - `share_type` 决定是按人员还是按组织共享
   - `share_permission` 控制共享权限级别
4. **冗余字段**: `owner_name`、`dept_name` 等冗余字段用于减少关联查询，提升性能

---

### 2. oa_file_permission（文件权限表）

**表名**: `oa_file_permission`  
**用途**: 管理文件夹的共享权限，记录哪些人员或组织可以访问哪些共享文件夹

#### 字段说明

| 字段名 | 类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | - | 是 | 自增 | 权限ID（主键） |
| file_id | bigint | - | 是 | - | 文件ID（关联oa_file_info.id） |
| share_type | tinyint | - | 是 | - | 分享类型（0人员 1组织） |
| target_id | bigint | - | 是 | - | 目标ID（人员ID或组织ID） |
| target_name | varchar | 100 | 否 | NULL | 目标名称（人员名称或组织名称） |
| permission | tinyint | - | 是 | 0 | 权限（0仅查看 1可管理） |
| creator | varchar | 64 | 否 | '' | 创建者 |
| create_time | datetime | - | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updater | varchar | 64 | 否 | '' | 更新者 |
| update_time | datetime | - | 是 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | bit | 1 | 是 | 0 | 是否删除（逻辑删除标记） |
| tenant_id | bigint | - | 是 | 0 | 租户编号 |

#### 索引说明

- **PRIMARY KEY**: `id` - 主键索引
- **INDEX**: `idx_file_id` - 文件索引，用于快速查询某文件的权限
- **INDEX**: `idx_target` - 目标索引（组合：share_type + target_id），用于快速查询某人员或组织的权限
- **UNIQUE INDEX**: `uk_file_share_target` - 唯一索引，防止重复授权（组合：file_id + share_type + target_id + deleted）

#### 设计说明

1. **灵活授权**: 支持按人员或按组织两种方式授权
2. **权限级别**: 
   - `0-仅查看`: 只能查看和下载
   - `1-可管理`: 可以上传、删除、重命名等
3. **唯一约束**: 同一个文件对同一个目标只能有一条权限记录
4. **冗余字段**: `target_name` 用于快速显示，减少关联查询

---

### 3. oa_file_favorite（收藏文件表）

**表名**: `oa_file_favorite`  
**用途**: 记录用户收藏的文件，实现快速访问功能

#### 字段说明

| 字段名 | 类型 | 长度 | 是否必填 | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | bigint | - | 是 | 自增 | 收藏ID（主键） |
| file_id | bigint | - | 是 | - | 文件ID（关联oa_file_info.id） |
| user_id | bigint | - | 是 | - | 用户ID |
| user_name | varchar | 100 | 否 | NULL | 用户名称 |
| creator | varchar | 64 | 否 | '' | 创建者 |
| create_time | datetime | - | 是 | CURRENT_TIMESTAMP | 创建时间（收藏时间） |
| updater | varchar | 64 | 否 | '' | 更新者 |
| update_time | datetime | - | 是 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | bit | 1 | 是 | 0 | 是否删除（逻辑删除标记） |
| tenant_id | bigint | - | 是 | 0 | 租户编号 |

#### 索引说明

- **PRIMARY KEY**: `id` - 主键索引
- **INDEX**: `idx_file_id` - 文件索引，用于查询某文件被多少人收藏
- **INDEX**: `idx_user_id` - 用户索引，用于快速查询某用户的收藏列表
- **UNIQUE INDEX**: `uk_file_user` - 唯一索引，防止重复收藏（组合：file_id + user_id + deleted）

#### 设计说明

1. **简单明了**: 只记录收藏关系，不包含复杂逻辑
2. **防重复**: 通过唯一索引防止用户重复收藏同一个文件
3. **快速查询**: 双向索引支持快速查询用户的收藏和文件的收藏者
4. **冗余字段**: `user_name` 用于展示，减少关联查询

---

## 表关系说明

```
oa_file_info (1) -----> (*) oa_file_permission
    文件           可以被多个人员/组织访问

oa_file_info (1) -----> (*) oa_file_favorite  
    文件           可以被多个用户收藏

oa_file_info (1) -----> (*) oa_file_info
    父文件夹        可以包含多个子文件/文件夹
```

## 数据字典

### 文件类型（file_type）
- `0`: 文件夹
- `1`: 文件

### 分享类型（share_type）
- `0`: 按人员分享
- `1`: 按组织分享

### 权限类型（permission / share_permission）
- `0`: 仅查看（只读权限）
- `1`: 可管理（读写权限）

### 是否共享（is_shared）
- `0`: 否（私有）
- `1`: 是（共享）

## 使用示例

### 1. 查询用户的根目录文件列表

```sql
SELECT * FROM oa_file_info 
WHERE owner_id = ? 
  AND parent_id = 0 
  AND deleted = 0 
  AND tenant_id = ?
ORDER BY file_type ASC, create_time DESC;
```

### 2. 查询某文件夹下的所有内容

```sql
SELECT * FROM oa_file_info 
WHERE parent_id = ? 
  AND deleted = 0 
  AND tenant_id = ?
ORDER BY file_type ASC, sort_order ASC, create_time DESC;
```

### 3. 查询用户有权限访问的共享文件夹

```sql
-- 按人员共享的文件夹
SELECT f.* FROM oa_file_info f
INNER JOIN oa_file_permission p ON f.id = p.file_id
WHERE p.target_id = ? 
  AND p.share_type = 0
  AND f.is_shared = 1
  AND f.deleted = 0 
  AND p.deleted = 0
  AND f.tenant_id = ?;

-- 按组织共享的文件夹（假设用户所属组织ID为 deptId）
SELECT f.* FROM oa_file_info f
INNER JOIN oa_file_permission p ON f.id = p.file_id
WHERE p.target_id = ? 
  AND p.share_type = 1
  AND f.is_shared = 1
  AND f.deleted = 0 
  AND p.deleted = 0
  AND f.tenant_id = ?;
```

### 4. 查询用户的收藏列表

```sql
SELECT f.* FROM oa_file_info f
INNER JOIN oa_file_favorite fav ON f.id = fav.file_id
WHERE fav.user_id = ? 
  AND fav.deleted = 0
  AND f.deleted = 0
  AND f.tenant_id = ?
ORDER BY fav.create_time DESC;
```

### 5. 添加文件收藏

```sql
INSERT INTO oa_file_favorite 
  (file_id, user_id, user_name, creator, tenant_id)
VALUES 
  (?, ?, ?, ?, ?);
```

### 6. 为文件夹授权

```sql
-- 给某个人员授权
INSERT INTO oa_file_permission 
  (file_id, share_type, target_id, target_name, permission, creator, tenant_id)
VALUES 
  (?, 0, ?, ?, ?, ?, ?);

-- 给某个组织授权
INSERT INTO oa_file_permission 
  (file_id, share_type, target_id, target_name, permission, creator, tenant_id)
VALUES 
  (?, 1, ?, ?, ?, ?, ?);
```

## 扩展建议

### 1. 文件版本管理
如果需要支持文件版本管理，可以新增 `oa_file_version` 表：

```sql
CREATE TABLE `oa_file_version` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '版本ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `version_no` int NOT NULL COMMENT '版本号',
  `file_path` varchar(500) COMMENT '文件存储路径',
  `file_url` varchar(500) COMMENT '文件访问URL',
  `file_size` bigint COMMENT '文件大小',
  `file_md5` varchar(64) COMMENT '文件MD5值',
  `remark` varchar(500) COMMENT '版本说明',
  ...
) ENGINE = InnoDB COMMENT = 'OA协同办公-企业云盘-文件版本表';
```

### 2. 操作日志
如果需要记录文件操作历史，可以新增 `oa_file_log` 表：

```sql
CREATE TABLE `oa_file_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `operation_type` tinyint NOT NULL COMMENT '操作类型（1上传 2下载 3删除 4重命名 5移动 6分享）',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) COMMENT '操作人名称',
  `operation_detail` varchar(1000) COMMENT '操作详情',
  ...
) ENGINE = InnoDB COMMENT = 'OA协同办公-企业云盘-操作日志表';
```

### 3. 文件标签
如果需要支持文件标签分类，可以新增标签相关表：

```sql
CREATE TABLE `oa_file_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `tag_color` varchar(20) COMMENT '标签颜色',
  ...
) ENGINE = InnoDB COMMENT = 'OA协同办公-企业云盘-文件标签表';

CREATE TABLE `oa_file_tag_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID',
  ...
) ENGINE = InnoDB COMMENT = 'OA协同办公-企业云盘-文件标签关联表';
```

## 注意事项

1. **租户隔离**: 所有表都包含 `tenant_id` 字段，确保多租户数据隔离
2. **逻辑删除**: 使用 `deleted` 字段实现逻辑删除，便于数据恢复
3. **性能优化**: 合理使用索引，避免全表扫描
4. **数据一致性**: 删除文件时需要同步删除权限和收藏记录
5. **存储优化**: 文件实际内容存储在文件系统，数据库只存储元数据
6. **安全性**: 文件访问需要先验证权限，防止越权访问

## 文件存放位置

SQL脚本文件位置：
- 完整脚本: `yudao-module-oa-server/src/main/resources/sql/enterprise_cloud_disk.sql`
- 分表脚本:
  - `yudao-module-oa-server/src/main/resources/sql/oa_file_info.sql`
  - `yudao-module-oa-server/src/main/resources/sql/oa_file_permission.sql`
  - `yudao-module-oa-server/src/main/resources/sql/oa_file_favorite.sql`

