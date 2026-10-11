#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: POST /tw/telemetry/track — 反例(时间窗过大) — 节点3

环境变量：BASE_URL（必填）、TOKEN（可选）、TW_TEST_VIN（可选）、EXPECT_CODE（默认 1）
依赖：仅标准库 + requests
"""
from __future__ import annotations

import os
import sys


def main() -> int:
    try:
        import requests
    except ImportError:
        print("FAIL: please pip install requests")
        return 2

    base = os.environ.get("BASE_URL", "").rstrip("/")
    if not base:
        print("FAIL: BASE_URL is required")
        return 2
    token = os.environ.get("TOKEN", "")
    headers = {"Authorization": token} if token else {}
    vin = os.environ.get("TW_TEST_VIN", "TESTVIN001").strip()
    expect_code = int(os.environ.get("EXPECT_CODE", "1"))

    url = f"{base}/tw/telemetry/track"
    payload = {
        "vin": vin,
        "startTime": "2026-09-01 00:00:00",
        "endTime": "2026-09-03 00:00:01",
        "maxPoints": 100,
    }
    resp = requests.post(url, json=payload, headers=headers, timeout=30)
    try:
        data = resp.json()
    except Exception:
        data = {}
    code = data.get("code") if isinstance(data, dict) else None
    if code == expect_code:
        print(f"PASS ng: rejected bizCode={code} msg={data.get('msg')}")
        return 0
    print(f"FAIL ng: expected code={expect_code}, got http={resp.status_code} body={resp.text[:500]}")
    return 1


if __name__ == "__main__":
    sys.exit(main())
