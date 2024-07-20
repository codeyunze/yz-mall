package com.yz.unqid.service;

import java.util.List;

/**
 * @author yunze
 * @date 2024/6/23 星期日 22:49
 */
public interface InternalUnqidService {

    /**
     * 生成流水号
     *
     * @param prefix       流水号前缀
     * @param numberLength 流水号的序号长度
     * @return 流水号
     */
    String generateSerialNumber(String prefix, Integer numberLength);

    /**
     * 批量生成流水号
     *
     * @param prefix       流水号前缀
     * @param numberLength 流水号的序号长度
     * @param quantity     生成流水号数量
     * @return 流水号列表
     */
    List<String> generateSerialNumbers(String prefix, Integer numberLength, Integer quantity);

}
