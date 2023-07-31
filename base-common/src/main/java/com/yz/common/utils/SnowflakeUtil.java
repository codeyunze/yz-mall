package com.yz.common.utils;

import com.yz.common.Constants;
import com.yz.redistools.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @ClassName SnowFlakeGenerateIdWorker
 * @Description 雪花ID生成
 * @Author yunze
 * @Date 2022/11/18 17:57
 * @Version 1.0
 * 雪花的二进制结构如下
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 符号位 - 时间戳差值 - 数据中心ID - 服务ID - 序列号
 * 符号位: 正数是0，负数是1
 * 时间戳差值: 当前时间戳 减 开始时间戳
 * 数据中心ID: 一个服务器一个ID，0~31
 * 服务ID: 一个服务器有多个服务，一个服务一个ID，0~31
 * 序列号: 同一数据中心，同一服务，同一时间戳下，可产生4096个序列号(0~4095)
 */
@Component
public class SnowflakeUtil {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 雪花ID开始使用时间的时间戳
     * 2022-11-21 00:00:00
     */
    private final long twepoch = 1668995023000L;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 5L;

    /**
     * 数据标识id所占的位数
     */
    private final long datacenterIdBits = 5L;


    /**
     * 工作机器ID(0~31)
     */
    @Value("${base.snowflake.worker-id}")
    private long workerId;

    /**
     * 数据中心ID(0~31)
     */
    @Value("${base.snowflake.datacenter-id:0}")
    private long datacenterId;

    /**
     * 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 支持的最大数据标识id，结果是31
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /**
     * 序列在id中占的位数
     */
    private final long sequenceBits = 12L;

    /**
     * 机器ID向左移12位
     */
    private final long workerIdShift = sequenceBits;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);


    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return long
     */
    public synchronized long generateId() {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }

        long timestamp = timeGen();
        timestamp = generateId(timestamp);
        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    private long generateId(long timestamp) {
        long lastTimestamp = getLastTimestamp();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        // 记录最后一次生成ID的时间截
        setLastTimestamp(timestamp);
        return timestamp;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 将最后一次生成雪花Id的时间存入redis
     *
     * @param lastTimestamp 最后一次生成时间
     */
    public void setLastTimestamp(long lastTimestamp) {
        redisUtil.insertOrUpdate(Constants.SNOW_FLAKE_LAST_TIMESTAMP, lastTimestamp);
    }

    /**
     * 从redis获取到上次生成雪花Id的时间
     *
     * @return 上次生成雪花Id的时间戳
     */
    private long getLastTimestamp() {
        Object select = redisUtil.get(Constants.SNOW_FLAKE_LAST_TIMESTAMP);
        if (StringUtils.isEmpty(select)) {
            return -1L;
        } else {
            return Long.parseLong(String.valueOf(select));
        }
    }
}
