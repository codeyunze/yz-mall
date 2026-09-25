#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: POST /tw/telemetry/track — 反例(无Token) — 节点3"""
from __future__ import annotations

import os
import sys

sys.path.insert(0, os.path.dirname(__file__))
from _common import base_url, biz_code  # noqa: E402


def main() -> int:
    try:
        import requests
    except ImportError:
        print("FAIL: please pip install requests")
        return 2

    expect_code = int(os.environ.get("EXPECT_CODE", "50000"))
    url = f"{base_url()}/tw/telemetry/track"
    payload = {
        "vin": "TESTVIN001",
        "startTime": "2026-09-25 00:00:00",
        "endTime": "2026-09-25 12:00:00",
    }
    resp = requests.post(url, json=payload, headers={}, timeout=30)
    try:
        data = resp.json()
    except Exception:
        data = {}
    code = biz_code(data)
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
