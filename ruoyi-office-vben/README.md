<h1 align="center">
  <br>
  RuoYi Office · 企业管理一体化平台
  <br>
</h1>

<h4 align="center">🏢 一个平台，管好整个企业 —— 中小企业全业务数字化办公解决方案</h4>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5-blue.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20Cloud-2025-blue.svg" alt="Spring Cloud">
  <img src="https://img.shields.io/badge/Vue-3.5-brightgreen.svg" alt="Vue3">
  <img src="https://img.shields.io/badge/TypeScript-5.8-blue.svg" alt="TypeScript">
  <img src="https://img.shields.io/badge/Vben%20Admin-Latest-orange.svg" alt="Vben Admin">
  <img src="https://img.shields.io/badge/JDK-17%2F21-red.svg" alt="JDK">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
</p>

<p align="center">
  <a href="#-在线体验">🌐 在线演示</a> •
  <a href="#-功能模块总览">📦 功能模块</a> •
  <a href="#-技术架构">🏗️ 技术架构</a> •
  <a href="#-快速开始">🚀 快速开始</a> •
  <a href="#-联系我们">📞 联系我们</a>
</p>

---

<div align="center">

### 💬 扫码添加微信，获取专属技术支持 & 定制方案

<!-- 请替换为实际微信二维码图片 -->
<img src="/.image/readme/wechat.jpg" width="200" alt="微信二维码">

**微信号：17156169080** · 备注「**RuoYi Office**」即可

[![在线演示](https://img.shields.io/badge/🌐_点击体验-在线演示-blue?style=for-the-badge)](http://ruoyioffice.com/web/)

</div>

---

## ⚡ 一句话介绍

> **RuoYi Office** 是基于 **Spring Cloud Alibaba + Gateway + Nacos + RocketMQ + Vue3 + Vben Admin** 构建的**中小企业全业务办公一体化平台**，涵盖 **OA 协同办公、BPM 审批流程、HRM 人力资源、CRM 客户管理、ERP 进销存、合同管理、PMS 项目管理、EAM 资产管理、WMS 仓储管理、MALL 企业商城、AI 智能助手、IoT 物联网、数据报表** 等十余个子系统，**一套代码解决中小企业全业务信息化需求。**

---

## 🎯 为什么选择 RuoYi Office？

<table>
<tr>
<td align="center" width="25%">

### 🔄 告别多系统割裂
不再需要分别采购 OA + HRM + CRM + ERP 等多套系统，一个平台全部打通，数据互联互通

</td>
<td align="center" width="25%">

### 💰 大幅降低成本
模块化按需启用，中小企业无需为用不到的功能买单，IT 投入降低 **70%+**

</td>
<td align="center" width="25%">

### 🏗️ 微服务架构
基于 Spring Cloud Alibaba 微服务架构，各模块独立部署、弹性伸缩，生产级可靠

</td>
<td align="center" width="25%">

### 🤖 AI 赋能办公
内置 AI 大模型能力，对接 ChatGPT / 通义千问 / 文心一言 / DeepSeek 等，让办公更智能

</td>
</tr>
</table>

---

## 🌐 在线体验

| 🖥️ 体验入口 | 地址 | 账号/密码 |
|:---:|:---:|:---:|
| **✅ 演示地址** | 👉 **[http://ruoyioffice.com/web/](http://ruoyioffice.com/web/)** | `admin` / `admin123` |

> 💡 **温馨提示**：演示环境数据每日重置，请勿存储重要信息。建议使用 Chrome / Edge 浏览器获得最佳体验。

<!-- 
  📸 系统截图展示区 —— 请替换为实际截图
  建议截图尺寸：1920x1080 或 1440x900
  存放路径：/.image/ 目录下
-->

<table>
<tr>
<td width="50%">

**🔐 登录页面**

![登录页面](/.image/readme/login.png)

</td>
<td width="50%">

**🏠 系统首页 / 工作台**

![系统首页](/.image/readme/dashboard.png)

</td>
</tr>
<tr>
<td width="50%">

**📋 OA 协同办公 — 车辆管理**

![OA协同办公](/.image/readme/oa.png)

</td>
<td width="50%">

**👥 HRM 人力资源 — 员工档案**

![HRM人力资源](/.image/readme/hrm.png)

</td>
</tr>
<tr>
<td width="50%">

**🤝 CRM 客户管理 — 线索管理**

![CRM客户管理](/.image/readme/crm.png)

</td>
<td width="50%">

**📊 ERP 进销存 — 销售订单**

![ERP进销存](/.image/readme/erp.png)

</td>
</tr>
<tr>
<td width="50%">

**⚙️ BPM 流程中心 — 流程模型（仿钉钉/飞书设计器）**

![BPM流程审批](/.image/readme/bpm.png)

</td>
<td width="50%">

**🤖 AI 智能助手 — 对话 / 绘画 / 写作 / 音乐 / 知识库**

![AI智能助手](/.image/readme/ai.png)

</td>
</tr>
</table>

---

## 📦 功能模块总览

> 🔥 一个平台覆盖企业 **全链路业务**，模块化架构、按需组合、独立部署

### 🟢 已上线模块

| 序号 | 子系统 | 模块 | 核心功能 | 状态 |
|:---:|:---|:---|:---|:---:|
| 1 | **🏠 OA 协同办公** | `yudao-module-oa` | 用车管理（申请/归还）、印章管理（用印审批）、会议室管理（在线预约）、企业云盘（文件共享/权限管理）、通知公告 | ✅ |
| 2 | **👥 HRM 人力资源** | `yudao-module-hrm` | 员工档案（教育/工作/家属信息）、入职申请、转正申请、人事调动、离职管理、一键生成系统账号 | ✅ |
| 3 | **🤝 CRM 客户管理** | `yudao-module-crm` | 线索管理、客户管理、联系人、商机跟进、合同管理、回款管理、产品管理、数据统计分析 | ✅ |
| 4 | **📊 ERP 进销存** | `yudao-module-erp` | 采购管理（订单/入库/退货）、销售管理（订单/出库/退货）、库存管理、产品管理、财务管理（收付款） | ✅ |
| 5 | **🏭 EAM 资产管理** | `yudao-module-asset` | 资产分类、资产登记、资产物品管理、资产全生命周期管理（采购→领用→维修→报废） | ✅ |
| 6 | **📦 WMS 仓储管理** | `yudao-module-wms` | 仓库管理、采购订单、采购入库、出入库操作、库存查询、通用出入库单 | ✅ |
| 7 | **🛒 MALL 企业商城** | `yudao-module-mall` | 商品管理、订单管理、营销活动（优惠券/秒杀/拼团/砍价/满减）、售后退款、分销裂变 | ✅ |
| 8 | **🤖 AI 智能助手** | `yudao-module-ai` | AI 对话（多模型切换）、AI 写作、AI 绘图、知识库问答、AI 思维导图 | ✅ |
| 9 | **⚙️ BPM 流程引擎** | `yudao-module-bpm` | 仿钉钉/飞书可视化设计器 + BPMN 设计器、会签/或签/依次审批、抄送/转办/委派/加减签、父子流程、超时审批、自动提醒 | ✅ |
| 10 | **💳 支付中心** | `yudao-module-pay` | 微信支付、支付宝、多渠道管理、支付/退款/回调管理 | ✅ |
| 11 | **📈 数据报表** | `yudao-module-report` | 报表设计器、大屏设计器、数据可视化 | ✅ |
| 12 | **📡 IoT 物联网** | `yudao-module-iot` | 设备管理、设备数据采集与监控 | ✅ |
| 13 | **🔧 系统管理** | `yudao-module-system` | 用户/角色/权限/菜单、多租户 SaaS、短信/邮件/站内信、SSO 单点登录、数据权限 | ✅ |
| 14 | **🌐 微信公众号** | `yudao-module-mp` | 粉丝管理、消息管理、自动回复、模板消息、菜单/素材管理 | ✅ |
| 15 | **👤 会员中心** | `yudao-module-member` | 会员管理、会员标签/等级/分组、积分签到 | ✅ |
| 16 | **🔩 基础设施** | `yudao-module-infra` | 代码生成器、API 文档、文件存储、定时任务、日志管理、链路追踪 | ✅ |

### 🔵 规划中模块（持续迭代中）

| 子系统 | 规划功能 | 解决的企业痛点 |
|:---|:---|:---|
| **📋 PMS 项目管理** | 项目立项、任务分解/看板、工时管理、里程碑、甘特图 | 项目进度不透明、工时难统计 |
| **📝 合同管理（增强版）** | 合同起草/模板、电子签章、到期预警、合同台账、关联收付款 | 合同散落各处、到期无人跟进 |
| **💰 财务管理** | 费用报销、预算管理、收支流水、发票管理、应收应付 | 报销流程繁琐、财务数据分散 |
| **📅 考勤管理** | 打卡签到、排班管理、请假/加班/出差审批、考勤报表 | 考勤规则复杂、统计困难 |
| **💵 薪酬管理** | 薪资核算、社保公积金、个税计算、工资条推送 | 手动算薪易出错 |
| **🎯 绩效管理** | KPI / OKR 目标管理、绩效考核、360° 评估 | 绩效考核流于形式 |
| **📧 CMS 内容管理** | 企业官网/内网门户、文章发布、栏目管理 | 企业宣传渠道缺失 |
| **🎓 培训管理** | 课程管理、在线学习、考试测评、培训记录 | 员工培训无体系 |

---

## 🔥 核心功能详解

### 🏠 OA 协同办公

> 让日常办公事务全部线上化，告别纸质单据和跑腿签字

| 功能模块 | 功能描述 | 亮点 |
|:---|:---|:---|
| 🚗 用车管理 | 公务车辆登记、用车申请、用车审批、车辆归还、车辆状态实时查看 | 与 BPM 流程引擎深度集成 |
| 🔏 印章管理 | 印章登记、用印申请、用印审批、用印记录追溯 | 全流程可追溯 |
| 🏢 会议室管理 | 会议室资源管理、在线预约、时间冲突检测、预约日历 | 可视化日历预约 |
| 📁 企业云盘 | 文件上传/下载、目录管理、文件共享、权限控制、收藏管理 | 多级权限精细化控制 |

### 👥 HRM 人力资源管理

> 从入职到离职的全员工生命周期管理

| 功能模块 | 功能描述 |
|:---|:---|
| 📋 员工档案 | 员工基本信息、教育经历、工作经历、家属信息、银行卡信息 |
| ✅ 入职管理 | 入职申请单、入职审批流程、自动生成员工工号、一键创建系统账号 |
| ⬆️ 转正管理 | 转正申请、转正审批、试用期→正式员工状态流转 |
| 🔄 调动管理 | 人事调动申请、部门/岗位变更审批 |
| 🚪 离职管理 | 离职申请、离职审批、离职交接 |

### ⚙️ BPM 流程引擎

> 历经头部企业生产验证，标配仿钉钉/飞书 + BPMN **双设计器**

| 设计器模式 | 适用场景 |
|:---|:---|
| **🎨 仿钉钉/飞书设计器** | 轻量配置简单流程，拖拽搭建，10 分钟完成审批流程配置 |
| **📐 BPMN 设计器** | 复杂业务场景深度编排，满足多层级审批及流程自动化 |

**已实现功能**：会签 ✅ · 或签 ✅ · 依次审批 ✅ · 抄送 ✅ · 驳回 ✅ · 转办 ✅ · 委派 ✅ · 加签 ✅ · 减签 ✅ · 撤销 ✅ · 终止 ✅ · 表单权限 ✅ · 超时审批 ✅ · 自动提醒 ✅ · 父子流程 ✅ · 条件分支 ✅ · 并行分支 ✅ · 包容分支 ✅ · 路由分支 ✅ · 触发节点 ✅ · 延迟节点 ✅

### 🤖 AI 智能助手

> 内置 AI 大模型能力，让企业办公更智能

- 🗣️ **AI 对话**：支持 ChatGPT / 通义千问 / 文心一言 / DeepSeek / 讯飞星火等多模型切换
- ✍️ **AI 写作**：公文起草、邮件撰写、报告生成
- 🎨 **AI 绘图**：基于 DALL·E / Midjourney / Stable Diffusion
- 📚 **知识库问答**：上传企业文档，AI 自动问答
- 🧠 **AI 思维导图**：一键生成结构化思维导图

---

## 🏗️ 技术架构
![技术架构图](/.image/common/yudao-cloud-architecture.png)

### 核心技术选型

| 层级 | 技术选型 | 版本 |
|:---|:---|:---|
| **微服务框架** | Spring Cloud Alibaba + Spring Boot | 2025.0.0 / 3.5.x |
| **服务网关** | Spring Cloud Gateway | Latest |
| **注册/配置中心** | Nacos | 2.x |
| **消息队列** | RocketMQ | 5.x |
| **工作流引擎** | Flowable（仿钉钉/飞书 + BPMN 双设计器） | 7.0.x |
| **前端框架** | Vue 3 + Vben Admin + TypeScript | 3.5 / 5.8 |
| **UI 组件库** | Ant Design Vue / Element Plus / Naive UI | 4.x / 2.x |
| **数据库** | MySQL / PostgreSQL / Oracle / 达梦 / 人大金仓 / SQL Server | 多版本 |
| **缓存** | Redis + Redisson | 6.x / 3.50 |
| **ORM** | MyBatis Plus | 3.5.12 |
| **安全框架** | Spring Security + OAuth 2.0 + JWT | 6.x |
| **定时任务** | XXL-Job | 2.4.x |
| **监控追踪** | SkyWalking + Spring Boot Admin | 9.x / 3.x |
| **AI 大模型** | ChatGPT / 通义千问 / 文心一言 / DeepSeek 等 | 多模型支持 |
| **多终端** | PC Web + uni-app（APP / 小程序 / H5） | 全终端覆盖 |

---

## 🌟 平台亮点

<table>
<tr>
<td width="50%">

### 🏢 全业务覆盖
一个平台打通办公、人事、销售、采购、仓储、资产等全链路业务，**告别信息孤岛**

### 🔌 模块化架构
各子系统独立部署、按需组合，轻量启动不臃肿，**像搭积木一样组合你的企业系统**

### 🔄 流程驱动
所有业务单据与 BPM 审批引擎深度集成，**仿钉钉/飞书可视化配置，10 分钟搭建审批流**

### 🤖 AI 赋能
内置 AI 大模型能力，**智能对话、AI 写作、知识库问答**，让办公效率翻倍

### 🔐 企业级安全
Spring Security + OAuth 2.0 + RBAC 权限模型 + **数据权限隔离 + 多租户 SaaS**

</td>
<td width="50%">

### 👥 多租户 SaaS
支持 SaaS 多租户模式，**一套系统服务多家企业**，租户间数据完全隔离

### 📱 多终端适配
PC Web + 移动端（H5 / 小程序 / APP），**随时随地移动办公**

### 🗄️ 多数据库
MySQL / PostgreSQL / Oracle / 达梦 / 人大金仓 / SQL Server，**全面适配国产信创**

### ⚡ 代码生成器
一键生成前后端代码 + SQL 脚本 + API 文档，**开发效率翻倍**

### 📊 数据可视化
内置报表设计器 + 大屏设计器，**拖拽即可生成数据看板和酷炫大屏**

</td>
</tr>
</table>

---

## 📸 系统截图

> 📌 以下为系统核心功能截图，更多功能请访问 [在线演示](http://ruoyioffice.com/web/) 体验

### 系统管理

| 模块 | 截图 | 截图 | 截图 |
|:---|:---:|:---:|:---:|
| 登录 & 首页 | ![登录](/.image/readme/login.png) | ![首页](/.image/readme/dashboard.png) | ![个人中心](/.image/个人中心.jpg) |
| 用户 & 应用 | ![用户管理](/.image/用户管理.jpg) | ![令牌管理](/.image/令牌管理.jpg) | ![应用管理](/.image/应用管理.jpg) |
| 租户 & 套餐 | ![租户管理](/.image/租户管理.jpg) | ![租户套餐](/.image/租户套餐.png) | - |
| 部门 & 岗位 | ![部门管理](/.image/部门管理.jpg) | ![岗位管理](/.image/岗位管理.jpg) | - |
| 菜单 & 角色 | ![菜单管理](/.image/菜单管理.jpg) | ![角色管理](/.image/角色管理.jpg) | - |

### 工作流程

| BPMN 设计器 | 钉钉/飞书设计器 |
|:---:|:---:|
| ![](/.image/工作流设计器-bpmn.jpg) | ![](/.image/工作流设计器-simple.jpg) |

| 模块 | 截图 | 截图 | 截图 |
|:---|:---:|:---:|:---:|
| 流程模型 | ![流程模型-列表](/.image/流程模型-列表.jpg) | ![流程模型-设计](/.image/流程模型-设计.jpg) | ![流程模型-定义](/.image/流程模型-定义.jpg) |
| 我的流程 | ![我的流程-列表](/.image/我的流程-列表.jpg) | ![我的流程-发起](/.image/我的流程-发起.jpg) | ![我的流程-详情](/.image/我的流程-详情.jpg) |
| 待办 & 已办 | ![任务列表-审批](/.image/任务列表-审批.jpg) | ![任务列表-待办](/.image/任务列表-待办.jpg) | ![任务列表-已办](/.image/任务列表-已办.jpg) |

### OA 协同办公

| 模块 | 截图 |
|:---|:---:|
| 车辆管理（含用车分类、车辆信息、状态管理） | ![OA-车辆管理](/.image/readme/oa.png) |

> 📸 更多 OA 模块截图（印章管理、会议室预约、企业云盘、日程管理等）请访问 [在线演示](http://ruoyioffice.com/web/) 体验

### HRM 人力资源

| 模块 | 截图 |
|:---|:---:|
| 员工档案（员工工号/姓名/部门/职位/入职/转正全流程） | ![HRM-员工档案](/.image/readme/hrm.png) |

> 📸 更多 HRM 模块截图（组织架构图、入职管理、转正管理、调动管理、离职管理等）请访问 [在线演示](http://ruoyioffice.com/web/) 体验

### CRM 客户管理

| 模块 | 截图 |
|:---|:---:|
| CRM 线索管理（线索/客户/联系人/商机/合同/回款/产品） | ![CRM系统](/.image/readme/crm.png) |

### ERP 进销存

| 模块 | 截图 |
|:---|:---:|
| ERP 销售订单（采购/销售/库存/产品/财务管理） | ![ERP系统](/.image/readme/erp.png) |

### AI 大模型

| 截图 |
|:---:|
| ![AI智能助手](/.image/readme/ai.png) |

### 商城系统

| 截图 | 截图 |
|:---:|:---:|
| ![商城功能图](/.image/common/mall-feature.png) | ![商城预览](/.image/common/mall-preview.png) |

### 数据报表 & 大屏

| 模块 | 截图 | 截图 | 截图 |
|:---|:---:|:---:|:---:|
| 报表设计器 | ![数据报表](/.image/报表设计器-数据报表.jpg) | ![图形报表](/.image/报表设计器-图形报表.jpg) | ![打印设计](/.image/报表设计器-打印设计.jpg) |
| 大屏设计器 | ![大屏列表](/.image/大屏设计器-列表.jpg) | ![大屏预览](/.image/大屏设计器-预览.jpg) | ![大屏编辑](/.image/大屏设计器-编辑.jpg) |

### 移动端

| 截图 | 截图 | 截图 |
|:---:|:---:|:---:|
| ![](/.image/admin-uniapp/01.png) | ![](/.image/admin-uniapp/02.png) | ![](/.image/admin-uniapp/03.png) |
| ![](/.image/admin-uniapp/04.png) | ![](/.image/admin-uniapp/05.png) | ![](/.image/admin-uniapp/06.png) |

---

## 🏷️ 适用场景

适用于 **10 ~ 500 人** 规模的中小企业，覆盖但不限于以下行业：

<table>
<tr>
<td>🏭 制造业</td>
<td>🏪 贸易公司</td>
<td>💻 互联网/科技</td>
<td>🏗️ 建筑工程</td>
</tr>
<tr>
<td>🚛 物流运输</td>
<td>🎓 教育培训</td>
<td>🏥 医疗健康</td>
<td>🛍️ 零售连锁</td>
</tr>
<tr>
<td>🏦 金融服务</td>
<td>🍽️ 餐饮酒店</td>
<td>🏢 物业管理</td>
<td>🏛️ 政府/事业单位</td>
</tr>
</table>

---

## 🚀 快速开始

### 环境要求

| 环境 | 版本要求 |
|:---|:---|
| JDK | 17 或 21 |
| MySQL | 5.7+ 或 8.0+ |
| Redis | 5.0+ |
| Node.js | 18+ |
| Maven | 3.9+ |
| Nacos | 2.x |

### 一键启动

```bash
# 1. 克隆项目
git clone https://github.com/your-org/ruoyi-office.git

# 2. 导入数据库
mysql -u root -p < sql/mysql/ruoyi-vue-pro.sql

# 3. 启动后端（先启动 Nacos）
cd ruoyi-office
mvn clean install -DskipTests
java -jar yudao-server/target/yudao-server.jar

# 4. 启动前端
cd ruoyi-office-vben
pnpm install
npm run dev:antd
```

> 📖 更详细的部署文档请查看 [快速启动指南](https://cloud.iocoder.cn/quick-start/)

---

## 📐 项目结构

### 后端模块

| 模块 | 说明 |
|:---|:---|
| `yudao-dependencies` | Maven 依赖版本管理 |
| `yudao-framework` | Java 框架核心拓展 |
| `yudao-gateway` | Spring Cloud 微服务网关 |
| `yudao-server` | 管理后台 + 用户 APP 服务端 |
| `yudao-module-system` | 系统管理（用户/角色/权限/租户） |
| `yudao-module-infra` | 基础设施（代码生成/文件/日志） |
| `yudao-module-bpm` | 工作流程引擎 |
| `yudao-module-oa` | OA 协同办公 |
| `yudao-module-hrm` | HRM 人力资源管理 |
| `yudao-module-crm` | CRM 客户关系管理 |
| `yudao-module-erp` | ERP 进销存管理 |
| `yudao-module-asset` | EAM 资产管理 |
| `yudao-module-wms` | WMS 仓储管理 |
| `yudao-module-mall` | MALL 企业商城 |
| `yudao-module-pay` | 支付系统 |
| `yudao-module-ai` | AI 大模型 |
| `yudao-module-iot` | IoT 物联网 |
| `yudao-module-member` | 会员中心 |
| `yudao-module-mp` | 微信公众号 |
| `yudao-module-report` | 数据报表 |

---

## 😎 开源协议

本项目采用 [MIT License](LICENSE) 开源协议，个人与企业可 100% 免费使用，不用保留类作者、Copyright 信息。

---

## 📞 联系我们

> 🔥 **想要快速上手？想要定制开发？想要了解更多？** 欢迎随时联系我们！

### 🤝 合作方式

| 服务类型 | 说明 |
|:---|:---|
| **🆓 开源使用** | 代码完全开源，直接 Fork 使用，社区交流 |
| **📚 技术支持** | 一对一技术指导，快速解决部署和使用问题 |
| **🎨 定制开发** | 根据企业需求定制功能模块，快速交付 |
| **🏢 企业级服务** | 提供企业级部署、运维、培训全套服务 |
| **💼 项目外包** | 基于本平台快速开发各类企业管理系统 |

### 📱 联系方式

<table>
<tr>
<td align="center" width="50%">

**💬 微信咨询**

添加微信好友，备注「**RuoYi Office**」

<!-- 请替换为实际微信二维码图片 -->
![微信二维码](/.image/readme/wechat.jpg)

微信号：**17156169080**

</td>
<td align="center" width="50%">

**💬 微信交流群**

扫码加入技术交流群

<!-- 请替换为实际微信群二维码图片 -->
![微信群二维码](/.image/readme/wechat-group-qr.png)

</td>
</tr>
</table>

### 🛒 获取方式

<table>
<tr>
<td align="center" width="33%">

#### Step 1️⃣ 体验系统
访问 [在线演示](http://ruoyioffice.com/web/)
<br>账号密码：`admin` / `admin123`

</td>
<td align="center" width="33%">

#### Step 2️⃣ 添加微信
扫码或搜索微信号 **17156169080**
<br>备注「**RuoYi Office**」

</td>
<td align="center" width="33%">

#### Step 3️⃣ 深入沟通
了解需求，提供定制方案
<br>选择适合的服务模式

</td>
</tr>
</table>

---

## 🎁 为什么中小企业需要一体化平台？

<details>
<summary><b>📊 点击展开：中小企业信息化痛点分析</b></summary>

### 常见困境

| 痛点 | 传统方式 | RuoYi Office 解决方案 |
|:---|:---|:---|
| 📊 系统割裂 | OA、CRM、ERP 各自独立，数据无法互通 | 一个平台集成所有业务，数据实时打通 |
| 💸 成本高昂 | 分别采购多套系统，每套都要付费 | 模块化按需启用，一套解决所有问题 |
| 🔄 流程断裂 | 审批需要在不同系统间切换 | BPM 引擎统一驱动所有业务流程 |
| 📱 无法移动办公 | 只能在公司电脑上操作 | PC + 移动端全终端覆盖 |
| 🔒 数据安全 | 数据分散在多个系统，难以统一管控 | 统一权限体系 + 数据权限隔离 |
| 🛠️ 扩展困难 | 传统系统封闭，难以二次开发 | 开源 + 微服务架构，灵活扩展 |
| 🤖 缺乏智能 | 重复性工作耗费大量人力 | AI 赋能，智能辅助决策和办公 |

### 投资回报

- 💰 IT 信息化投入降低 **70%+**
- ⏱️ 审批流程效率提升 **300%+**
- 📈 业务数据利用率提升 **200%+**
- 🔄 跨部门协作效率提升 **150%+**

</details>

---

## ⭐ Star History

如果这个项目对您有帮助，请给个 ⭐ Star，这对我们非常重要！

<!-- 替换为实际的 Star History 图表 -->
<!-- [![Star History Chart](https://api.star-history.com/svg?repos=your-org/ruoyi-office&type=Date)](https://star-history.com/#your-org/ruoyi-office&Date) -->

---

<p align="center">
  <b>🏢 RuoYi Office · 企业管理一体化平台</b>
  <br>
  <sub>一个平台，管好整个企业 —— 让中小企业数字化转型更简单</sub>
  <br><br>
  <a href="http://ruoyioffice.com/web/">🌐 在线演示</a> •
  <a href="#-联系我们">📞 联系我们</a> •
  <a href="#-获取方式">🛒 立即获取</a>
</p>

---

<!-- 
  🔍 SEO 关键词（搜索引擎优化）
  
  中小企业办公平台, 企业管理一体化平台, OA协同办公系统, OA办公系统, 审批流程系统,
  CRM客户管理系统, CRM客户关系管理, ERP进销存系统, ERP管理系统,
  HRM人力资源管理, 人事管理系统, 员工管理系统,
  BPM流程引擎, 工作流引擎, 仿钉钉审批, 飞书审批流程,
  资产管理系统EAM, 仓储管理系统WMS, 项目管理系统PMS,
  合同管理系统, 企业商城系统, AI智能办公,
  Spring Cloud微服务, Spring Boot, Vue3前端框架, Vben Admin,
  多租户SaaS平台, 低代码开发平台, 代码生成器,
  开源办公系统, 企业信息化, 数字化转型, 企业数字化,
  RuoYi, 宇擎, ruoyi-office, 若依办公
  
  制造业ERP, 贸易公司管理系统, 中小企业管理软件,
  免费OA系统, 开源ERP, 开源CRM, Java企业管理系统
-->
