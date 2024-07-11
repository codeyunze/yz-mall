package com.yz.unqid;

import com.yz.unqid.dto.SysUnqidUpdateDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yunze
 * @date 2024/7/11 星期四 23:31
 */
public class UnqidHolder {

    private static List<SysUnqidUpdateDto> addUnqids = new ArrayList<>();

    private static List<SysUnqidUpdateDto> updateUnqids = new ArrayList<>();

    public List<SysUnqidUpdateDto> getAddUnqids() {
        return addUnqids;
    }

    public static void addNewUnqid(SysUnqidUpdateDto dto) {
        addUnqids.add(dto);
    }

}
