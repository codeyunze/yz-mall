#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: POST /tw/telemetry/dev/raw/ingest — 正例模拟三写 — 节点4

环境变量：BASE_URL（必填）、TOKEN（可选）、TW_TEST_VIN（可选，默认 TESTVIN001）
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

    url = f"{base}/tw/telemetry/dev/raw/ingest"
    payload = {
        "vin": vin,
        "lng": 116.397128,
        "lat": 39.916527,
        "speed": 36.5,
        "heading": 90.0,
        "altitude": 12.0,
        "gpsTime": "2026-09-25T10:00:01.000+08:00",
        "soc": 78.5,
        "signalLevel": 4,
    }
    resp = requests.post(url, json=payload, headers=headers, timeout=30)
    if resp.status_code != 200:
        print(f"FAIL http={resp.status_code} body={resp.text[:500]}")
        return 1
    body = resp.json()
    if body.get("code") != 200:
        print(f"FAIL bizCode={body.get('code')} msg={body.get('msg')}")
        return 1
    print("PASS ok raw ingest")
    return 0


if __name__ == "__main__":
    sys.exit(main())
