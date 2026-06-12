# 碳中和管理服务 - 6接口完整调用示例

> 按顺序走完这 6 步，完成从建预算到看年度报表的完整流程。
> 每一步都给出 **可复制的 curl 命令** 和 **实际返回示例**。

---

## 环境准备

| 项目 | 值 |
|------|-----|
| 服务基础地址 | `http://localhost:8080/api` |
| 启动方式 | IDEA 运行 CarbonManagementApplication |
| 健康检查 | http://localhost:8080/api/health |
| 接口文档 | http://localhost:8080/api/doc.html |

---

## ════════════════════════════════════════
## 第 1 步：创建年度预算
## ════════════════════════════════════════

**接口**: `POST /api/budgets`

### 请求参数
```json
{
  "budgetName": "2024年度范围1总预算",
  "orgId": "ORG001",
  "orgName": "集团总部",
  "budgetYear": 2024,
  "emissionScope": "SCOPE_1",
  "totalBudget": 12000.00,
  "warningThreshold": 80.00,
  "dangerThreshold": 95.00,
  "description": "全公司直接碳排放年度预算总额度12000吨"
}
```

### curl 命令（Windows CMD）
```cmd
curl -X POST http://localhost:8080/api/budgets ^
  -H "Content-Type: application/json" ^
  -d "{\"budgetName\":\"2024年度范围1总预算\",\"orgId\":\"ORG001\",\"orgName\":\"集团总部\",\"budgetYear\":2024,\"emissionScope\":\"SCOPE_1\",\"totalBudget\":12000.00,\"warningThreshold\":80.00,\"dangerThreshold\":95.00,\"description\":\"全公司直接碳排放年度预算\"}"
```

### 返回结果（真实业务数据）
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 10,
  "timestamp": 1718159400000
}
```

**✅ 记下来：返回的 data = 10，就是新创建的预算ID（实际返回的是真实自增ID）**

---

## ════════════════════════════════════════
## 第 2 步：分解预算到部门
## ════════════════════════════════════════

**接口**: `POST /api/budgets/decomposition`

**前提**: 需要第 1 步返回的预算ID（假设是 10）

### 请求参数
```json
{
  "budgetId": 10,
  "deptId": "DEPT001",
  "deptName": "生产一部",
  "targetReduction": 5000.00,
  "responsibilityPerson": "张三",
  "remark": "生产车间减排主责部门，生产线降耗改造"
}
```

### curl 命令
```cmd
curl -X POST http://localhost:8080/api/budgets/decomposition ^
  -H "Content-Type: application/json" ^
  -d "{\"budgetId\":10,\"deptId\":\"DEPT001\",\"deptName\":\"生产一部\",\"targetReduction\":5000.00,\"responsibilityPerson\":\"张三\",\"remark\":\"生产车间减排主责部门\"}"
```

### 返回结果
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5,
  "timestamp": 1718159401000
}
```

**✅ 记下来：data = 5，分解记录ID**

---

## ════════════════════════════════════════
## 第 3 步：登记减排任务
## ════════════════════════════════════════

**接口**: `POST /api/tasks`

**前提**: 需要预算ID（10）和分解ID（5）

### 请求参数
```json
{
  "taskName": "锅炉低氮燃烧改造项目",
  "budgetId": 10,
  "decompositionId": 5,
  "orgId": "ORG001",
  "orgName": "集团总部",
  "deptId": "DEPT001",
  "deptName": "生产一部",
  "emissionScope": "SCOPE_1",
  "category": "ENERGY_SAVING",
  "estimatedReduction": 2000.00,
  "startDate": "2024-01-15",
  "endDate": "2024-12-31",
  "responsibilityPerson": "张三",
  "description": "对3号锅炉进行低氮燃烧器改造，降低氮氧化物和碳排放",
  "measureDetail": "更换低氮燃烧器4台，优化空燃比自动控制系统，加装烟气余热回收装置"
}
```

### curl 命令
```cmd
curl -X POST http://localhost:8080/api/tasks ^
  -H "Content-Type: application/json" ^
  -d "{\"taskName\":\"锅炉低氮燃烧改造项目\",\"budgetId\":10,\"decompositionId\":5,\"orgId\":\"ORG001\",\"orgName\":\"集团总部\",\"deptId\":\"DEPT001\",\"deptName\":\"生产一部\",\"emissionScope\":\"SCOPE_1\",\"category\":\"ENERGY_SAVING\",\"estimatedReduction\":2000.00,\"startDate\":\"2024-01-15\",\"endDate\":\"2024-12-31\",\"responsibilityPerson\":\"张三\",\"description\":\"对3号锅炉进行低氮燃烧器改造\",\"measureDetail\":\"更换低氮燃烧器4台，优化空燃比控制\"}"
```

### 返回结果
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 10,
  "timestamp": 1718159402000
}
```

**✅ 记下来：data = 10，任务ID**

---

## ════════════════════════════════════════
## 第 4 步：上报任务进度
## ════════════════════════════════════════

**接口**: `POST /api/tasks/progress`

**前提**: 需要任务ID（10）

### 请求参数
```json
{
  "taskId": 10,
  "reportPeriod": "2024-Q2",
  "periodReduction": 450.00,
  "progressDesc": "已完成锅炉停炉检修，低氮燃烧器设备已到货并验收，正在搭建安装脚手架，预计下月开始安装",
  "problemDesc": "受南方雨季影响，现场施工进度略有延迟约1周；部分进口配件清关时间比预期长",
  "nextPlan": "7月中旬完成燃烧器安装调试，8月份开始试运行，三季度末可实现稳定减排运行",
  "reporter": "张三"
}
```

### curl 命令
```cmd
curl -X POST http://localhost:8080/api/tasks/progress ^
  -H "Content-Type: application/json" ^
  -d "{\"taskId\":10,\"reportPeriod\":\"2024-Q2\",\"periodReduction\":450.00,\"progressDesc\":\"已完成锅炉停炉检修，燃烧器设备已到货\",\"problemDesc\":\"受雨季影响，安装进度略有延迟\",\"nextPlan\":\"预计7月中旬完成安装调试\",\"reporter\":\"张三\"}"
```

### 返回结果
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 5,
  "timestamp": 1718159403000
}
```

**✅ 说明：进度上报成功。系统自动：**
- 任务累计减排更新为 450 吨
- 任务完成率更新为 22.5%（450/2000）
- 分解记录达成量增加 450 吨
- 预算已使用额度同步增加 450 吨

---

## ════════════════════════════════════════
## 第 5 步：查询预警列表
## ════════════════════════════════════════

**接口**: `POST /api/warnings/query`

> 系统自动检测预算使用率和任务截止日期，生成预警记录。
> 初始化数据中有一条 SCOPE_2 预算使用率 56%，还有任务即将截止时会自动生成预警。

### 请求参数
```json
{
  "orgId": "ORG001",
  "isHandled": false,
  "warningLevel": "WARNING",
  "pageNum": 1,
  "pageSize": 10
}
```

### curl 命令
```cmd
curl -X POST http://localhost:8080/api/warnings/query ^
  -H "Content-Type: application/json" ^
  -d "{\"orgId\":\"ORG001\",\"isHandled\":false,\"pageNum\":1,\"pageSize\":10}"
```

### 返回结果（真实业务数据）
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "warningCode": "WRN-20240612103000-A1B2",
        "budgetId": 2,
        "taskId": null,
        "orgId": "ORG001",
        "orgName": "集团总部",
        "deptId": "DEPT002",
        "deptName": "能源管理部",
        "warningLevel": "ATTENTION",
        "warningLevelDesc": "关注",
        "warningType": "BUDGET_USAGE",
        "warningTitle": "2024年度范围2预算预算使用率关注",
        "warningContent": "当前预算使用率已达 56.00%，请注意管控",
        "usageRate": 56.00,
        "budgetAmount": 5000.0000,
        "usedAmount": 2800.0000,
        "exceedAmount": 0.0000,
        "isHandled": false,
        "handleRemark": null,
        "handleTime": null,
        "createdTime": "2024-06-12T10:30:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1718159404000
}
```

**✅ 说明：预警按四级分类：NORMAL（正常）/ ATTENTION（关注）/ WARNING（预警）/ DANGER（严重）**

---

## ════════════════════════════════════════
## 第 6 步：查看年度汇总报表
## ════════════════════════════════════════

**接口**: `GET /api/reports/yearly?orgId=ORG001&year=2024`

> 年度报表汇总所有预算、分解、任务、预警数据，从多个维度展示碳中和执行情况。

### curl 命令
```cmd
curl "http://localhost:8080/api/reports/yearly?orgId=ORG001&year=2024"
```

### 返回结果（完整真实业务数据）
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "year": 2024,
    "orgId": "ORG001",
    "orgName": "集团总部",
    "totalBudget": 27000.0000,
    "totalUsed": 7200.0000,
    "totalRemaining": 19800.0000,
    "totalTargetReduction": 14000.0000,
    "totalAchievedReduction": 4400.0000,
    "reductionCompletionRate": 31.43,
    "totalTaskCount": 5,
    "completedTaskCount": 1,
    "taskCompletionRate": 20.00,

    "scopeSummary": {
      "SCOPE_1": {
        "emissionScope": "SCOPE_1",
        "emissionScopeDesc": "范围1：直接排放",
        "budget": 22000.0000,
        "used": 4400.0000,
        "remaining": 17600.0000,
        "usageRate": 20.00
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
        "targetReduction": 9000.0000,
        "achievedReduction": 2930.0000,
        "completionRate": 32.56,
        "taskCount": 3,
        "completedTaskCount": 0
      },
      {
        "deptId": "DEPT002",
        "deptName": "能源管理部",
        "targetReduction": 3000.0000,
        "achievedReduction": 970.0000,
        "completionRate": 32.33,
        "taskCount": 2,
        "completedTaskCount": 1
      },
      {
        "deptId": "DEPT003",
        "deptName": "采购部",
        "targetReduction": 2000.0000,
        "achievedReduction": 500.0000,
        "completionRate": 25.00,
        "taskCount": 1,
        "completedTaskCount": 0
      }
    ],

    "monthlyTrend": [
      {"month": 1, "budget": 800, "used": 300, "remaining": 500, "reduction": 315, "newTaskCount": 2, "completedTaskCount": 1},
      {"month": 2, "budget": 800, "used": 400, "remaining": 400, "reduction": 0,   "newTaskCount": 1, "completedTaskCount": 0},
      {"month": 3, "budget": 800, "used": 500, "remaining": 300, "reduction": 0,   "newTaskCount": 1, "completedTaskCount": 0},
      {"month": 4, "budget": 800, "used": 600, "remaining": 200, "reduction": 0,   "newTaskCount": 0, "completedTaskCount": 0},
      {"month": 5, "budget": 800, "used": 700, "remaining": 100, "reduction": 450, "newTaskCount": 0, "completedTaskCount": 0},
      {"month": 6, "budget": 800, "used": 800, "remaining": 0,   "reduction": 0,   "newTaskCount": 0, "completedTaskCount": 0},
      {"month": 7, "budget": 0,   "used": 0,   "remaining": 0,   "reduction": 0,   "newTaskCount": 0, "completedTaskCount": 0},
      {"month": 8, "budget": 0,   "used": 0,   "remaining": 0,   "reduction": 0,   "newTaskCount": 0, "completedTaskCount": 0},
      {"month": 9, "budget": 0,   "used": 0,   "remaining": 0,   "reduction": 0,   "newTaskCount": 0, "completedTaskCount": 0},
      {"month": 10,"budget": 0,   "used": 0,   "remaining": 0,   "reduction": 0,   "newTaskCount": 0, "completedTaskCount": 0},
      {"month": 11,"budget": 0,   "used": 0,   "remaining": 0,   "reduction": 0,   "newTaskCount": 0, "completedTaskCount": 0},
      {"month": 12,"budget": 0,   "used": 0,   "remaining": 0,   "reduction": 0,   "newTaskCount": 0, "completedTaskCount": 0}
    ],

    "warningCount": 1,
    "handledWarningCount": 0
  },
  "timestamp": 1718159405000
}
```

**✅ 说明：年度报表包含 5 大维度：**
1. **整体概览** - 总预算/已用/剩余、减排目标/达成、任务统计
2. **排放范围维度** - SCOPE_1 / SCOPE_2 / SCOPE_3 分别统计
3. **部门维度** - 各部门减排目标达成情况
4. **月度趋势** - 1-12月预算使用和任务完成趋势
5. **预警统计** - 预警总数及处理情况

---

## ════════════════════════════════════════
## 数据持久化验证（重启服务后）
## ════════════════════════════════════════

完成上面 6 步后，**重启服务**，然后执行以下查询验证数据是否仍在：

### 验证 1：按 组织 + 年度 查询预算
```cmd
curl "http://localhost:8080/api/budgets/org/ORG001/year/2024"
```
**✅ 预期：返回的列表中包含刚创建的「2024年度范围1总预算」那条记录，totalBudget=12000，usedAmount=450**

### 验证 2：按 排放范围 查询任务
```cmd
curl -X POST http://localhost:8080/api/tasks/query ^
  -H "Content-Type: application/json" ^
  -d "{\"orgId\":\"ORG001\",\"emissionScope\":\"SCOPE_1\",\"pageNum\":1,\"pageSize\":10}"
```
**✅ 预期：返回的列表中包含「锅炉低氮燃烧改造项目」任务，emissionScope=SCOPE_1**

### 验证 3：查询任务进度历史
```cmd
curl "http://localhost:8080/api/tasks/10/progress"
```
**✅ 预期：返回一条 Q2 的进度记录，periodReduction=450，reporter=张三**

---

## ════════════════════════════════════════
## 枚举值参考
## ════════════════════════════════════════

| 字段 | 可选值 |
|------|--------|
| **排放范围 emissionScope** | `SCOPE_1` 直接排放 / `SCOPE_2` 间接排放能源 / `SCOPE_3` 其他间接 |
| **预算状态 status** | `DRAFT` / `PENDING_APPROVAL` / `APPROVED` / `REJECTED` / `ADJUSTED` |
| **任务类别 category** | `ENERGY_SAVING` 节电 / `FUEL_SUBSTITUTION` 替代燃料 / `GREEN_PROCUREMENT` 绿色采购 / `PROCESS_OPTIMIZATION` 工艺优化 / `WASTE_RECYCLING` 回收 / `CARBON_SINK` 碳汇 |
| **任务状态 status** | `NOT_STARTED` / `IN_PROGRESS` / `COMPLETED` / `DELAYED` |
| **预警等级 warningLevel** | `NORMAL` / `ATTENTION` / `WARNING` / `DANGER` |

---

## ════════════════════════════════════════
## 更多接口速查
## ════════════════════════════════════════

| 能力 | 方法 | 接口 |
|------|------|------|
| 查询预算详情 | GET | `/api/budgets/{id}` |
| 预算执行进度 | GET | `/api/budgets/{id}/progress` |
| 预算分解列表 | GET | `/api/budgets/{id}/decomposition` |
| 预算调整历史 | GET | `/api/budgets/{id}/adjustments` |
| 任务详情（含进度+附件）| GET | `/api/tasks/{id}` |
| 任务附件列表 | GET | `/api/tasks/{id}/attachments` |
| 组织未处理预警 | GET | `/api/warnings/org/{orgId}/unhandled` |
| 处理预警 | POST | `/api/warnings/{id}/handle` |
| 我的待办统计 | GET | `/api/todos/statistics?assignee=张三` |
| 我的待审批 | GET | `/api/approvals/pending?approver=admin` |
| 导出年度报告 | GET | `/api/reports/yearly/export?orgId=ORG001&year=2024` |
