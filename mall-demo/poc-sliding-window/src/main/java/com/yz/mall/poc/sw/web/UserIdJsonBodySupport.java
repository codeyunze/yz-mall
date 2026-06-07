package com.yz.mall.poc.sw.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 从 JSON 根节点解析滑动窗口所需字段（与 {@link com.yz.mall.poc.sw.dto.TestARequest} 字段名一致；不含 clientId，clientId 由请求头提供）。
 */
public final class UserIdJsonBodySupport {

    private UserIdJsonBodySupport() {}

    public static UserIdPayload parse(JsonNode body) {
        if (body == null || body.isNull()) {
            throw new ResponseStatusException(BAD_REQUEST, "请求体不能为空");
        }
        List<String> userIds = readUserIds(body.get("userIds"));
        int maxDistinctUserIds = readPositiveInt(body.get("maxUserIdCount"), "maxUserIdCount");
        int windowSeconds = readPositiveInt(body.get("timeWindow"), "timeWindow");
        return new UserIdPayload(userIds, maxDistinctUserIds, windowSeconds);
    }

    private static List<String> readUserIds(JsonNode node) {
        if (node == null || node.isNull()) {
            return List.of();
        }
        if (!node.isArray()) {
            throw new ResponseStatusException(BAD_REQUEST, "userIds 须为 JSON 数组");
        }
        List<String> out = new ArrayList<>(node.size());
        for (JsonNode e : node) {
            if (e == null || e.isNull()) {
                continue;
            }
            if (e.isTextual()) {
                String t = e.asText();
                if (!t.isBlank()) {
                    out.add(t);
                }
            } else if (e.isNumber()) {
                out.add(e.asText());
            } else {
                throw new ResponseStatusException(BAD_REQUEST, "userIds 元素须为字符串或数字");
            }
        }
        return out;
    }

    private static int readPositiveInt(JsonNode node, String fieldName) {
        if (node == null || node.isNull()) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " 不能为空");
        }
        int v;
        if (node.isInt() || node.isLong()) {
            v = node.intValue();
        } else if (node.isTextual()) {
            try {
                v = Integer.parseInt(node.asText().strip());
            } catch (NumberFormatException ex) {
                throw new ResponseStatusException(BAD_REQUEST, fieldName + " 须为正整数");
            }
        } else {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " 须为数字");
        }
        if (v <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " 须为正整数");
        }
        return v;
    }

    /**
     * 过滤器从 JSON 解析出的滑动窗口入参（不含 clientId）。
     */
    public static final class UserIdPayload {

        private final List<String> userIds;
        private final int maxUserIdCount;
        private final int timeWindow;

        public UserIdPayload(List<String> userIds, int maxUserIdCount, int timeWindow) {
            this.userIds = userIds == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(userIds));
            this.maxUserIdCount = maxUserIdCount;
            this.timeWindow = timeWindow;
        }

        public List<String> getUserIds() {
            return userIds;
        }

        public int getMaxUserIdCount() {
            return maxUserIdCount;
        }

        public int getTimeWindow() {
            return timeWindow;
        }
    }
}
