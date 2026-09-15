**English** · [中文](#chinese)

<a name="english"></a>
# ruoyi-office · Intent as a Service reference host

A [yudao](https://github.com/YunaiV/ruoyi-vue-pro)-based office suite backend and its
[Vben](https://github.com/vbenjs/vue-vben-admin) front end in a single repository, with
**Intent as a Service** embedded as a working reference implementation — including a full CRM intent
chain (40 intents).

> **Intent as a Service removes the chat box.** A business page shows a row of *intent buttons*; one
> click runs the intent and a structured result card renders in place. The AI runs **in-process
> inside the application**, so permissions, transactions and data scope stay exactly as the host
> defines them — no separate account system, no cross-domain calls, no data leaving your boundary.

This repository is one of three:

| Repository | What it is |
|---|---|
| [intent-sdk](https://github.com/intent-as-a-service/intent-sdk) | The framework: protocol, execution engine, host SPI, Spring Boot starter |
| [intent-ui-sdk](https://github.com/intent-as-a-service/intent-ui-sdk) | The framework-agnostic front end: floating button, drawer, result cards, trace |
| [RuoYi-Vue-Plus](https://github.com/intent-as-a-service/RuoYi-Vue-Plus) | The other reference host (RuoYi-Vue-Plus + plus-ui, 14 system/monitor intents) |

---

## Repository layout

| Path | Origin | Contents |
|---|---|---|
| `ruoyi-office/` | yudao-based office suite, JDK 21 / Spring Boot 3.5 | Backend **+ `yudao-module-intent` + the CRM intent chain** |
| `ruoyi-office-vben/` | Vben Admin | Front end |

## What was added on top of upstream

### `ruoyi-office/yudao-module-intent/` — the platform layer

- `IntentAutoConfiguration` / `IntentProperties` / `IntentUiConfiguration` — auto-configuration and
  the `intent-ui` static resources (debug console).
- Three bridges, the yudao counterparts of the host SPI: `HostPrincipalProvider`,
  `HostPermissionPolicy` and `HostContextBridge`. The context bridge is not optional — intent tools
  run on their own threads, and without carrying the request context across, the login state and
  data-scope filtering **silently stop working**. No exception, just wrong results.
- Four controllers: intents, intent configuration, intent rules, executor profiles.
- Four tables: `IntentSpecDO`, `IntentConfigDO`, `IntentRuleDO`, `ExecutorProfileDO`.

### `ruoyi-office/yudao-module-crm/` — the business intent chain

- **40 intent YAML specs** covering the whole CRM flow: customers, contacts, clues, business
  opportunities, contracts, receivables, products, statistics and team management.
- **7 declarative fact rules** — dataset + filter + badge + items + parameter mapping. Conditions and
  wording live in YAML, so a threshold change needs no Java change and no release.
- Supporting Java: intent configuration, fact providers and status listeners (contract / receivable).

### Front end

- Floating AI button and the intent screens in `ruoyi-office-vben/apps/web-antd`.

### SQL

- `ruoyi-office/sql/mysql/intent.sql` — **the four intent tables plus the 意图中心 menus and their
  super-admin grants** (idempotent). The older incremental script `add_intent_executor.sql` is kept for
  history and is already covered by it.

---

## Quick start

**Prerequisites**: JDK 21, Maven, MySQL 8, Redis, Node 20.19+ with pnpm 10.

**1. Install the intent SDK** (not published to Maven Central yet). This builds all six modules
including the executors — pi ships as a local package inside that repository, so nothing else is
needed:

```bash
git clone git@github.com:intent-as-a-service/intent-sdk.git
cd intent-sdk && mvn install -DskipTests
```

**2. Create the database and import the schema:**

```bash
mysql -uroot -p -e "CREATE DATABASE \`ruoyi-office\` DEFAULT CHARACTER SET utf8mb4"
mysql -uroot -p ruoyi-office < ruoyi-office/sql/mysql/ruoyi-vue-pro.sql   # ① upstream yudao schema
mysql -uroot -p ruoyi-office < ruoyi-office/sql/mysql/quartz.sql          # ② optional: scheduled jobs
mysql -uroot -p ruoyi-office < ruoyi-office/sql/mysql/intent.sql          # ③ required: intent tables + menus
```

> **`intent.sql` is required for the intent features.** It creates the four intent tables
> (`intent_spec` / `intent_config` / `intent_rule` / `intent_executor`) plus the 意图中心 back-office
> menus and their super-admin grants. It is idempotent and supersedes the older incremental script
> `add_intent_executor.sql`.
>
> The intent *content* is deliberately not in SQL: the 40 intent specs, the declarative fact rules and
> the executor profiles ship as YAML under `yudao-module-intent` / `yudao-module-crm` and are **seeded
> into the database at startup**.

**3. Build and run the backend.**

> ⚠️ **Build with `-Pboot`.** The `cloud` profile is active *by default* and sets
> `skip.repackage=false`, which repackages **every module** into its own executable jar; `yudao-server`
> then bundles those nested jars and the application fails at startup with
> `No qualifying bean of type '...PermissionCommonApi'` (measured: a 1.1 GB `yudao-server.jar`
> instead of ~190 MB). `-Pboot` sets `skip.repackage=true` so modules stay plain jars and
> `yudao-server` does the single packaging step.

Defaults: port `48080`, database `ruoyi-office` on `127.0.0.1:33061`. Adjust
`ruoyi-office/yudao-server/src/main/resources/application-local.yaml` to match your environment.

```bash
cd ruoyi-office && mvn -Pboot -DskipTests install
java -jar yudao-server/target/yudao-server.jar        # monolith on port 48080
```

**4. Run the front end** (the Ant Design app):

```bash
cd ruoyi-office-vben && pnpm install && pnpm dev:antd
```

## Trying the intents

| Where | What to expect |
|---|---|
| Startup log | `[intent]` lines: registered intents, tools and declarative fact rules |
| `/intent-ui/index.html` on the backend | The intent debug console — try the catalog, slot form, execution and trace without writing code |
| CRM pages, bottom right | The draggable AI button, with todos derived from real CRM data |

## Documentation

- The intent platform's own design documents live in the
  [intent-sdk](https://github.com/intent-as-a-service/intent-sdk) repository.
- The RuoYi-Vue-Plus host ships a full delivery write-up in its `backend/docs/intent/` directory.

## Notes

- **Upstream git history is not included.** This repository was imported as a snapshot; the two
  sub-directories came from their upstream projects without their commit history.
- **Runtime data is deliberately not published.** `ruoyi-office/yudao-server/data/` (execution
  traces and user feedback) and `ruoyi-office/sql/mysql/dump-*.sql` (full local database exports,
  which contain real customer, employee and `ai_api_key` rows) are git-ignored by design. Import your
  own data to run the CRM intents against something meaningful.
- Only the intent-related paths differ from upstream.

---

<a name="chinese"></a>
# ruoyi-office × 意图即服务

[English](#english) · **中文**

本仓把基于 [yudao](https://github.com/YunaiV/ruoyi-vue-pro) 的办公套件后端与
[Vben](https://github.com/vbenjs/vue-vben-admin) 前端放在一个仓库里，并在其上植入了
**意图即服务**的完整参考实现 —— 含一条完整的 CRM 意图链（40 个意图）。

> **意图即服务去掉聊天框**：业务页面放一排意图按钮，点击即执行，结果卡片就地渲染。
> AI 能力以**原生 SDK 进程内嵌入**应用，权限、事务、数据范围完全沿用宿主。

### 目录结构

| 路径 | 内容 |
|---|---|
| `ruoyi-office/` | yudao 系后端（JDK 21 / Spring Boot 3.5）**+ `yudao-module-intent` + CRM 意图链** |
| `ruoyi-office-vben/` | Vben 前端 |

### 相对上游新增了什么

- **平台层** `yudao-module-intent/`：自动装配、`/intent-ui` 调试台、三件套桥接
  （`HostPrincipalProvider` / `HostPermissionPolicy` / `HostContextBridge`）、4 个控制器、4 张表。
- **业务意图链** `yudao-module-crm/`：**40 份意图 YAML**（客户 / 联系人 / 线索 / 商机 / 合同 /
  回款 / 产品 / 统计 / 团队）、**7 份声明式事实规则**，以及配套的 Configuration / FactProvider /
  状态监听器。
- **前端**：悬浮球与意图页面（`apps/web-antd`）。
- **建表**：`sql/mysql/intent.sql`（四张意图表 + 意图中心菜单与超管授权，幂等可重复执行；
  历史增量脚本 `add_intent_executor.sql` 已被它覆盖）。

### 为什么必须有"上下文桥"

意图工具在**独立线程**执行，而宿主的登录态与数据权限挂在原线程上。不把请求上下文搬过去
**不会报错**，而是**静默返回错误结果** —— 越权查到别人的数据，或者什么都查不到。

### 快速开始

```bash
# 1) 先装意图 SDK（尚未发布中央仓）。会一并构建执行器模块；
#    pi 以本地包形式随该仓分发，无需任何其它前置。
git clone git@github.com:intent-as-a-service/intent-sdk.git
cd intent-sdk && mvn install -DskipTests

# 2) 建库导表（②③ 可选/必需见下：intent.sql 是意图功能的必需项）
mysql -uroot -p -e "CREATE DATABASE \`ruoyi-office\` DEFAULT CHARACTER SET utf8mb4"
mysql -uroot -p ruoyi-office < ruoyi-office/sql/mysql/ruoyi-vue-pro.sql   # ① 基础表（上游 yudao）
mysql -uroot -p ruoyi-office < ruoyi-office/sql/mysql/quartz.sql          # ② 定时任务表（可选）
mysql -uroot -p ruoyi-office < ruoyi-office/sql/mysql/intent.sql          # ③ 意图表 + 菜单（必需）

# 3) 构建并启动后端。**必须带 -Pboot**：
#    默认激活的是 cloud（微服务）profile，skip.repackage=false 会把每个模块都单独打成可执行包，
#    yudao-server 再把这些嵌套 jar 收进来，启动就会报
#    No qualifying bean of type '...PermissionCommonApi'（实测 jar 会从 ~190MB 涨到 1.1GB）。
#    -Pboot 让模块保持普通 jar，由 yudao-server 统一打包。
#    默认端口 48080，库 ruoyi-office@127.0.0.1:33061
cd ruoyi-office && mvn -Pboot -DskipTests install
java -jar yudao-server/target/yudao-server.jar

# 4) 启动前端（Ant Design 版）
cd ruoyi-office-vben && pnpm install && pnpm dev:antd
```

### 有意不公开的内容

`ruoyi-office/yudao-server/data/`（意图执行留痕与用户反馈）与
`ruoyi-office/sql/mysql/dump-*.sql`（完整业务库导出，含真实客户 / 员工数据与 `ai_api_key` 表）
**一律 git-ignore**。想看到有意义的意图结果，请导入你自己的数据。

本仓为**快照导入**，未携带上游 git 历史；除意图相关路径外均为上游原样代码。
