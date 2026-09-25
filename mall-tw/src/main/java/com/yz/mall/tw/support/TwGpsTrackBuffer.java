package com.yz.mall.tw.support;

import com.yz.mall.tw.config.TwTelemetryProperties;
import com.yz.mall.tw.dto.TwGpsTrackPointDto;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 轨迹点本地缓冲：条数 / 定时双触发刷盘，进程关闭 flush。
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "tw.telemetry.clickhouse", name = "enabled", havingValue = "true")
public class TwGpsTrackBuffer {

    private final TwClickHouseHttpClient clickHouseHttpClient;
    private final TwTelemetryProperties properties;
    private final List<TwGpsTrackPointDto> buffer = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public TwGpsTrackBuffer(TwClickHouseHttpClient clickHouseHttpClient, TwTelemetryProperties properties) {
        this.clickHouseHttpClient = clickHouseHttpClient;
        this.properties = properties;
    }

    /**
     * 入队；达到 batchSize 立即刷盘。
     *
     * @param point 轨迹点
     */
    public void offer(TwGpsTrackPointDto point) {
        if (point == null) {
            return;
        }
        int batchSize = Math.max(1, properties.getTrack().getBatchSize());
        List<TwGpsTrackPointDto> toFlush = null;
        lock.lock();
        try {
            buffer.add(point);
            if (buffer.size() >= batchSize) {
                toFlush = drainLocked();
            }
        } finally {
            lock.unlock();
        }
        if (toFlush != null) {
            doFlush(toFlush);
        }
    }

    /**
     * 批量入队。
     *
     * @param points 点列表
     */
    public void offerAll(List<TwGpsTrackPointDto> points) {
        if (points == null || points.isEmpty()) {
            return;
        }
        for (TwGpsTrackPointDto point : points) {
            offer(point);
        }
    }

    /**
     * 强制刷盘。
     */
    public void flush() {
        List<TwGpsTrackPointDto> toFlush;
        lock.lock();
        try {
            toFlush = drainLocked();
        } finally {
            lock.unlock();
        }
        if (toFlush != null) {
            doFlush(toFlush);
        }
    }

    /**
     * 定时刷盘（间隔取自配置，默认 1s）。
     */
    @Scheduled(fixedDelayString = "${tw.telemetry.track.flush-interval-ms:1000}")
    public void scheduledFlush() {
        flush();
    }

    @PreDestroy
    public void onShutdown() {
        flush();
    }

    private List<TwGpsTrackPointDto> drainLocked() {
        if (buffer.isEmpty()) {
            return null;
        }
        List<TwGpsTrackPointDto> drained = new ArrayList<>(buffer);
        buffer.clear();
        return drained;
    }

    private void doFlush(List<TwGpsTrackPointDto> points) {
        try {
            clickHouseHttpClient.batchInsert(points);
            log.debug("ClickHouse 批量写入 {} 条", points.size());
        } catch (Exception ex) {
            // CH 失败不影响 latest；记录错误，点暂丢（节点 4 可接死信）
            log.error("ClickHouse 批量写入失败，丢弃 {} 条: {}", points.size(), ex.getMessage());
        }
    }
}
