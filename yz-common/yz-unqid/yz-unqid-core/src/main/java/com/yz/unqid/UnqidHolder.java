package com.yz.unqid;

import com.yz.unqid.entity.SysUnqid;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yunze
 * @date 2024/7/11 星期四 23:31
 */
public class UnqidHolder {

    private static ConcurrentHashMap<String, LinkedList<SysUnqid>> serialNumbers = new ConcurrentHashMap<>();


}
