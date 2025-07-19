package com.yz.mall.serial;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.yz.mall.serial.dto.SerialNumberDto;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 流水号号池
 *
 * @author yunze
 * @date 2024/7/11 星期四 23:31
 */
public class SerialHolder {

    /**
     * 流水号号池
     * Map<流水号前缀, List<流水号>>
     */
    private static ConcurrentHashMap<String, ConcurrentLinkedQueue<SerialNumberDto>> serialNumberPool = new ConcurrentHashMap<>();

    /**
     * 本批次流水号生成时间
     * Map<流水号前缀, 最后一次生成流水号时间>
     */
    private static ConcurrentHashMap<String, LocalDateTime> generateTimes = new ConcurrentHashMap<>();

    /**
     * 获取一个指定前缀的流水号
     * 获取的流水号会从流水号号池删除
     *
     * @param prefix 序列号前缀
     * @return 流水号信息
     */
    public static SerialNumberDto get(String prefix) {
        ConcurrentLinkedQueue<SerialNumberDto> array = serialNumberPool.get(prefix);
        if (CollectionUtils.isEmpty(array)) {
            return null;
        }
        return array.poll();
    }

    /**
     * 获取流水号号池里指定前缀序列号的数量
     *
     * @param prefix 序列号前缀
     * @return 剩余流水号数量
     */
    public static Integer getCount(String prefix) {
        if (!serialNumberPool.containsKey(prefix)) {
            return 0;
        }
        return serialNumberPool.get(prefix).size();
    }

    /**
     * 补充指定前缀的流水号
     *
     * @param prefix 序列号前缀
     * @param serial 流水号
     */
    public synchronized static void add(String prefix, SerialNumberDto serial) {
        if (serialNumberPool.containsKey(prefix)) {
            serialNumberPool.get(prefix).add(serial);
        } else {
            ConcurrentLinkedQueue<SerialNumberDto> array = new ConcurrentLinkedQueue<>();
            array.add(serial);
            serialNumberPool.put(prefix, array);
        }

        generateTimes.put(prefix, LocalDateTime.now());
    }

    public synchronized static void cleanAll() {
        serialNumberPool.clear();
    }

    public synchronized static void clean(String prefix) {
        if (serialNumberPool.containsKey(prefix)) {
            serialNumberPool.get(prefix).clear();
        }
    }

    /**
     * 清理号池里过期的号
     */
    public synchronized static void cleanExpiredNumber() {
        // 当前时间的年月日
        String now = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.NORM_DATE_PATTERN);
        generateTimes.forEach((prefix, localDateTime) -> {
            String generateTime = LocalDateTimeUtil.format(localDateTime, DatePattern.NORM_DATE_PATTERN);
            if (!now.equals(generateTime)) {
                // 清理号池里该流水号前缀的号
                clean(prefix);
            }
        });
    }
}
