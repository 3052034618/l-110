# 碳中和管理后端服务

面向企业内部系统的碳预算与减排任务统一能力平台。提供预算创建、指标分解、任务登记、进度上报、预警查询和报表读取等接口。

---

## 📁 项目目录

打开 `110` 目录，核心文件一目了然：

```
110/
├── src/
│   └── main/
│       ├── java/com/carbon/management/
│       │   ├── CarbonManagementApplication.java    ← 【启动入口】
│       │   ├── controller/                          ← 接口文件
│       │   │   ├── HealthController.java           ← 健康检查
│       │   │   ├── BudgetController.java           ← 预算管理接口
│       │   │   ├── TaskController.java             ← 任务管理接口
│       │   │   ├── WarningController.java          ← 预警查询接口
│       │   │   ├── ReportController.java           ← 报表读取接口
│       │   │   └── TodoApprovalController.java     ← 待办与审批接口
│       │   ├── service/                             ← 业务逻辑
│       │   ├── repository/                          ← 数据访问
│       │   ├── entity/                              ← 数据库实体
│       │   ├── dto/                                 ← 请求参数
│       │   ├── vo/                                  ← 返回结果
│       │   ├── common/enums/                        ← 枚举定义
│       │   └── config/
│       │       └── DataInitializer.java            ← 演示数据初始化
│       └── resources/
│           └── application.yml                      ← 【配置文件】
├── data/                                            ← 【数据库文件】（启动后自动生成，持久化保存）
├── pom.xml                                          ← Maven 配置
├── start.bat                                        ← Windows 启动脚本
├── test-flow.bat                                    ← 【CMD一键测试】6接口完整流程
├── test-flow.ps1                                    ← 【PowerShell一键测试】6接口完整流程
├── api-test.http                                    ← 【HTTP测试】IDEA/VSCode直接运行
├── postman-collection.json                          ← Postman 接口文件（可直接导入）
├── QUICKSTART.md                                    ← ⚡ 快速启动卡片（3步启动+6步流程）
├── API-EXAMPLES.md                                  ← 📘 6接口curl示例+真实返回
└── README.md                                        ← 本文档（完整说明）
```

---

## 📚 快速上手文档索引

| 你想做什么 | 看这个文档 |
|-----------|------------|
| 3 分钟快速跑起来 | [QUICKSTART.md](file:///d:/trae-bz/TraeProjects/110/QUICKSTART.md) |
| 6条接口 **curl命令 + 真实返回示例** | [API-EXAMPLES.md](file:///d:/trae-bz/TraeProjects/110/API-EXAMPLES.md) |
| 想在 IDEA 或 VSCode 里点按钮直接测试 | 打开 `api-test.http` |
| 想导入 Postman 批量测试 | 导入 `postman-collection.json` |
| 想一键自动测试完整流程 | 双击 `test-flow.bat` 或 `test-flow.ps1` |
| 想看完整详细的设计说明 | 继续往下看 ↓ |

---

## 🚀 本地启动

### 前置条件

- **JDK 17** 或更高版本
- **Maven 3.6+**（可选，IDEA 内置 Maven 也可）

### 启动方式一：IDEA 启动（最推荐）

1. 用 **IntelliJ IDEA** 打开 `110` 目录
2. 等待 Maven 依赖自动下载完成
3. 找到启动类：`src/main/java/com/carbon/management/CarbonManagementApplication.java`
4. 右键类名 → **Run 'CarbonManagementApplication'**
5. 看到 `Started CarbonManagementApplication` 表示启动成功

### 启动方式二：命令行启动

如果已安装 Maven，在 `110` 目录下执行：

```bash
mvn spring-boot:run
```

或者双击 `start.bat` 启动。

### 启动方式三：打包后运行

```bash
mvn clean package
java -jar target/carbon-management-1.0.0.jar
```

---

## 🌐 访问地址

服务启动后，浏览器直接打开以下地址：

| 名称 | 地址 | 说明 |
|------|------|------|
| **健康检查** | http://localhost:8080/api/health | 验证服务是否启动成功 |
| **服务信息** | http://localhost:8080/api/info | 查看服务版本和各入口地址 |
| **Swagger 文档** | http://localhost:8080/api/swagger-ui.html | 在线调试接口 |
| **Knife4j 文档** | http://localhost:8080/api/doc.html | 更美观的接口文档 |
| **H2 数据库** | http://localhost:8080/api/h2-console | 数据库管理台 |

**H2 控制台登录信息**：
- JDBC URL: `jdbc:h2:file:./data/carbon_db`
- 用户名: `sa`
- 密码: `sa`

---

## ✅ 验证启动成功

访问 http://localhost:8080/api/health ，应该返回：

```json
{
  "code": 200,
  "message": "服务运行正常",
  "data": {
    "status": "UP",
    "service": "carbon-management",
    "version": "1.0.0",
    "timestamp": "2024-06-12 10:30:00"
  },
  "timestamp": 1718159400000
}
```

---

## 🔥 一键跑通完整流程

服务启动后，在 `110` 目录下打开 PowerShell 运行：

```powershell
powershell -ExecutionPolicy Bypass -File .\test-flow.ps1
```

**脚本会自动依次调用 6 条主流程接口**，并展示每一步的请求参数和返回结果：

1. ✅ 创建年度预算
2. ✅ 预算分解到部门
3. ✅ 登记减排任务
4. ✅ 上报任务进度
5. ✅ 查询预算进度 + 预警列表
6. ✅ 查询年度汇总报表

最后还会**验证数据持久化**：用组织、月份、排放范围查询，确认数据确实保存了。

---

## 📝 6 条主流程接口 - 完整调用示例

以下每一条 curl 命令都可以直接复制运行。

### 【接口 1】创建预算

**POST** `/api/budgets`

```bash
curl -X POST http://localhost:8080/api/budgets ^
  -H "Content-Type: application/json" ^
  -d "{\"budgetName\":\"2024年度范围1总预算\",\"orgId\":\"ORG001\",\"orgName\":\"集团总部\",\"budgetYear\":2024,\"emissionScope\":\"SCOPE_1\",\"totalBudget\":12000.00,\"warningThreshold\":80.00,\"dangerThreshold\":95.00,\"description\":\"全公司直接碳排放年度预算\"}"
```

**返回**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 10,
  "timestamp": 1718159400000
}
```

---

### 【接口 2】预算分解

**POST** `/api/budgets/decomposition`

```bash
curl -X POST http://localhost:8080/api/budgets/decomposition ^
  -H "Content-Type: application/json" ^
  -d "{\"budgetId\":10,\"deptId\":\"DEPT001\",\"deptName\":\"生产一部\",\"targetReduction\":5000.00,\"responsibilityPerson\":\"张三\",\"remark\":\"生产车间减排主责部门\"}"
```

**返回**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5,
  "timestamp": 1718159401000
}
```

---

### 【接口 3】登记任务

**POST** `/api/tasks`

```bash
curl -X POST http://localhost:8080/api/tasks ^
  -H "Content-Type: application/json" ^
  -d "{\"taskName\":\"锅炉低氮燃烧改造\",\"budgetId\":10,\"decompositionId\":5,\"orgId\":\"ORG001\",\"orgName\":\"集团总部\",\"deptId\":\"DEPT001\",\"deptName\":\"生产一部\",\"category\":\"ENERGY_SAVING\",\"estimatedReduction\":2000.00,\"startDate\":\"2024-01-15\",\"endDate\":\"2024-12-31\",\"responsibilityPerson\":\"张三\",\"description\":\"对3号锅炉进行低氮燃烧器改造\",\"measureDetail\":\"更换低氮燃烧器4台，优化空燃比控制\"}"
```

**返回**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 10,
  "timestamp": 1718159402000
}
```

---

### 【接口 4】进度上报

**POST** `/api/tasks/progress`

```bash
curl -X POST http://localhost:8080/api/tasks/progress ^
  -H "Content-Type: application/json" ^
  -d "{\"taskId\":10,\"reportPeriod\":\"2024-Q2\",\"periodReduction\":450.00,\"progressDesc\":\"已完成锅炉停炉检修，燃烧器设备已到货\",\"problemDesc\":\"受雨季影响，安装进度略有延迟\",\"nextPlan\":\"预计7月中旬完成安装调试\",\"reporter\":\"张三\"}"
```

**返回**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5,
  "timestamp": 1718159403000
}
```

---

### 【接口 5】预警查询

**POST** `/api/warnings/query`

```bash
curl -X POST http://localhost:8080/api/warnings/query ^
  -H "Content-Type: application/json" ^
  -d "{\"orgId\":\"ORG001\",\"isHandled\":false,\"pageNum\":1,\"pageSize\":10}"
```

**返回**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "warningCode": "WRN-20240612-0001",
        "warningLevel": "WARNING",
        "warningLevelDesc": "预警",
        "warningType": "BUDGET_USAGE",
        "warningTitle": "2024年度范围1总预算预算使用率预警",
        "warningContent": "当前预算使用率已达 85%，已超过预警阈值，请关注",
        "usageRate": 85.00,
        "isHandled": false,
        "createdTime": "2024-06-12T10:00:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1718159404000
}
```

---

### 【接口 6】年度汇总

**GET** `/api/reports/yearly?orgId=ORG001&year=2024`

```bash
curl "http://localhost:8080/api/reports/yearly?orgId=ORG001&year=2024"
```

**返回**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "year": 2024,
    "orgId": "ORG001",
    "orgName": "集团总部",
    "totalBudget": 27000.0000,
    "totalUsed": 6750.0000,
    "totalRemaining": 20250.0000,
    "totalTargetReduction": 14000.0000,
    "totalAchievedReduction": 3950.0000,
    "reductionCompletionRate": 28.21,
    "totalTaskCount": 5,
    "completedTaskCount": 1,
    "taskCompletionRate": 20.00,
    "scopeSummary": {
      "SCOPE_1": {
        "emissionScope": "SCOPE_1",
        "emissionScopeDesc": "范围1：直接排放",
        "budget": 22000.0000,
        "used": 3950.0000,
        "remaining": 18050.0000,
        "usageRate": 17.95
      },
      "SCOPE_2": {
        "emissionScope": "SCOPE_2",
        "emissionScopeDesc": "范围2：间接排放（能源）",
        "budget": 5000.0000,
        "used": 2800.0000,
        "remaining": 2200.0000,
        "usageRate": 56.00
      }
    },
    "deptSummary": [
      {
        "deptId": "DEPT001",
        "deptName": "生产一部",
        "targetReduction": 4000.0000,
        "achievedReduction": 1800.0000,
        "completionRate": 45.00,
        "taskCount": 2,
        "completedTaskCount": 0
      }
    ],
    "monthlyTrend": [
      {"month": 1, "budget": 800.0000, "used": 300.0000, "remaining": 500.0000, "reduction": 315.0000},
      {"month": 2, "budget": 800.0000, "used": 400.0000, "remaining": 400.0000, "reduction": 0.0000}
    ],
    "warningCount": 2,
    "handledWarningCount": 0
  },
  "timestamp": 1718159405000
}
```

---

## 🔍 数据持久化验证

完成上面的流程后，**重启服务**，然后执行查询验证数据还在：

```bash
# 按组织 + 年度查询预算，应该能查到刚创建的那条
curl "http://localhost:8080/api/budgets/org/ORG001/year/2024"

# 按类别查询任务，应该能查到刚创建的锅炉改造任务
curl -X POST http://localhost:8080/api/tasks/query ^
  -H "Content-Type: application/json" ^
  -d "{\"category\":\"ENERGY_SAVING\",\"pageNum\":1,\"pageSize\":10}"

# 查询任务进度历史，应该能查到刚才上报的 Q2 进度
curl "http://localhost:8080/api/tasks/10/progress"
```

所有数据都保存在 `110/data/` 目录中，重启不会丢失。需要重置数据时，删除 `data` 目录再启动即可。

---

## 📚 完整接口清单

| 模块 | 前缀 | 主要接口 |
|------|------|----------|
| 健康检查 | `/api` | `GET /health`、`GET /info` |
| 预算管理 | `/api/budgets` | 创建、更新、删除、查询、分解、调整、提交审批、进度 |
| 任务管理 | `/api/tasks` | 创建、更新、删除、查询、进度上报、附件管理 |
| 预警管理 | `/api/warnings` | 分页查询、按预算/任务/组织查询、处理预警 |
| 报表管理 | `/api/reports` | 年度汇总、预算进度报表、导出年度报告 |
| 待办审批 | `/api` | 待办查询/标记、审批处理/记录 |

共计 **50+ 个 REST API**，详见 [Swagger 文档](http://localhost:8080/api/swagger-ui.html)。

---

## 📋 枚举值速查

| 字段 | 可选值 |
|------|--------|
| **排放范围** | `SCOPE_1` 直接排放 / `SCOPE_2` 能源间接 / `SCOPE_3` 其他间接 |
| **预算状态** | `DRAFT` 草稿 / `PENDING_APPROVAL` 待审批 / `APPROVED` 已审批 / `REJECTED` 已驳回 / `ADJUSTED` 已调整 |
| **任务类别** | `ENERGY_SAVING` 节电 / `FUEL_SUBSTITUTION` 替代燃料 / `GREEN_PROCUREMENT` 绿色采购 / `PROCESS_OPTIMIZATION` 工艺优化 / `WASTE_RECYCLING` 回收 / `CARBON_SINK` 碳汇 |
| **任务状态** | `NOT_STARTED` 未开始 / `IN_PROGRESS` 进行中 / `COMPLETED` 已完成 / `DELAYED` 已延期 |
| **预警等级** | `NORMAL` 正常 / `ATTENTION` 关注 / `WARNING` 预警 / `DANGER` 严重 |

---

## 🔄 切换到 MySQL

修改 `src/main/resources/application.yml` 中的数据库配置即可：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/carbon_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

---

## 📌 说明

- 首次启动会自动创建**演示数据**（3 条预算、3 条分解、4 条任务），方便直接测试
- 数据库文件在 `110/data/` 目录，**删除此目录可重置所有数据**
- 所有接口返回格式统一为 `{code, message, data, timestamp}`
- 建议配合 `postman-collection.json` 导入 Postman 使用
