#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: POST /tw/telemetry/track — 正例 — 节点3 / G5

环境变量：BASE_URL（必填）、TOKEN（可选）、TW_TEST_VIN（必填）、TW_TRACK_START/END（可选）
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
    vin = os.environ.get("TW_TEST_VIN", "").strip()
    if not vin:
        print("FAIL: TW_TEST_VIN is required")
        return 2
    token = os.environ.get("TOKEN", "")
    headers = {"Authorization": token} if token else {}
    start = os.environ.get("TW_TRACK_START", "2026-09-25 00:00:00")
    end = os.environ.get("TW_TRACK_END", "2026-09-25 23:59:59")

    url = f"{base}/tw/telemetry/track"
    payload = {"vin": vin, "startTime": start, "endTime": end, "maxPoints": 2000}
    resp = requests.post(url, json=payload, headers=headers, timeout=30)
    if resp.status_code != 200:
        print(f"FAIL http={resp.status_code} body={resp.text[:500]}")
        return 1
    body = resp.json()
    if body.get("code") != 200:
        print(f"FAIL bizCode={body.get('code')} msg={body.get('msg')}")
        return 1
    data = body.get("data") or {}
    if "points" not in data:
        print(f"FAIL missing points field body={resp.text[:300]}")
        return 1
    print(f"PASS ok track total={data.get('total')} sampled={data.get('sampled')}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
