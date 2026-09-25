#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: POST /tw/telemetry/track — 反例(时间窗过大) — 节点3"""
from __future__ import annotations

import os
import sys

sys.path.insert(0, os.path.dirname(__file__))
from _common import auth_headers, base_url, biz_code  # noqa: E402


def main() -> int:
    try:
        import requests
    except ImportError:
        print("FAIL: please pip install requests")
        return 2

    vin = os.environ.get("TW_TEST_VIN", "TESTVIN001").strip()
    expect_code = int(os.environ.get("EXPECT_CODE", "1"))
    url = f"{base_url()}/tw/telemetry/track"
    payload = {
        "vin": vin,
        "startTime": "2026-09-01 00:00:00",
        "endTime": "2026-09-03 00:00:01",
        "maxPoints": 100,
    }
    resp = requests.post(url, json=payload, headers=auth_headers(), timeout=30)
    try:
        data = resp.json()
    except Exception:
        data = {}
    code = biz_code(data)
    if code == expect_code:
        print(f"PASS ng: rejected bizCode={code} msg={data.get('msg')}")
        return 0
    print(f"FAIL ng: expected code={expect_code}, got http={resp.status_code} body={resp.text[:500]}")
    return 1


if __name__ == "__main__":
    sys.exit(main())
