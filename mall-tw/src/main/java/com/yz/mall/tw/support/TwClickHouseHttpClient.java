package com.yz.mall.tw.support;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.tw.config.TwTelemetryProperties;
import com.yz.mall.tw.dto.TwGpsTrackPointDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ClickHouse HTTP 客户端：批量 INSERT / 按 VIN+时间窗查询。
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "tw.telemetry.clickhouse", name = "enabled", havingValue = "true")
public class TwClickHouseHttpClient {

    private static final DateTimeFormatter CH_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String TABLE = "tw_gps_track";

    private final HttpClient httpClient;
    private final TwTelemetryProperties properties;
    private final ObjectMapper objectMapper;
    private final String baseHttpUrl;
    private final String database;

    public TwClickHouseHttpClient(@Qualifier("clickHouseHttpClient") HttpClient httpClient, TwTelemetryProperties properties) {
        this.httpClient = httpClient;
        this.properties = properties;
        this.objectMapper = JacksonUtil.getObjectMapper();
        ParsedJdbc parsed = parseJdbc(properties.getClickhouse().getUrl());
        this.baseHttpUrl = parsed.baseHttpUrl();
        this.database = parsed.database();
    }

    /**
     * 批量追加轨迹点。
     *
     * @param points 点列表
     */
    public void batchInsert(List<TwGpsTrackPointDto> points) {
        if (points == null || points.isEmpty()) {
            return;
        }
        StringBuilder body = new StringBuilder(points.size() * 160);
        body.append("INSERT INTO ").append(TABLE)
                .append(" (vin,vehicle_id,lng,lat,altitude,speed,heading,gps_time,receive_time,soc,signal_level) FORMAT JSONEachRow\n");
        try {
            for (TwGpsTrackPointDto p : points) {
                body.append(objectMapper.writeValueAsString(toRow(p))).append('\n');
            }
        } catch (Exception ex) {
            throw new BusinessException("构建 ClickHouse 写入报文失败: " + ex.getMessage());
        }
        execute(body.toString());
    }

    /**
     * 按 VIN + 时间窗查询，按 gps_time 升序。
     *
     * @param vin       车架号
     * @param startTime 开始（含）
     * @param endTime   结束（含）
     * @return 轨迹点
     */
    public List<TwGpsTrackPointDto> queryByVinAndTime(String vin, LocalDateTime startTime, LocalDateTime endTime) {
        String sql = "SELECT vin, vehicle_id, lng, lat, altitude, speed, heading, gps_time, receive_time, soc, signal_level "
                + "FROM " + TABLE + " WHERE vin = {vin:String} AND gps_time >= {start:DateTime64(3)} AND gps_time <= {end:DateTime64(3)} "
                + "ORDER BY gps_time FORMAT JSONEachRow";
        String queryUrl = baseHttpUrl + "/?database=" + encode(database)
                + "&param_vin=" + encode(vin)
                + "&param_start=" + encode(startTime.format(CH_DT))
                + "&param_end=" + encode(endTime.format(CH_DT));
        String raw = executeQuery(queryUrl, sql);
        return parseJsonEachRow(raw);
    }

    private Map<String, Object> toRow(TwGpsTrackPointDto p) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("vin", p.getVin());
        row.put("vehicle_id", p.getVehicleId() == null ? 0L : p.getVehicleId());
        row.put("lng", p.getLng());
        row.put("lat", p.getLat());
        row.put("altitude", p.getAltitude());
        row.put("speed", p.getSpeed());
        row.put("heading", p.getHeading());
        row.put("gps_time", p.getGpsTime() == null ? null : p.getGpsTime().format(CH_DT));
        row.put("receive_time", p.getReceiveTime() == null ? null : p.getReceiveTime().format(CH_DT));
        row.put("soc", p.getSoc());
        row.put("signal_level", p.getSignalLevel());
        return row;
    }

    private void execute(String sqlBody) {
        String url = baseHttpUrl + "/?database=" + encode(database);
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).timeout(java.time.Duration.ofSeconds(30)).POST(HttpRequest.BodyPublishers.ofString(sqlBody, StandardCharsets.UTF_8));
            applyAuth(builder);
            HttpResponse<String> resp = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 300) {
                throw new BusinessException("ClickHouse 写入失败: HTTP " + resp.statusCode() + " " + truncate(resp.body()));
            }
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("ClickHouse 写入异常: " + ex.getMessage());
        }
    }

    private String executeQuery(String url, String sql) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).timeout(java.time.Duration.ofSeconds(30)).POST(HttpRequest.BodyPublishers.ofString(sql, StandardCharsets.UTF_8));
            applyAuth(builder);
            HttpResponse<String> resp = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 300) {
                throw new BusinessException("ClickHouse 查询失败: HTTP " + resp.statusCode() + " " + truncate(resp.body()));
            }
            return resp.body() == null ? "" : resp.body();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("ClickHouse 查询异常: " + ex.getMessage());
        }
    }

    private void applyAuth(HttpRequest.Builder builder) {
        TwTelemetryProperties.Clickhouse ch = properties.getClickhouse();
        String user = StrUtil.blankToDefault(ch.getUsername(), "default");
        String pass = ch.getPassword() == null ? "" : ch.getPassword();
        String token = java.util.Base64.getEncoder().encodeToString((user + ":" + pass).getBytes(StandardCharsets.UTF_8));
        builder.header("Authorization", "Basic " + token);
    }

    private List<TwGpsTrackPointDto> parseJsonEachRow(String raw) {
        List<TwGpsTrackPointDto> list = new ArrayList<>();
        if (StrUtil.isBlank(raw)) {
            return list;
        }
        String[] lines = raw.split("\n");
        for (String line : lines) {
            if (StrUtil.isBlank(line)) {
                continue;
            }
            try {
                JsonNode json = objectMapper.readTree(line);
                TwGpsTrackPointDto p = new TwGpsTrackPointDto();
                p.setVin(textOrNull(json, "vin"));
                if (json.hasNonNull("vehicle_id")) {
                    p.setVehicleId(json.get("vehicle_id").asLong());
                }
                p.setLng(toDecimal(json, "lng"));
                p.setLat(toDecimal(json, "lat"));
                p.setAltitude(toDecimal(json, "altitude"));
                p.setSpeed(toDecimal(json, "speed"));
                p.setHeading(toDecimal(json, "heading"));
                p.setGpsTime(parseDt(textOrNull(json, "gps_time")));
                p.setReceiveTime(parseDt(textOrNull(json, "receive_time")));
                p.setSoc(toDecimal(json, "soc"));
                if (json.hasNonNull("signal_level")) {
                    p.setSignalLevel(json.get("signal_level").asInt());
                }
                list.add(p);
            } catch (Exception ex) {
                log.warn("解析 ClickHouse JSONEachRow 失败: {}", ex.getMessage());
            }
        }
        return list;
    }

    private static String textOrNull(JsonNode json, String key) {
        JsonNode node = json.get(key);
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }

    private static BigDecimal toDecimal(JsonNode json, String key) {
        JsonNode node = json.get(key);
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isNumber()) {
            return node.decimalValue();
        }
        String text = node.asText();
        if (StrUtil.isBlank(text)) {
            return null;
        }
        return new BigDecimal(text);
    }

    private static LocalDateTime parseDt(String raw) {
        if (StrUtil.isBlank(raw)) {
            return null;
        }
        String s = raw.trim();
        if (s.length() == 19) {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (s.length() >= 23) {
            return LocalDateTime.parse(s.substring(0, 23), CH_DT);
        }
        return LocalDateTime.parse(s);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    private static String truncate(String body) {
        if (body == null) {
            return "";
        }
        return body.length() > 200 ? body.substring(0, 200) : body;
    }

    /**
     * 解析 {@code jdbc:clickhouse://host:port/db} 为 HTTP 基址与库名。
     */
    static ParsedJdbc parseJdbc(String jdbcUrl) {
        String url = StrUtil.blankToDefault(jdbcUrl, "jdbc:clickhouse://127.0.0.1:8123/tw");
        String stripped = url;
        if (stripped.startsWith("jdbc:clickhouse://")) {
            stripped = stripped.substring("jdbc:clickhouse://".length());
        } else if (stripped.startsWith("jdbc:ch://")) {
            stripped = stripped.substring("jdbc:ch://".length());
        } else if (stripped.startsWith("http://") || stripped.startsWith("https://")) {
            URI uri = URI.create(stripped);
            String path = uri.getPath();
            String db = "tw";
            if (StrUtil.isNotBlank(path) && path.length() > 1) {
                db = path.substring(1).replace("/", "");
            }
            String base = uri.getScheme() + "://" + uri.getHost() + (uri.getPort() > 0 ? ":" + uri.getPort() : "");
            return new ParsedJdbc(base, db);
        }
        int slash = stripped.indexOf('/');
        String hostPort = slash >= 0 ? stripped.substring(0, slash) : stripped;
        String db = slash >= 0 && slash + 1 < stripped.length() ? stripped.substring(slash + 1) : "tw";
        if (db.contains("?")) {
            db = db.substring(0, db.indexOf('?'));
        }
        return new ParsedJdbc("http://" + hostPort, db);
    }

    record ParsedJdbc(String baseHttpUrl, String database) {
    }
}
