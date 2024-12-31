package com.yz.mall.web.common;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Ids
 *
 * @author yunze
 * @date 2024/12/10 12:40
 */
public class IdsDto implements Serializable {

    private final static long serialVersionUID = 1L;

    public IdsDto(List<Long> ids) {
        this.ids = ids;
    }

    public IdsDto() {
    }

    @NotNull(message = "id不能为空")
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}
