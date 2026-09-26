# mall-tw 遥测 API 脚本

**硬性约定：每个 `test_*.py` 必须可独立运行**——禁止 `import` 同目录其它 `.py`（含 `_common`），禁止依赖额外配置文件；参数一律走环境变量。

第三方依赖仅允许运行时已有的 `requests`（缺失时脚本打印安装提示并退出码 2）。

## 环境变量

| 变量 | 说明 |
|------|------|
| `BASE_URL` | 网关或 mall-tw 根地址，如 `http://127.0.0.1:5005`（多数脚本必填） |
| `TOKEN` | 可选，`Authorization` 完整值，如 `Bearer xxx` |
| `TW_TEST_VIN` | 正例用 VIN |
| `EXPECT_CODE` | 反例期望业务码 |

## 单跑示例

```powershell
$env:BASE_URL = "http://127.0.0.1:5005"
$env:TOKEN = "Bearer xxx"
$env:TW_TEST_VIN = "TESTVIN001"
python yz-mall\mall-tw\src\test\python\tw_telemetry\test_raw_ingest_ok.py
```
