package com.yz.common.dto;

/**
 * @ClassName WorkerDTO
 * @Description 雪花ID配置信息
 * @Author 3hgh
 * @Date 2022/11/21 14:12
 * @Version 1.0
 */
public class SnowflakeDTO {

    /**
     * 雪花ID生成器的工作机器ID(0~31)
     */
    private long workerId;

    /**
     * 雪花ID生成器的数据中心ID(0~31)
     */
    private long datacenterId;

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(long datacenterId) {
        this.datacenterId = datacenterId;
    }
}
