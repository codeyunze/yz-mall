#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: POST /emqx/auth — 正例（dev-bypass 下非空账号放行）

环境变量：BASE_URL（必填）
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

    url = f"{base}/emqx/auth"
    payload = {
        "clientid": "TESTVIN001",
        "username": "TESTVIN001",
        "password": "dev",
    }
    resp = requests.post(url, json=payload, timeout=15)
    if resp.status_code != 200:
        print(f"FAIL http={resp.status_code} body={resp.text[:500]}")
        return 1
    body = resp.json()
    if body.get("result") != "allow":
        print(f"FAIL result={body}")
        return 1
    print("PASS ok emqx auth")
    return 0


if __name__ == "__main__":
    sys.exit(main())
