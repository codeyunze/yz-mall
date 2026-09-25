# mall-tw 遥测 API 脚本

环境变量：

| 变量 | 说明 |
|------|------|
| `BASE_URL` | 网关或 mall-tw 根地址，如 `http://127.0.0.1:30001` |
| `TOKEN` | `Authorization` 完整值，如 `Bearer xxx`（运营账号需有位置/批量权限） |
| `TW_TEST_VIN` | 正例用 VIN（建议先经 `/tw/telemetry/dev/ingest` 灌点） |
| `EXPECT_CODE` | 反例期望业务码，默认见各脚本 |

单跑：

```bash
cd yz-mall/mall-tw/src/test/python
pip install -r requirements.txt
export BASE_URL=http://127.0.0.1:30001
export TOKEN='Bearer xxx'
export TW_TEST_VIN=TESTVIN001
python tw_telemetry/test_latest_ok.py
EXPECT_CODE=50000 python tw_telemetry/test_latest_ng_no_token.py
```
