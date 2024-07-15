package com.yz.unqid;

import com.yz.unqid.dto.SerialNumberDto;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

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
    private static ConcurrentHashMap<String, LinkedList<SerialNumberDto>> serialNumbers = new ConcurrentHashMap<>();

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
        LinkedList<SerialNumberDto> list = serialNumbers.get(prefix);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        SerialNumberDto serialNumber = list.get(0);
        list.remove(0);
        return serialNumber;
    }

    /**
     * 获取流水号号池里指定前缀序列号的数量
     *
     * @param prefix 序列号前缀
     * @return 剩余流水号数量
     */
    public static Integer getCount(String prefix) {
        if (!serialNumbers.containsKey(prefix)) {
            return 0;
        }
        return serialNumbers.get(prefix).size();
    }

    /**
     * 补充指定前缀的流水号
     *
     * @param prefix  序列号前缀
     * @param serials 流水号
     */
    public static void add(String prefix, LinkedList<SerialNumberDto> serials) {
        if (serialNumbers.containsKey(prefix)) {
            serialNumbers.get(prefix).addAll(serials);
        } else {
            serialNumbers.put(prefix, serials);
        }

        generateTimes.put(prefix, LocalDateTime.now());
    }

    /**
     * 补充指定前缀的流水号
     * @param prefix 序列号前缀
     * @param serial 流水号
     */
    public static void add(String prefix, SerialNumberDto serial) {
        if (serialNumbers.containsKey(prefix)) {
            serialNumbers.get(prefix).add(serial);
        } else {
            LinkedList<SerialNumberDto> list = new LinkedList<>();
            list.add(serial);
            serialNumbers.put(prefix, list);
        }

        generateTimes.put(prefix, LocalDateTime.now());
    }
}
