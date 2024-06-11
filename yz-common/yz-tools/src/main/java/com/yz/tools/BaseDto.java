package com.yz.tools;

import java.io.Serializable;

/**
 * @author yunze
 * @date 2024/6/10 15:12
 */
public class BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源
     */
    private String dbSource;

    public String getDbSource() {
        return dbSource;
    }

    public void setDbSource(String dbSource) {
        this.dbSource = dbSource;
    }
}
