#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: GET /tw/telemetry/latest — 正例 — 节点2 / G4"""
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

    vin = os.environ.get("TW_TEST_VIN", "").strip()
    if not vin:
        print("FAIL: TW_TEST_VIN is required")
        return 2

    url = f"{base_url()}/tw/telemetry/latest"
    resp = requests.get(url, params={"vin": vin}, headers=auth_headers(), timeout=30)
    if resp.status_code != 200:
        print(f"FAIL http={resp.status_code} body={resp.text[:500]}")
        return 1
    body = resp.json()
    if biz_code(body) != 200:
        print(f"FAIL bizCode={body.get('code')} msg={body.get('msg')}")
        return 1
    print("PASS ok latest")
    return 0


if __name__ == "__main__":
    sys.exit(main())
