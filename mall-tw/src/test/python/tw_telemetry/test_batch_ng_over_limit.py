#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""API: POST /tw/telemetry/latest/batch — 反例(超过200) — 节点2"""
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

    vins = [f"VIN{i:05d}" for i in range(201)]
    url = f"{base_url()}/tw/telemetry/latest/batch"
    resp = requests.post(url, json={"vins": vins}, headers=auth_headers(), timeout=30)
    try:
        data = resp.json()
    except Exception:
        data = {}
    code = biz_code(data)
    # BusinessException → code=1
    expect_code = int(os.environ.get("EXPECT_CODE", "1"))
    if code == expect_code:
        print(f"PASS ng: rejected bizCode={code} msg={data.get('msg')}")
        return 0
    print(f"FAIL ng: expected code={expect_code}, got http={resp.status_code} body={resp.text[:500]}")
    return 1


if __name__ == "__main__":
    sys.exit(main())
