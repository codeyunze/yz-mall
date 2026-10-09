#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: POST /emqx/bridge/gps — 正例（需 Kafka 可用）

环境变量：BASE_URL（必填）、TW_TEST_VIN（可选）
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
    vin = os.environ.get("TW_TEST_VIN", "TESTVIN001").strip()

    url = f"{base}/emqx/bridge/gps"
    payload = {
        "topic": f"tsp/{vin}/up/gps",
        "clientid": vin,
        "username": vin,
        "payload": {
            "vin": vin,
            "lng": 116.397128,
            "lat": 39.916527,
            "speed": 36.5,
            "gpsTime": "2026-09-26T17:00:01.000+08:00",
        },
    }
    resp = requests.post(url, json=payload, timeout=20)
    if resp.status_code != 200:
        print(f"FAIL http={resp.status_code} body={resp.text[:500]}")
        return 1
    body = resp.json()
    if not body.get("sent"):
        print(f"FAIL sent=false body={body}")
        return 1
    print("PASS ok bridge gps")
    return 0


if __name__ == "__main__":
    sys.exit(main())
