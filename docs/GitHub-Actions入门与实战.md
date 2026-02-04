# GitHub Actions 入门与实战

本文介绍 GitHub Actions 的基本概念和用法，并结合本仓库中的「数据库备份 + SCP 上传」工作流做实战说明。

---

## 一、什么是 GitHub Actions

GitHub Actions 是 GitHub 提供的 **CI/CD（持续集成/持续部署）** 能力：

- **CI**：代码推送/合并时自动跑测试、构建、检查
- **CD**：通过脚本自动部署到服务器、发版、上传制品

特点：配置写在仓库里的 YAML 文件，和代码一起版本管理；支持定时任务、手动触发；可复用他人写好的“动作”（Action）。

---

## 二、核心概念

| 概念 | 说明 |
|------|------|
| **Workflow（工作流）** | 一个完整的自动化流程，对应一个 YAML 文件 |
| **Event（事件）** | 触发工作流执行的条件，如 push、定时、手动 |
| **Job（任务）** | 工作流里的一“大块”，可包含多个 Step，默认并行 |
| **Step（步骤）** | Job 内按顺序执行的一步，可执行命令或使用 Action |
| **Runner（运行器）** | 执行 Job 的机器，GitHub 提供 Ubuntu/Windows/macOS 等 |
| **Action（动作）** | 可复用的脚本/插件，如 `actions/checkout@v4` |

关系可以理解为：**Event 触发 → 运行 Workflow → 执行 Job(s) → 每个 Job 里按顺序执行 Step(s)**。

---

## 三、工作流文件放哪里

- 路径：**`.github/workflows/`**
- 格式：**`.yml` 或 `.yaml`**
- 一个仓库可以有多个 workflow 文件，每个文件是一个独立工作流。

示例目录结构：

```
你的仓库/
├── .github/
│   └── workflows/
│       ├── ci.yml          # 例如：测试 + 构建
│       └── backup.yml      # 例如：数据库备份
├── src/
└── ...
```

---

## 四、Workflow 文件基本结构

```yaml
name: 工作流显示名称          # 在 Actions 页面看到的名称

on:                         # 触发条件
  push:
    branches: [ main ]
  workflow_dispatch:         # 允许在网页上手动点“运行”

jobs:
  任务ID:                    # 如 build、test、deploy
    runs-on: ubuntu-latest   # 跑在哪种机器上
    steps:
      - name: 步骤名称
        uses: actions/checkout@v4

      - name: 执行命令
        run: |
          echo "hello"
          npm install
```

- **`on`**：什么情况下跑（事件）。
- **`jobs.xxx`**：定义一个任务；多个 job 默认并行，需要顺序时用 `needs`。
- **`runs-on`**：用 GitHub 提供的哪种 Runner（如 `ubuntu-latest`）。
- **`steps`**：步骤列表；`uses` 用现成 Action，`run` 执行 shell 命令。

---

## 五、常用触发事件（on）

```yaml
on:
  push:                      # 推代码时
    branches: [ main, dev ]
  pull_request:              # 提 PR 时
    branches: [ main ]
  schedule:                  # 定时（UTC 时间）
    - cron: '0 2 * * *'      # 每天 02:00
  workflow_dispatch:         # 在网页上手动运行
```

- **cron** 使用 UTC，例如北京时间 10:00 = UTC 02:00。
- 多个事件写在一起时，满足任一即可触发。

---

## 六、Steps 里写什么

### 1. 用现成 Action：`uses`

```yaml
- uses: actions/checkout@v4           # 拉取当前仓库代码
- uses: actions/setup-node@v4         # 安装 Node
  with:
    node-version: '20'
```

- `@v4` 表示使用该 Action 的 v4 版本，建议写死版本号，避免被破坏性更新影响。

### 2. 自己写命令：`run`

```yaml
- name: 安装依赖
  run: npm ci

- name: 多行命令
  run: |
    echo "step1"
    npm run build
    echo "step2"
```

- **`name`** 会显示在 Actions 日志里，方便排查。

### 3. 环境变量与 Secrets

```yaml
- name: 使用环境变量
  env:
    MY_VAR: 值
    PASSWORD: ${{ secrets.MY_SECRET }}
  run: echo "var is $MY_VAR"
```

- **`secrets.XXX`**：在仓库 Settings → Secrets and variables → Actions 里配置，用于密码、密钥等敏感信息，日志里会被脱敏。

---

## 七、本仓库实战：数据库备份 + SCP 上传

本仓库中已有一个完整示例：**`.github/workflows/mysql-backup-upload.yml`**。

### 1. 作用

- 在 GitHub 提供的 Ubuntu 环境里执行 **MySQL 备份脚本**。
- 将生成的备份目录通过 **SCP** 上传到你指定的服务器。

### 2. 触发方式

- **手动**：仓库页 → Actions → 选择 “MySQL Backup & SCP Upload” → Run workflow。
- **定时**：默认每天 UTC 02:00（可在 YAML 里改 `schedule`）。

### 3. 需要配置的 Secrets

在 **Settings → Secrets and variables → Actions** 中新增：

| Secret | 说明 |
|--------|------|
| `DB_HOST` | 数据库主机 |
| `DB_PORT` | 数据库端口 |
| `DB_USER` | 数据库用户 |
| `DB_PASS` | 数据库密码 |
| `DB_NAME` | 要备份的库名 |
| `SCP_HOST` | 上传目标服务器地址 |
| `SCP_USER` | 目标机 SSH 用户名 |
| `SCP_REMOTE_DIR` | 目标机目录（如 `/data/backup/mysql`） |
| `SCP_SSH_PRIVATE_KEY` | 登录目标机的 SSH 私钥全文 |

可选：`BACKUP_ROOT`（Runner 上备份根目录）、`SCP_PORT`（SSH 端口，默认 22）。

### 4. 流程对应关系

- **Checkout**：拉取仓库（拿到备份脚本）。
- **Install MySQL client**：在 Runner 上装 `mysql`/`mysqldump`。
- **Run backup script**：用 Secrets 调用 `docs/script/mysql-backup-schema-data.sh`，生成带时间戳的备份目录。
- **Setup SSH and upload**：用私钥通过 SCP 把该目录上传到 `SCP_USER@SCP_HOST:SCP_REMOTE_DIR/`。

通过这个文件可以直观看到：**事件 → 多步 Job → 使用 Secrets、run、uses** 的完整用法。

---

## 八、常用技巧

1. **查看日志**：Actions 页点进某次运行 → 点对应 Job → 展开 Step 看输出。
2. **调试**：在步骤里加 `run: env | sort` 可看环境变量（注意不要 `echo` 敏感信息）。
3. **条件执行**：`if: success()`、`if: failure()`、`if: github.ref == 'refs/heads/main'` 等。
4. **多 Job 顺序**：用 `needs: [ 上一个 job 的 id ]` 让当前 job 在上一个成功后再跑。
5. **缓存**：用 `actions/cache` 缓存依赖，加快后续运行。

---

## 九、学习与参考

- [GitHub Actions 官方文档](https://docs.github.com/zh/actions)
- [Workflow 语法](https://docs.github.com/zh/actions/using-workflows/workflow-syntax-for-github-actions)
- [Marketplace 现成 Action](https://github.com/marketplace?type=actions)

建议：先跑通本仓库的 `mysql-backup-upload.yml`，再按需改事件、步骤和 Secrets，即可快速上手 GitHub Actions。
