package com.yz.mall.base;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Id
 *
 * @author yunze
 * @date 2024/12/10 12:40
 */
public class IdDto implements Serializable {

    private final static long serialVersionUID = 1L;

    @NotNull(message = "id不能为空")
    private Long id;

    public IdDto(Long taskId) {
        this.id = taskId;
    }

    public IdDto(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
