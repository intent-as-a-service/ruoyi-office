# 企业云盘功能说明

## 功能概述

企业云盘是一个基于Web的文件管理系统,类似于百度网盘、阿里云盘等,提供文件/文件夹的创建、上传、下载、收藏、分享等功能。

## 功能特性

### 1. 文件管理
- **文件夹管理**: 创建、编辑、删除文件夹,支持多级目录结构
- **文件上传**: 支持单个或批量文件上传
- **文件下载**: 支持文件下载到本地
- **文件重命名**: 快速重命名文件或文件夹
- **文件移动**: 支持文件/文件夹在不同目录间移动
- **批量操作**: 支持批量选择和删除

### 2. 文件浏览
- **列表视图**: 以列表形式展示文件,包含文件名、大小、所有者、修改时间等信息
- **面包屑导航**: 清晰展示当前目录路径,支持快速跳转
- **文件图标**: 根据文件类型显示不同的图标(文件夹、图片、文档、Excel、PDF等)
- **文件大小格式化**: 自动将字节转换为合适的单位显示(B、KB、MB、GB等)

### 3. 收藏功能
- **收藏文件**: 可以收藏常用的文件或文件夹
- **我的收藏**: 独立的收藏视图,快速访问收藏的文件
- **收藏状态**: 文件列表中显示星标标识

### 4. 分享功能(扩展)
- **文件夹分享**: 可以将文件夹分享给指定人员或组织
- **分享类型**: 支持人员分享和组织分享两种方式
- **权限控制**: 支持"仅查看"和"可管理"两种权限级别

### 5. 权限管理
- **所有者**: 每个文件/文件夹都有明确的所有者
- **部门隔离**: 文件按部门进行组织和管理
- **操作权限**: 通过菜单权限控制用户的操作能力

## 数据库表结构

### 1. oa_file_info (文件信息表)
存储文件和文件夹的基本信息

主要字段:
- `id`: 文件ID(主键)
- `parent_id`: 父文件夹ID
- `file_type`: 文件类型(0-文件, 1-文件夹)
- `file_name`: 文件名称
- `file_extension`: 文件扩展名
- `file_size`: 文件大小(字节)
- `file_path`: 文件存储路径
- `file_url`: 文件访问URL
- `file_md5`: 文件MD5值
- `owner_id`: 所有者ID
- `owner_name`: 所有者名称
- `dept_id`: 部门ID
- `dept_name`: 部门名称
- `is_shared`: 是否共享文件夹
- `share_type`: 分享类型(0-人员, 1-组织)
- `share_permission`: 分享权限(0-仅查看, 1-可管理)
- `sort_order`: 排序

### 2. oa_file_permission (文件权限表)
存储文件夹的分享权限关系

主要字段:
- `id`: 权限ID(主键)
- `file_id`: 文件ID
- `share_type`: 分享类型(0-人员, 1-组织)
- `target_id`: 人员或组织ID
- `target_name`: 人员或组织名称
- `permission`: 权限(0-仅查看, 1-可管理)

### 3. oa_file_favorite (收藏文件表)
存储用户的文件收藏关系

主要字段:
- `id`: 收藏ID(主键)
- `file_id`: 文件ID
- `user_id`: 收藏人ID
- `user_name`: 收藏人名称

## 部署说明

### 1. 数据库初始化

按顺序执行以下SQL文件:

```bash
# 1. 创建表结构
mysql -u root -p database_name < oa_file_info.sql
mysql -u root -p database_name < oa_file_permission.sql
mysql -u root -p database_name < oa_file_favorite.sql

# 或者执行合并文件
mysql -u root -p database_name < enterprise_cloud_disk.sql

# 2. 初始化菜单和字典
mysql -u root -p database_name < oa_file_menu_dict.sql
```

### 2. 后端部署

后端代码已创建在以下位置:
- DO: `cn.iocoder.yudao.module.oa.dal.dataobject.file`
- Mapper: `cn.iocoder.yudao.module.oa.dal.mysql.file`
- Service: `cn.iocoder.yudao.module.oa.service.file`
- Controller: `cn.iocoder.yudao.module.oa.controller.admin.file`
- VO: `cn.iocoder.yudao.module.oa.controller.admin.file.vo`

### 3. 前端部署

前端代码已创建在以下位置:
- API: `yudao-ui-admin-vben/apps/web-antd/src/api/oa/file/index.ts`
- 页面: `yudao-ui-admin-vben/apps/web-antd/src/views/oa/file/`
  - `index.vue`: 主页面
  - `data.ts`: 数据结构和工具函数
  - `modules/folder-form.vue`: 文件夹表单组件

### 4. 菜单配置

执行 `oa_file_menu_dict.sql` 后,会自动创建以下菜单结构:

```
OA协同办公
  └─ 企业云盘
      ├─ 查询文件 (oa:file:query)
      ├─ 创建文件夹 (oa:file:create)
      ├─ 上传文件 (oa:file:upload)
      ├─ 更新文件 (oa:file:update)
      ├─ 删除文件 (oa:file:delete)
      ├─ 导出文件列表 (oa:file:export)
      └─ 收藏文件 (oa:file:favorite)
```

## API接口说明

### 文件管理接口
- `POST /oa/file/create`: 创建文件/文件夹
- `PUT /oa/file/update`: 更新文件/文件夹
- `DELETE /oa/file/delete`: 删除文件/文件夹
- `DELETE /oa/file/delete-list`: 批量删除文件/文件夹
- `GET /oa/file/get`: 获取文件详情
- `GET /oa/file/page`: 分页查询文件列表
- `GET /oa/file/list`: 获取指定文件夹下的文件列表

### 文件操作接口
- `PUT /oa/file/move`: 移动文件/文件夹
- `PUT /oa/file/rename`: 重命名文件/文件夹

### 收藏接口
- `POST /oa/file/favorite`: 收藏文件
- `DELETE /oa/file/unfavorite`: 取消收藏文件
- `GET /oa/file/favorite-list`: 获取收藏的文件列表

### 导出接口
- `GET /oa/file/export-excel`: 导出文件列表为Excel

## 使用说明

### 1. 访问企业云盘
登录系统后,在左侧菜单中找到"OA协同办公" -> "企业云盘"

### 2. 创建文件夹
1. 点击"新建文件夹"按钮
2. 输入文件夹名称
3. 设置排序(可选)
4. 点击确定

### 3. 上传文件
1. 点击"上传文件"按钮
2. 选择要上传的文件
3. 等待上传完成

### 4. 浏览文件
- 双击文件夹进入该文件夹
- 点击面包屑导航可以快速返回上级目录
- 点击"全部文件"返回根目录

### 5. 收藏文件
- 点击文件右侧的星标图标即可收藏
- 再次点击可取消收藏
- 点击左侧"我的收藏"查看所有收藏的文件

### 6. 文件操作
- **下载**: 点击文件操作列的"下载"按钮
- **重命名**: 点击"重命名"按钮,输入新名称
- **删除**: 点击"删除"按钮,确认后删除
- **批量删除**: 选中多个文件后,点击顶部"批量删除"按钮

## 扩展建议

### 1. 文件上传增强
- 集成文件上传组件(如 Upload 组件)
- 支持拖拽上传
- 显示上传进度
- 支持断点续传
- 文件大小限制和类型限制

### 2. 文件预览
- 图片预览(支持缩放、旋转等)
- PDF在线预览
- Office文档在线预览(需要集成第三方服务)
- 视频在线播放

### 3. 搜索功能
- 全局文件搜索
- 按文件名、类型、大小等条件筛选
- 高级搜索(按日期范围、所有者等)

### 4. 分享功能完善
- 生成分享链接
- 设置分享有效期
- 分享密码保护
- 分享次数限制

### 5. 版本管理
- 文件版本历史记录
- 版本对比
- 版本回退

### 6. 协同编辑
- 多人同时编辑文档
- 实时同步
- 编辑冲突处理

### 7. 回收站
- 软删除机制
- 回收站功能
- 文件恢复

### 8. 存储优化
- 文件去重(通过MD5)
- 文件压缩
- 分片上传
- 对象存储集成(OSS、S3等)

## 注意事项

1. **文件存储**: 当前设计中,文件存储路径和URL需要在上传文件时进行设置,建议集成文件存储服务
2. **权限控制**: 确保用户只能访问自己有权限的文件
3. **存储空间**: 建议为每个用户或部门设置存储空间配额
4. **安全性**: 上传文件时需要进行安全检查,防止恶意文件上传
5. **性能**: 对于大文件列表,建议使用分页加载和虚拟滚动
6. **备份**: 定期备份重要文件数据

## 技术栈

### 后端
- Spring Boot
- MyBatis-Plus
- MySQL

### 前端
- Vue 3
- Vben Admin
- Ant Design Vue
- VxeTable

## 更新日志

### v1.0.0 (2024-01-09)
- 初始版本
- 实现基本的文件/文件夹管理功能
- 实现文件收藏功能
- 实现面包屑导航
- 实现文件类型图标显示
- 实现文件大小格式化
- 创建数据库表结构
- 创建前后端完整代码
- 创建菜单和字典配置

