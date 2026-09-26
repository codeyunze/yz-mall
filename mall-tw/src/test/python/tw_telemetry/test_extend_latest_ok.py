#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: GET /extend/tw/telemetry/latest/{vin} — 正例 — 节点2

环境变量：BASE_URL（必填）、TOKEN（可选）、TW_TEST_VIN（必填）
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

    url = f"{base}/extend/tw/telemetry/latest/{vin}"
    resp = requests.get(url, headers=headers, timeout=30)
    if resp.status_code != 200:
        print(f"FAIL http={resp.status_code} body={resp.text[:500]}")
        return 1
    body = resp.json()
    if body.get("code") != 200:
        print(f"FAIL bizCode={body.get('code')} msg={body.get('msg')}")
        return 1
    print("PASS ok extend latest")
    return 0


if __name__ == "__main__":
    sys.exit(main())
