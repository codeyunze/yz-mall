# 租户 schema 增量迁移脚本目录

将待执行脚本按目录放置。应用启动加载动态数据源后会自动扫描并执行；新租户 `initDb` 成功后也会补跑。

## 命名约定

```text
sql/tenant-migrate/common/              # 公共：所有启用服务数据源都执行
  VyyyyMMdd_NNN__描述.sql
  VyyyyMMdd_NNN__描述.rollback.sql

sql/tenant-migrate/{serviceCode}/       # 仅该 serviceCode 的启用数据源执行
  VyyyyMMdd_NNN__描述.sql
  VyyyyMMdd_NNN__描述.rollback.sql
```

- 正向与回滚必须成对；缺少回滚脚本时启动迁移会直接失败。
- 执行顺序：先 `common`，再各 `serviceCode`（按目录名字典序）；同目录内按文件名字典序。
- 允许 DDL 与初始化数据（INSERT 等）。
- 任一数据源执行失败：本轮已成功的全部脚本按逆序回滚，并中止后续迁移。
- 主库日志：`service_code` 记实际数据源服务；`common` 脚本的 `script_name` 带 `common/` 前缀，避免与服务目录同名脚本冲突。

## 示例

```text
sql/tenant-migrate/common/
  V20260825_001__createTableDemo.sql
  V20260825_001__createTableDemo.rollback.sql

sql/tenant-migrate/mall-pms/
  V20260825_002__pms_sku_add_remark.sql
  V20260825_002__pms_sku_add_remark.rollback.sql
```

执行记录写入主库表 `saas_schema_migrate_log`（见 `mall-sys-startup/src/main/resources/db/saas_schema_migrate_log.sql`）。
