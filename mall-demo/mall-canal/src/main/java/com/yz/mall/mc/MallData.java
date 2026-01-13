package com.yz.mall.mc;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author yunze
 * @date 2025/12/2 星期二 23:31
 */
@Slf4j
@Component
public class MallData {


    static int keepAliveTime = 30;             // 非核心线程的空闲存活时长（分钟）
    static int queueCapacity = 1;           // 队列最大长度
    public static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueCapacity);
    static ThreadPoolExecutor threadPool;

    static {
        threadPool = new ThreadPoolExecutor(1, 10, keepAliveTime, TimeUnit.SECONDS, queue);
    }

    @PostConstruct
    public void init() {
        // 创建异步任务添加到线程池
        CompletableFuture.runAsync(() -> {
            // 执行任务
            mysqlSyncEs();
        }, threadPool);
        mysqlSyncEs();
    }

    public void mysqlSyncEs() {
        System.out.println("开始启动canal");
        // 创建链接，后两个参数是用户名和密码，在搭建canal server时候如果指定了那么在这里也要填写
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("127.0.0.1",
                9933), "mall", "", "");
        try {
            connector.connect();
            // 指定订阅的数据库
            // 例如：.*\\..*代表订阅所有；test.*代表订阅test库下面的所有表；test\\.(table1|table2)代表订阅test库下面的table1和table2表；(test1|test2)\\..*代表订阅test1库和test2库下面的所有表
            // connector.subscribe(".*\\..*");
            connector.subscribe("mall.*");
            // 在启动 Canal 客户端时，可以调用 rollback 方法从上次未确认的位置继续消费数据，而不是从头开始消费
//            connector.rollback();
            while (true) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(100);
                // 返回 -1 时，表示在当前请求中没有获取到新的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                // 判断是否为有变化的数据
                if (batchId == -1 || size == 0) {
                    try {
                        // 没有变更，休息一会（根据实际业务情况定）
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 处理数据
                    try {
                        handleData(message.getEntries());
                        // 提交确认
                        connector.ack(batchId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // 处理失败, 回滚数据
                        connector.rollback(batchId);
                    }

                }
            }
        } finally {
            connector.disconnect();
        }
    }

    private void handleData(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            // ROWDATA：表示具体的行数据变更，包括插入（INSERT）、更新（UPDATE）和删除（DELETE）操作,这也是最常用的类型，通过这个类型来捕捉变化的数据进行同步;
            // TRANSACTIONBEGIN：表示一个事务的开始。通常不需要特别处理，除非需要记录事务的开始时间等信息。
            // TRANSACTIONEND：表示一个事务的结束。通常不需要特别处理，除非需要记录事务的结束时间等信息。
            // HEARTBEAT：表示心跳消息，用于保持连接活跃。通常不需要特别处理，除非需要监控连接状态。
            CanalEntry.EntryType entryType = entry.getEntryType();
            // 不是ROWDATA，跳过
            if (entryType != CanalEntry.EntryType.ROWDATA) {
                continue;
            }
            CanalEntry.RowChange rowChage;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }
            // 获取事件类型
            CanalEntry.EventType eventType = rowChage.getEventType();
            // 取出数据
            List<CanalEntry.RowData> rowDatasList = rowChage.getRowDatasList();
            // 遍历数据
            for (CanalEntry.RowData rowData : rowDatasList) {
                if (eventType == CanalEntry.EventType.DELETE) {
                    System.out.println("类型：" + eventType.name());
                    // 删除数据
                    log.info("删除数据: {}", rowData.getBeforeColumnsList());
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    System.out.println("类型：" + eventType.name());
                    // 新增数据
                    log.info("新增数据: {}", rowData.getAfterColumnsList());
                } else if (eventType == CanalEntry.EventType.UPDATE) {
                    System.out.println("类型：" + eventType.name());
                    // 修改数据
                    log.info("修改数据");

                    for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                        log.info(column.getName() + " : " + column.getValue());
                    }
                }
            }
        }
    }
}
