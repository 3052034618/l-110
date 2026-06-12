# ⚡ 快速启动指南

## 3 步启动项目

### 第 1 步：用 IDEA 打开项目
```
打开 IntelliJ IDEA → File → Open → 选择 110 目录 → 等待 Maven 加载依赖
```

### 第 2 步：启动服务
找到文件：
```
src/main/java/com/carbon/management/CarbonManagementApplication.java
```
右键 → **Run 'CarbonManagementApplication'**

看到下面这行就说明启动成功：
```
Started CarbonManagementApplication in X.XXX seconds
```

### 第 3 步：验证
打开浏览器访问：
- ✅ 健康检查: http://localhost:8080/api/health
- 📖 接口文档: http://localhost:8080/api/doc.html
- 🗄️  数据库台: http://localhost:8080/api/h2-console
  - JDBC URL: `jdbc:h2:file:./data/carbon_db`
  - 用户名: `sa`
  - 密码: `sa`

---

## 🚀 一键跑通完整流程

服务启动后，**双击运行** `test-flow.bat`

或者在 PowerShell 中执行：
```powershell
powershell -ExecutionPolicy Bypass -File .\test-flow.ps1
```

**自动完成以下 6 个步骤**：
1. ✅ 创建年度预算
2. ✅ 预算分解到部门
3. ✅ 登记减排任务
4. ✅ 上报任务进度
5. ✅ 查询预算进度 + 预警列表
6. ✅ 查询年度汇总报表

---

## 📋 目录中各文件说明

| 文件 | 用途 |
|------|------|
| `README.md` | 完整使用文档（推荐先看） |
| `API-EXAMPLES.md` | **6条接口调用示例 + curl命令 + 真实返回** |
| `QUICKSTART.md` | 本文档，快速启动 |
| `start.bat` | 启动服务（需要安装 Maven） |
| `test-flow.bat` | **一键测试6条主流程接口** |
| `test-flow.ps1` | 一键测试脚本（PowerShell完整版） |
| `api-test.http` | HTTP测试文件（IDEA/VSCode直接运行） |
| `postman-collection.json` | Postman接口集合（导入即可用） |
| `pom.xml` | Maven 配置 |
| `src/main/resources/application.yml` | 配置文件 |

---

## 💡 小贴士

- **数据保存位置**: `110/data/` 目录，删除即可重置数据
- **首次启动自动初始化**: 10条预算/分解/任务演示数据
- **遇到问题先看**: http://localhost:8080/api/health 是否正常返回
- **在线调试接口**: http://localhost:8080/api/doc.html 可以直接填参数调用
