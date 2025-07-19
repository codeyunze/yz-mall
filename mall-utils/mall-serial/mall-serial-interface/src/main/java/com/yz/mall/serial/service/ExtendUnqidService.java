package com.yz.mall.serial.service;

/**
 * @author yunze
 * @date 2024/6/23 星期日 22:49
 */
public interface ExtendUnqidService {

    /**
     * 生成流水号
     *
     * @param prefix       流水号前缀
     * @param numberLength 流水号的序号长度
     * @return 流水号
     */
    String generateNumber(String prefix, Integer numberLength);
}
