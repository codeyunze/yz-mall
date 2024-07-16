package com.yz.unqid;

import com.yz.unqid.dto.SerialNumberDto;
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
public class UnqidHolder {

    /**
     * 流水号号池
     */
    private static ConcurrentHashMap<String, ConcurrentLinkedQueue<SerialNumberDto>> serialNumberPool = new ConcurrentHashMap<>();

    /**
     * 本批次流水号生成时间
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
}
