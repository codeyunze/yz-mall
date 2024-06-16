package com.yz.tools;

import java.io.Serializable;

/**
 * @author yunze
 * @date 2024/6/10 15:12
 */
public class BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源标识
     */
    private String datasourceId;

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }
}
