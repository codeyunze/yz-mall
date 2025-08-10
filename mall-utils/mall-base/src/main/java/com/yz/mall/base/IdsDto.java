package com.yz.mall.base;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * Ids
 *
 * @author yunze
 * @date 2024/12/10 12:40
 */
public class IdsDto<T> implements Serializable {

    private final static long serialVersionUID = 1L;

    public IdsDto(List<T> ids) {
        this.ids = ids;
    }

    public IdsDto() {
    }

    @NotNull(message = "参数不能为空")
    private List<T> ids;

    public List<T> getIds() {
        return ids;
    }

    public void setIds(List<T> ids) {
        this.ids = ids;
    }
}
