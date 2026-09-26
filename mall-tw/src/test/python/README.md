# mall-tw 遥测 API 脚本

**硬性约定：每个 `test_*.py` 必须可独立运行**——禁止 `import` 同目录其它 `.py`；参数一律环境变量。

完整 T1～T9 对照与 curl 见：  
`.cursor/harness/runs/20260922-tw-telemetry/VERIFY_T1_T9.md`

## 环境变量

| 变量 | 说明 |
|------|------|
| `BASE_URL` | 如 `http://127.0.0.1:5005` |
| `TOKEN` | 可选 `Bearer xxx` |
| `TW_TEST_VIN` | 测试 VIN |
| `EXPECT_CODE` | 反例期望码 |

## 单跑

```powershell
$env:BASE_URL = "http://127.0.0.1:5005"
$env:TOKEN = "Bearer xxx"
$env:TW_TEST_VIN = "TESTVIN001"
python test_raw_ingest_ok.py
```
