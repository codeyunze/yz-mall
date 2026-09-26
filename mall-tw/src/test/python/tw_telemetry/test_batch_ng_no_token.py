#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: POST /tw/telemetry/latest/batch — 反例(无Token) — 节点2

环境变量：BASE_URL（必填）、EXPECT_CODE（默认 50000）
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
    expect_code = int(os.environ.get("EXPECT_CODE", "50000"))

    url = f"{base}/tw/telemetry/latest/batch"
    resp = requests.post(url, json={"vins": ["TESTVIN001"]}, headers={}, timeout=30)
    try:
        data = resp.json()
    except Exception:
        data = {}
    code = data.get("code") if isinstance(data, dict) else None
    if code == expect_code:
        print(f"PASS ng: rejected bizCode={code} msg={data.get('msg')}")
        return 0
    if resp.status_code in (401, 403) and expect_code in (50000, 50001, 50002, 50003):
        print(f"PASS ng: rejected http={resp.status_code} (auth)")
        return 0
    print(f"FAIL ng: expected code={expect_code}, got http={resp.status_code} body={resp.text[:500]}")
    return 1


if __name__ == "__main__":
    sys.exit(main())
