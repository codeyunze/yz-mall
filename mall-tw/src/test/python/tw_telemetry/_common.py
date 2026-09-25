#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""共用：读环境变量、请求头。"""
from __future__ import annotations

import os
from typing import Any, Dict


def base_url() -> str:
    base = os.environ.get("BASE_URL", "").rstrip("/")
    if not base:
        raise SystemExit("FAIL: BASE_URL is required")
    return base


def auth_headers() -> Dict[str, str]:
    token = os.environ.get("TOKEN", "")
    return {"Authorization": token} if token else {}


def biz_code(body: Any) -> Any:
    if isinstance(body, dict):
        return body.get("code")
    return None
