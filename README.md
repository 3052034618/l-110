# 碳中和管理后端服务

面向企业内部系统的碳预算与减排任务统一能力平台，提供预算创建、指标分解、任务登记、进度上报、预警查询和报表读取等接口，支持门户、移动端和报表工具统一取数。

## 技术栈

- **框架**: Spring Boot 3.2.x
- **持久层**: Spring Data JPA
- **数据库**: H2 (文件模式，持久化存储) / 支持 MySQL 切换
- **接口文档**: Swagger3 + Knife4j
- **构建工具**: Maven

## 本地启动

### 方式一：Maven 命令启动

```bash
# 编译项目
mvn clean compile

# 启动服务
mvn spring-boot:run
```

### 方式二：IDEA 启动

1. 用 IDEA 打开项目目录
2. 找到 `src/main/java/com/carbon/management/CarbonManagementApplication.java`
3. 右键 `Run 'CarbonManagementApplication'`

### 访问地址

| 名称 | 地址 |
|------|------|
| API 基础路径 | http://localhost:8080/api |
| Swagger UI | http://localhost:8080/api/swagger-ui.html |
| Knife4j 文档 | http://localhost:8080/api/doc.html |
| H2 数据库控制台 | http://localhost:8080/api/h2-console |

**H2 控制台配置**:
- JDBC URL: `jdbc:h2:file:./data/carbon_db`
- 用户名: `sa`
- 密码: `sa`

> 数据保存在项目根目录的 `data/` 文件夹中，重启服务后数据不会丢失。如需重置数据，删除 `data` 目录重新启动即可。

## 项目结构

```
src/main/java/com/carbon/management/
├── CarbonManagementApplication.java   # 启动入口
├── common/                            # 通用返回结构、枚举、异常
├── config/                            # 配置类（跨域、Swagger、数据初始化）
├── controller/                        # REST API 控制层
│   ├── BudgetController.java          # 预算管理接口
│   ├── TaskController.java            # 任务管理接口
│   ├── WarningController.java         # 预警管理接口
│   ├── ReportController.java          # 报表管理接口
│   └── TodoApprovalController.java    # 待办与审批接口
├── dto/                               # 请求参数对象
├── entity/                            # 数据库实体
├── repository/                        # 数据访问层
├── service/                           # 业务逻辑接口
│   └── impl/                          # 业务逻辑实现
└── vo/                                # 返回视图对象
```

## 核心业务流程

以下是一条从**创建预算 → 分解指标 → 登记任务 → 上报进度 → 查询预警 → 查看报表**的完整流程。

---

### 第一步：创建预算

**接口**: `POST /api/budgets`

```bash
curl -X POST http://localhost:8080/api/budgets \
  -H "Content-Type: application/json" \
  -d '{
    "budgetName": "2024年度范围1总预算",
    "orgId": "ORG001",
    "orgName": "集团总部",
    "budgetYear": 2024,
    "emissionScope": "SCOPE_1",
    "totalBudget": 12000.00,
    "warningThreshold": 80.00,
    "dangerThreshold": 95.00,
    "description": "2024年度全公司直接碳排放预算"
  }'
```

**返回示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1,
  "timestamp": 1718200000000
}
```

---

### 第二步：预算分解到部门

**接口**: `POST /api/budgets/decomposition`

```bash
curl -X POST http://localhost:8080/api/budgets/decomposition \
  -H "Content-Type: application/json" \
  -d '{
    "budgetId": 1,
    "deptId": "DEPT001",
    "deptName": "生产一部",
    "targetReduction": 5000.00,
    "responsibilityPerson": "张三",
    "remark": "生产车间减排主责部门"
  }'
```

**返回示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```

---

### 第三步：登记减排任务

**接口**: `POST /api/tasks`

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "taskName": "锅炉低氮燃烧改造",
    "budgetId": 1,
    "decompositionId": 1,
    "orgId": "ORG001",
    "orgName": "集团总部",
    "deptId": "DEPT001",
    "deptName": "生产一部",
    "category": "ENERGY_SAVING",
    "estimatedReduction": 2000.00,
    "startDate": "2024-01-15",
    "endDate": "2024-12-31",
    "responsibilityPerson": "张三",
    "description": "对3号锅炉进行低氮燃烧器改造",
    "measureDetail": "更换低氮燃烧器，优化空燃比控制"
  }'
```

**返回示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5
}
```

---

### 第四步：上报任务进度

**接口**: `POST /api/tasks/progress`

```bash
curl -X POST http://localhost:8080/api/tasks/progress \
  -H "Content-Type: application/json" \
  -d '{
    "taskId": 5,
    "reportPeriod": "2024-Q2",
    "periodReduction": 450.00,
    "progressDesc": "已完成锅炉停炉检修，燃烧器设备已到货",
    "problemDesc": "受雨季影响，安装进度略有延迟",
    "nextPlan": "预计7月中旬完成安装调试",
    "reporter": "张三"
  }'
```

**返回示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```

---

### 第五步：查询预算进度和预警

**预算进度查询**: `GET /api/budgets/{id}/progress`

```bash
curl http://localhost:8080/api/budgets/1/progress
```

**返回示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "budgetId": 1,
    "budgetCode": "BGT-ORG001-2024-ANNUAL",
    "budgetName": "2024年度范围1总预算",
    "orgName": "集团总部",
    "totalBudget": 12000.0000,
    "usedAmount": 450.0000,
    "remainingAmount": 11550.0000,
    "usageRate": 3.75,
    "warningLevel": "NORMAL",
    "warningLevelDesc": "正常",
    "overBudgetRisk": false,
    "relatedTaskCount": 1,
    "achievedReduction": 450.0000,
    "targetReduction": 5000.0000,
    "reductionRate": 9.00
  }
}
```

**预警列表查询**: `POST /api/warnings/query`

```bash
curl -X POST http://localhost:8080/api/warnings/query \
  -H "Content-Type: application/json" \
  -d '{
    "orgId": "ORG001",
    "isHandled": false,
    "pageNum": 1,
    "pageSize": 10
  }'
```

---

### 第六步：查看年度汇总报表

**接口**: `GET /api/reports/yearly`

```bash
curl "http://localhost:8080/api/reports/yearly?orgId=ORG001&year=2024"
```

**返回示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "year": 2024,
    "orgId": "ORG001",
    "orgName": "集团总部",
    "totalBudget": 15000.0000,
    "totalUsed": 6300.0000,
    "totalRemaining": 8700.0000,
    "totalTargetReduction": 9000.0000,
    "totalAchievedReduction": 3500.0000,
    "reductionCompletionRate": 38.89,
    "totalTaskCount": 4,
    "completedTaskCount": 1,
    "taskCompletionRate": 25.00,
    "scopeSummary": { ... },
    "deptSummary": [ ... ],
    "monthlyTrend": [ ... ],
    "warningCount": 2,
    "handledWarningCount": 0
  }
}
```

---

## 主要接口清单

### 预算管理 (`/api/budgets`)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/budgets` | 创建预算 |
| PUT | `/budgets/{id}` | 更新预算 |
| DELETE | `/budgets/{id}` | 删除预算 |
| GET | `/budgets/{id}` | 预算详情 |
| POST | `/budgets/query` | 分页查询预算 |
| POST | `/budgets/{id}/submit` | 提交审批 |
| GET | `/budgets/{id}/progress` | 预算执行进度 |
| POST | `/budgets/decomposition` | 创建预算分解 |
| GET | `/budgets/{budgetId}/decomposition` | 分解列表 |
| POST | `/budgets/adjustment` | 预算调整申请 |
| GET | `/budgets/{budgetId}/adjustments` | 调整历史 |

### 任务管理 (`/api/tasks`)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/tasks` | 创建减排任务 |
| PUT | `/tasks/{id}` | 更新任务 |
| DELETE | `/tasks/{id}` | 删除任务 |
| GET | `/tasks/{id}` | 任务详情 |
| POST | `/tasks/query` | 分页查询任务 |
| POST | `/tasks/progress` | 上报进度 |
| GET | `/tasks/{taskId}/progress` | 进度历史 |
| POST | `/tasks/attachment` | 添加附件 |
| GET | `/tasks/{taskId}/attachments` | 附件列表 |

### 预警管理 (`/api/warnings`)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/warnings/query` | 分页查询预警 |
| GET | `/warnings/budget/{budgetId}` | 预算相关预警 |
| GET | `/warnings/task/{taskId}` | 任务相关预警 |
| GET | `/warnings/org/{orgId}/unhandled` | 组织未处理预警 |
| POST | `/warnings/{id}/handle` | 处理预警 |

### 报表管理 (`/api/reports`)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/reports/yearly` | 年度汇总报表 |
| GET | `/reports/budget-progress` | 预算进度报表 |
| GET | `/reports/yearly/export` | 导出年度报告 |

### 待办与审批

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/todos/query` | 分页查询待办 |
| POST | `/todos/{id}/read` | 标记已读 |
| POST | `/todos/{id}/complete` | 标记已完成 |
| GET | `/todos/statistics` | 待办统计 |
| POST | `/approvals/{id}/process` | 处理审批 |
| GET | `/approvals/pending` | 我的待审批 |

---

## 枚举值说明

### 排放范围 (emissionScope)
- `SCOPE_1` - 范围1：直接排放
- `SCOPE_2` - 范围2：间接排放（能源）
- `SCOPE_3` - 范围3：其他间接排放

### 预算状态 (status)
- `DRAFT` - 草稿
- `PENDING_APPROVAL` - 待审批
- `APPROVED` - 已审批
- `REJECTED` - 已驳回
- `ADJUSTED` - 已调整
- `ARCHIVED` - 已归档

### 任务类别 (category)
- `ENERGY_SAVING` - 节电节能
- `FUEL_SUBSTITUTION` - 替代燃料
- `GREEN_PROCUREMENT` - 绿色采购
- `PROCESS_OPTIMIZATION` - 工艺优化
- `WASTE_RECYCLING` - 废弃物回收
- `CARBON_SINK` - 碳汇/植树造林
- `OTHER` - 其他

### 任务状态 (status)
- `NOT_STARTED` - 未开始
- `IN_PROGRESS` - 进行中
- `COMPLETED` - 已完成
- `DELAYED` - 已延期
- `CANCELLED` - 已取消

### 预警等级 (warningLevel)
- `NORMAL` - 正常
- `ATTENTION` - 关注
- `WARNING` - 预警
- `DANGER` - 严重

---

## 切换到 MySQL

修改 `src/main/resources/application.yml`:

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

## 注意事项

1. **JDK 版本**: 需要 JDK 17 或更高版本
2. **数据存储**: 默认使用 H2 文件数据库，数据存放在 `./data/` 目录
3. **端口配置**: 默认 8080，可在 `application.yml` 中修改
4. **演示数据**: 首次启动会自动创建演示数据（1条年度预算、3条分解记录、4条任务）
5. **重置数据**: 删除 `data/` 目录重新启动即可重置
